package com.project.evaluate.aspect;


import com.project.evaluate.annotation.RateLimiter;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/29 16:03
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {
    private static final ConcurrentMap<String, com.google.common.util.concurrent.RateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();

    @Pointcut("@annotation(com.project.evaluate.annotation.RateLimiter)")
    public void rateLimit() {
    }

    @Around(value = "rateLimit()")
    public static Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
//        System.out.println("限流切面");
//        获取 RateLimiter 注解
        RateLimiter rateLimiter = AnnotationUtils.findAnnotation(method, RateLimiter.class);
        if (!Objects.isNull(rateLimiter) && rateLimiter.qps() > RateLimiter.no_limit) {
            double qps = rateLimiter.qps();
            if (Objects.isNull(RATE_LIMITER_CACHE.get(method.getName()))) {
//                初始化 QPS
                RATE_LIMITER_CACHE.put(method.getName(), com.google.common.util.concurrent.RateLimiter.create(qps));
            }
            // 尝试获取令牌
            if (!Objects.isNull(RATE_LIMITER_CACHE.get(method.getName())) && !RATE_LIMITER_CACHE.get(method.getName()).tryAcquire(rateLimiter.timeout(), rateLimiter.timeUnit())) {
//                获取不到令牌，则访问频繁
                return new ResponseResult<>(ResultCode.FREQUENT_VISITS);
            }
        }
        return point.proceed();
    }
}
