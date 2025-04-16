package com.xinghuo.pro_classify.schedule;

import com.xinghuo.pro_classify.constants.RedisConstants;
import com.xinghuo.pro_classify.service.LitterImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ScheduleTask {
    private final LitterImageService litterImageService;

    private final RedisTemplate<String, Object> redisTemplate;

    public ScheduleTask(LitterImageService litterImageService, RedisTemplate<String, Object> redisTemplate) {
        this.litterImageService = litterImageService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 定时任务
     * 每天0点清除无用的图片
     * 并将具有反馈的图片打包成数据集上传至python服务端进行复训练
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void removeUselessImage() throws Exception {
        String lockValue = UUID.randomUUID().toString();

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(RedisConstants.LOCK_KEY, lockValue, RedisConstants.LOCK_TIMEOUT, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(locked)) {
            try {
                log.info("获取分布式锁成功，开始执行定时任务...");

                log.info("执行定时任务: 开始删除无用图片");
                litterImageService.removeUselessImage();
                log.info("执行定时任务: 删除无用图片成功");

                log.info("执行定时任务: 开始打包复训练训练集, 并发送置python服务端");
                litterImageService.zipDatasetAndRetrain();
                log.info("执行定时任务: 打包复训练训练集, 并发送置python服务端成功");
            } finally {
                if (lockValue.equals(redisTemplate.opsForValue().get(RedisConstants.LOCK_KEY))) {
                    redisTemplate.delete(RedisConstants.LOCK_KEY);
                    log.info("定时任务完成，释放分布式锁");
                }
            }
        } else {
            log.info("未获取到分布式锁，跳过定时任务");
        }
    }
}