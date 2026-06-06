package com.write.api.shared.persistence;

import com.write.api.core.domain.exception.TransientDatabaseException;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

public final class DatabaseRetryTranslator {

    private DatabaseRetryTranslator() {}

    public static RuntimeException translate(DataAccessException e) {
        Throwable cause = e.getCause();

        if (cause instanceof SQLException sql) {
            int errorCode = sql.getErrorCode();
            String state = sql.getSQLState();

            if (errorCode == 1213 || errorCode == 1205 || "08S01".equals(state)) {
                return new TransientDatabaseException("Transient database error", e);
            }
        }

        return e;
    }
}