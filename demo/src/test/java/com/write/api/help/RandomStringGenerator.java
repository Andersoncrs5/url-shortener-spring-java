package com.write.api.help;

import java.security.SecureRandom;

public class RandomStringGenerator {

    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final SecureRandom RANDOM =
            new SecureRandom();

    public static String random(int length) {

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(
                    CHARS.charAt(
                            RANDOM.nextInt(CHARS.length())
                    )
            );
        }

        return sb.toString();
    }
}