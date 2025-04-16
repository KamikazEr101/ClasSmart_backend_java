package com.xinghuo.pro_classify.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Limit {
    /**
     * 资源的key,唯一
     * 作用：不同的接口，不同的流量控制
     */
    String key() default "";

    /**
     * 最多的访问限制次数，-1 表示使用 LimitProperties 中的默认值
     */
    double permitsPerSecond() default -1;

    /**
     * 获取令牌最大等待时间，-1 表示使用 LimitProperties 中的默认值
     */
    long timeout() default -1;

    /**
     * 获取令牌最大等待时间单位，默认:毫秒
     */
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;
}