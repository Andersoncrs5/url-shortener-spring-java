package com.write.api.application.shared.aspect;

import com.write.api.application.shared.annotations.TrackExecutionTime;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ExecutionTimeAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(trackExecutionTime)")
    public Object measure(
            ProceedingJoinPoint joinPoint,
            TrackExecutionTime trackExecutionTime
    ) throws Throwable {

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(
                    Timer.builder(trackExecutionTime.value())
                            .register(meterRegistry)
            );
        }
    }
}