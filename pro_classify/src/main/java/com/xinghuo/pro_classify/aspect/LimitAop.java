package com.xinghuo.pro_classify.aspect;

import com.google.common.util.concurrent.RateLimiter;
import com.xinghuo.pro_classify.annotation.Limit;
import com.xinghuo.pro_classify.constants.RedisConstants;
import com.xinghuo.pro_classify.exception.BizException;
import com.xinghuo.pro_classify.exception.BizExceptionEnum;
import com.xinghuo.pro_classify.properties.LimitProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class LimitAop {
    // 全局限流map（仍使用本地内存，因为RateLimiter对象无法序列化到Redis）
    private final ConcurrentHashMap<String, RateLimiter> limitMap = new ConcurrentHashMap<>();

    // IP限流map（仍使用本地内存，因为RateLimiter对象无法序列化）
    private final ConcurrentHashMap<String, RateLimiter> ipLimitMap = new ConcurrentHashMap<>();

    private final RedisTemplate<String, Object> redisTemplate;

    private final LimitProperties limitProperties;

    public LimitAop(RedisTemplate<String, Object> redisTemplate, LimitProperties limitProperties) {
        this.redisTemplate = redisTemplate;
        this.limitProperties = limitProperties;
    }

    @Around("@annotation(com.xinghuo.pro_classify.annotation.Limit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Limit limit = method.getAnnotation(Limit.class);

        // 获取请求的IP地址
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = getClientIp(request);

        // 检查黑名单
        if (isBlacklisted(ip)) {
            log.warn("IP {} 在黑名单中，拒绝访问", ip);
            throw new BizException(BizExceptionEnum.IP_BLOCKED);
        }

        // IP级别限流
        if (checkIpRateLimit(ip)) {
            addToBlackList(ip);
            log.warn("IP {} 请求频率过高，已加入黑名单", ip);
            throw new BizException(BizExceptionEnum.IP_BLOCKED);
        }

        // 全局限流逻辑
        if (limit != null) {
            String key = limit.key();
            RateLimiter rateLimiter;

            // 动态绑定 permitsPerSecond
            double permitsPerSecond = limit.permitsPerSecond() == -1
                    ? limitProperties.getPermitsPerSecond()
                    : limit.permitsPerSecond();

            // 动态绑定 timeout
            long timeout = limit.timeout() == -1
                    ? limitProperties.getTimeout()
                    : limit.timeout();

            if (!limitMap.containsKey(key)) {
                rateLimiter = RateLimiter.create(permitsPerSecond);
                limitMap.put(key, rateLimiter);
                log.info("新建了令牌桶={}，容量={}", key, permitsPerSecond);
            }
            rateLimiter = limitMap.get(key);

            boolean acquire = rateLimiter.tryAcquire(timeout, limit.timeunit());
            if (!acquire) {
                log.debug("令牌桶={}，获取令牌失败", key);
                throw new BizException(BizExceptionEnum.REQUEST_FREQUENCY_OVER_LIMIT);
            }
        }

        return joinPoint.proceed();
    }

    // 获取客户端真实IP
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    // 检查IP是否在黑名单中
    private boolean isBlacklisted(String ip) {
        String key = RedisConstants.BLACKLIST_PREFIX + ip;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 将IP加入黑名单
    private void addToBlackList(String ip) {
        String key = RedisConstants.BLACKLIST_PREFIX + ip;
        redisTemplate.opsForValue().set(key, "blocked", limitProperties.getBlacklistDuration(), TimeUnit.SECONDS);
    }

    // 检查IP请求频率
    private boolean checkIpRateLimit(String ip) {
        String key = RedisConstants.IP_REQUEST_COUNT_PREFIX + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            // 第一次请求，设置1秒过期
            redisTemplate.expire(key, 1, TimeUnit.SECONDS);
        }
        // 如果1秒内请求超过限制，返回true
        return count > limitProperties.getIpPermitsPerSecond();
    }
}