package me.sbpro.grassggmissions.utils;

public class ProgressBarUtils {

    private ProgressBarUtils() {
    }

    public static String getProgressBar(int current, int max) {

        int bars = 20;

        double percent = (double) current / max;

        int completed = (int) (percent * bars);

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < bars; i++) {

            if (i < completed) {
                builder.append("§a█");
            } else {
                builder.append("§7█");
            }

        }

        return builder.toString();

    }

}