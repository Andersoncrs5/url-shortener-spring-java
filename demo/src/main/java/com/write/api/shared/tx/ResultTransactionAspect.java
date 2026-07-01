package com.write.api.shared.tx;

import com.write.api.application.shared.Result;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Aspect
@Component
@RequiredArgsConstructor
public class ResultTransactionAspect {

    private final PlatformTransactionManager transactionManager;

    @Around("@annotation(resultTransaction)")
    public Object around(
            ProceedingJoinPoint joinPoint,
            ResultTransaction resultTransaction
    ) throws Throwable {

        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();

        definition.setReadOnly(resultTransaction.readOnly());
        definition.setPropagationBehavior(resultTransaction.propagation().value());
        definition.setIsolationLevel(resultTransaction.isolation().value());
        definition.setTimeout(resultTransaction.timeout());

        TransactionStatus status = transactionManager.getTransaction(definition);

        try {

            Object returned = joinPoint.proceed();

            if (returned instanceof Result<?> result && result.isFailure()) {

                if (status.isNewTransaction()) {
                    transactionManager.rollback(status);
                } else {
                    status.setRollbackOnly();
                }

                return returned;
            }

            if (status.isNewTransaction()) {
                transactionManager.commit(status);
            }

            return returned;

        } catch (Throwable ex) {

            if (status.isNewTransaction()) {
                transactionManager.rollback(status);
            } else {
                status.setRollbackOnly();
            }

            throw ex;
        }
    }
}