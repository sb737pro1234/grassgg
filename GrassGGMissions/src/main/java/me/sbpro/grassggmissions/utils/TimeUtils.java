package me.sbpro.grassggmissions.utils;

public class TimeUtils {

    private TimeUtils() {
    }

    public static String format(long millis) {

        long seconds = millis / 1000;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        return hours + "h " + minutes + "m " + seconds + "s";
    }

}