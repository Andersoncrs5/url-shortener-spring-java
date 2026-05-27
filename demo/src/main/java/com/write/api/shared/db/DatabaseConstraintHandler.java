package com.write.api.shared.db;


import com.write.api.application.shared.Result;
import org.springframework.dao.DataIntegrityViolationException;
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
                    400
            );
        }

        if (message.contains("cannot be null")) {

            String column =
                    DatabaseErrorUtils.extractColumn(message);

            return Result.failure(
                    "Required field '" + column + "' is missing",
                    400
            );
        }

        if (message.contains("Data too long")) {

            String column =
                    DatabaseErrorUtils.extractColumn(message);

            return Result.failure(
                    "Field '" + column + "' exceeded the allowed size",
                    400
            );
        }

        return Result.failure(
                "Database integrity error: " + message,
                400
        );
    }
}