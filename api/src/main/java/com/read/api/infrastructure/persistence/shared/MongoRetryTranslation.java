package com.read.api.infrastructure.persistence.shared;

import org.springframework.stereotype.Component;
import java.util.function.Supplier;

@Component
public class MongoRetryTranslation {

    public <T> T execute(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            throw MongoRetryTranslator.translate(e);
        }
    }

    public void execute(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            throw MongoRetryTranslator.translate(e);
        }
    }
}