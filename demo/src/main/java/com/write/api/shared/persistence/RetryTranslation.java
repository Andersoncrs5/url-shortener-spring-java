package com.write.api.shared.persistence;

import lombok.RequiredArgsConstructor;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RetryTranslation {

    public <T> T execute(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException e) {
            throw DatabaseRetryTranslator.translate(e);
        }
    }

    public void execute(Runnable action) {
        try {
            action.run();
        } catch (DataAccessException e) {
            throw DatabaseRetryTranslator.translate(e);
        }
    }
}
