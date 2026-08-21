package com.stp.stPlatform.utils;

import java.util.Random;

public class OtpUtils {
    public static String generateOTP() {
        int otpLength = 6;
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < otpLength; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}