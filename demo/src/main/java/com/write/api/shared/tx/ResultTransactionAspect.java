package com.write.api.shared.tx;

import com.write.api.application.shared.Result;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

@Aspect
@Component
@RequiredArgsConstructor
public class ResultTransactionAspect {

    private final PlatformTransactionManager transactionManager;

    @Around("@annotation(resultTransaction)")
    public Object around(ProceedingJoinPoint joinPoint, ResultTransaction resultTransaction) throws Throwable {
        TransactionStatus status = transactionManager.getTransaction(
                TransactionDefinition.withDefaults()
        );

        try {
            Object returned = joinPoint.proceed();

            if (returned instanceof Result<?> result && result.isFailure()) {
                transactionManager.rollback(status);
                return returned;
            }

            transactionManager.commit(status);
            return returned;

        } catch (Throwable ex) {
            transactionManager.rollback(status);
            throw ex;
        }
    }
}
