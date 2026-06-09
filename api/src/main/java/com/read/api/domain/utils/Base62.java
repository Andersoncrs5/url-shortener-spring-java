package com.read.api.domain.utils;

public final class Base62 {

    private static final String CHARACTERS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int BASE = CHARACTERS.length();

    private Base62() {}

    public static String encode(long value) {

        if (value == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();

        while (value > 0) {

            int remainder = (int) (value % BASE);

            sb.append(CHARACTERS.charAt(remainder));

            value /= BASE;
        }

        return sb.reverse().toString();
    }

    public static long decode(String value) {

        long result = 0;

        for (char c : value.toCharArray()) {
            result = result * BASE + CHARACTERS.indexOf(c);
        }

        return result;
    }
}
