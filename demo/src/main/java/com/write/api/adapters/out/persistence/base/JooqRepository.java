package com.write.api.adapters.out.persistence.base;

import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.shared.persistence.RetryTranslation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class JooqRepository {

    @Autowired
    SnowflakeIdGenerator idGen;

    @Autowired
    DSLContext dsl;

    @Autowired
    RetryTranslation retryTranslator;

    protected <T> T execute(Supplier<T> supplier) {
        return retryTranslator.execute(supplier);
    }

    protected void execute(Runnable runnable) {
        retryTranslator.execute(runnable);
    }
}