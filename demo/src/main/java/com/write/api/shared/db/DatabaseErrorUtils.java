package com.write.api.shared.db;

public final class DatabaseErrorUtils {

    private DatabaseErrorUtils() {
    }

    public static String extractColumn(String message) {
        if (message == null) {
            return "unknown";
        }

        int firstQuote = message.indexOf('\'');
        int secondQuote = message.indexOf('\'', firstQuote + 1);

        if (firstQuote == -1 || secondQuote == -1) {
            return "unknown";
        }

        return message.substring(firstQuote + 1, secondQuote);
    }
}
