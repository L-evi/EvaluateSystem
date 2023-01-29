package com.project.evaluate.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    int no_limit = 0;

    /**
     * qps：Queries-per-second， 每秒查询率，QPS = req/sec = 请求数/秒
     */
    @AliasFor("qps")
    double value() default no_limit;

    @AliasFor("value")
    double qps() default no_limit;

    /**
     * 超时时间
     */
    int timeout() default 0;

    /**
     * 超时时间时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
