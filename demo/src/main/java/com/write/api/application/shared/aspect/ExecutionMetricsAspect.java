package com.write.api.application.shared.aspect;

import com.write.api.application.shared.annotations.TrackExecutionTime;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Locale;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class ExecutionMetricsAspect {

    private static final Duration[] SLOS = {
            Duration.ofMillis(50),
            Duration.ofMillis(100),
            Duration.ofMillis(250),
            Duration.ofMillis(500),
            Duration.ofSeconds(1),
            Duration.ofSeconds(2),
            Duration.ofSeconds(5)
    };

    private final MeterRegistry meterRegistry;

    @Around("@annotation(trackExecutionTime)")
    public Object measure(
            ProceedingJoinPoint joinPoint,
            TrackExecutionTime trackExecutionTime
    ) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        String metricBase = normalizeMetricName(trackExecutionTime.value());

        Timer.Sample timerSample = Timer.start(meterRegistry);
        LongTaskTimer.Sample longTaskSample = longTaskTimer(metricBase, className, methodName).start();

        String outcome = "success";
        String exception = "none";

        Counter.builder(metricBase + ".calls")
                .description("Total executions")
                .tags("class", className, "method", methodName)
                .register(meterRegistry)
                .increment();

        try {
            Object result = joinPoint.proceed();

            Counter.builder(metricBase + ".success")
                    .description("Successful executions")
                    .tags("class", className, "method", methodName)
                    .register(meterRegistry)
                    .increment();

            return result;
        } catch (Throwable ex) {
            outcome = "error";
            exception = ex.getClass().getSimpleName();

            Counter.builder(metricBase + ".errors")
                    .description("Failed executions")
                    .tags("class", className, "method", methodName, "exception", exception)
                    .register(meterRegistry)
                    .increment();

            throw ex;
        } finally {
            Timer timer = Timer.builder(metricBase + ".duration")
                    .description(descriptionOrDefault(trackExecutionTime))
                    .publishPercentileHistogram()
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .serviceLevelObjectives(SLOS)
                    .tags(
                            "class", className,
                            "method", methodName,
                            "outcome", outcome,
                            "exception", exception
                    )
                    .register(meterRegistry);

            timerSample.stop(timer);
            longTaskSample.stop();
        }
    }

    private LongTaskTimer longTaskTimer(String metricBase, String className, String methodName) {
        return LongTaskTimer.builder(metricBase + ".inflight")
                .description("Currently running executions")
                .tags("class", className, "method", methodName)
                .register(meterRegistry);
    }

    private String descriptionOrDefault(TrackExecutionTime annotation) {
        if (annotation.description() == null || annotation.description().isBlank()) {
            return "Execution time";
        }
        return annotation.description();
    }

    private String normalizeMetricName(String value) {
        if (value == null || value.isBlank()) {
            return "application.execution";
        }

        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]", ".");
    }
}