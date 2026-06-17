package com.read.api.domain.utils;

public class SnowflakeIdGenerator {

    private static final long EPOCH = 1700000000000L;

    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long maxWorkerId = ~(-1L << WORKER_ID_BITS);
    private static final long maxSequence = ~(-1L << SEQUENCE_BITS);

    private static final long TIMESTAMP_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;

    private final long workerId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Invalid workerId");
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << (WORKER_ID_BITS + SEQUENCE_BITS))
                | (workerId << SEQUENCE_BITS)
                | sequence;
    }

    public static boolean isValid(Long id) {

        if (id == null || id <= 0) {
            return false;
        }

        long timestamp = (id >> TIMESTAMP_SHIFT) + EPOCH;

        long now = System.currentTimeMillis();

        return timestamp <= now + 60_000;
    }

}