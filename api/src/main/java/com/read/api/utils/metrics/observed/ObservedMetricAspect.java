package com.read.api.utils.metrics.observed;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ObservedMetricAspect {

    MeterRegistry meterRegistry;

    @Around("@annotation(metric)")
    public Object observe(
            ProceedingJoinPoint joinPoint,
            ObservedMetric metric
    ) throws Throwable {

        String name = metric.value();

        Counter.builder(name + ".calls")
                .register(meterRegistry)
                .increment();

        Timer.Sample sample = Timer.start(meterRegistry);

        try {

            Object result = joinPoint.proceed();

            Counter.builder(name + ".success")
                    .register(meterRegistry)
                    .increment();

            return result;

        } catch (Exception ex) {

            Counter.builder(name + ".errors")
                    .register(meterRegistry)
                    .increment();

            throw ex;

        } finally {

            sample.stop(
                    Timer.builder(name + ".duration")
                            .publishPercentiles(
                                    0.50,
                                    0.90,
                                    0.95,
                                    0.99
                            )
                            .register(meterRegistry)
            );
        }
    }
}