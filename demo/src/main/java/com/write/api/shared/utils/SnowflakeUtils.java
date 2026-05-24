package com.write.api.shared.utils;

public final class SnowflakeUtils {

    private static final long EPOCH = 1700000000000L;

    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long TIMESTAMP_SHIFT =
            WORKER_ID_BITS + SEQUENCE_BITS;

    private SnowflakeUtils() {}

    public static boolean isValid(Long id) {

        if (id == null || id <= 0) {
            return false;
        }

        long timestamp = (id >> TIMESTAMP_SHIFT) + EPOCH;

        long now = System.currentTimeMillis();

        return timestamp <= now + 60_000;
    }

    public static long extractTimestamp(Long id) {
        return (id >> TIMESTAMP_SHIFT) + EPOCH;
    }
}