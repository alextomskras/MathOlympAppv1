package com.dreamer.matholympappv1.utils;

public class StringIntegerConverter {
    public static int stringToInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            // Handle the case where the string is not a valid integer
            e.printStackTrace();
            return 0; // Or some other default value
        }
    }

    public static String intToString(int i) {
        return String.valueOf(i);
    }
}
