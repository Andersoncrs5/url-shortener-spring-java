package com.read.api.infrastructure.persistence.shared;

import com.read.api.domain.exceptions.TransientDatabaseException;
import com.mongodb.MongoException;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoSocketException;
import org.springframework.dao.TransientDataAccessException;

public final class MongoRetryTranslator {

    private MongoRetryTranslator() {}

    public static RuntimeException translate(RuntimeException e) {
        if (e instanceof TransientDataAccessException) {
            return new TransientDatabaseException("Transient database error", e);
        }

        Throwable cause = e.getCause();
        if (cause instanceof MongoException) {
            if (cause instanceof MongoExecutionTimeoutException || cause instanceof MongoSocketException) {
                return new TransientDatabaseException("Transient MongoDB error", e);
            }

            int errorCode = ((MongoException) cause).getCode();
            if (errorCode == 11201 || errorCode == 11600 || errorCode == 262) {
                return new TransientDatabaseException("Transient MongoDB cluster error", e);
            }
        }

        return e;
    }
}