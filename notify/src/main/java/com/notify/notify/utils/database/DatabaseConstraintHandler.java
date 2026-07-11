package com.notify.notify.utils.database;

import com.notify.notify.globals.classes.result.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

public final class DatabaseConstraintHandler {

    private DatabaseConstraintHandler() {
    }

    public static <T> Result<T> handle(
            DataIntegrityViolationException e
    ) {

        String message =
                e.getMostSpecificCause().getMessage();

        if (message == null) {
            return Result.failure(
                    "Database integrity error",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (message.contains("cannot be null")) {

            String column =
                    DatabaseErrorUtils.extractColumn(message);

            return Result.failure(
                    "Required field '" + column + "' is missing",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (message.contains("Data too long")) {

            String column =
                    DatabaseErrorUtils.extractColumn(message);

            return Result.failure(
                    "Field '" + column + "' exceeded the allowed size",
                    HttpStatus.BAD_REQUEST
            );
        }

        return Result.failure(
                "Database integrity error: " + message,
                HttpStatus.BAD_REQUEST
        );
    }
}
