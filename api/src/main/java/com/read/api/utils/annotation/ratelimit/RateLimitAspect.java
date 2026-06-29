package com.read.api.utils.annotation.ratelimit;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimiterRegistry registry;

    @Around("@annotation(rateLimited)")
    public Object execute(
            ProceedingJoinPoint joinPoint,
            RateLimited rateLimited
    ) throws Throwable {

        RateLimiter limiter =
                registry.rateLimiter(rateLimited.value());

        if (!limiter.acquirePermission()) {

            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded"
            );
        }

        return joinPoint.proceed();
    }
}
