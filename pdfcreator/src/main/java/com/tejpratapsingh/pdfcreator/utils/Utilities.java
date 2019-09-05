package com.tejpratapsingh.pdfcreator.utils;

public class Utilities {
    /**
     * Generate random number between two numbers
     *
     * @param lower from number
     * @param upper to number
     * @return a random number
     */
    public static int generateRandomNumber(int lower, int upper) {
        return (int) (Math.random() * (upper + 1 - lower)) + lower;
    }
}
