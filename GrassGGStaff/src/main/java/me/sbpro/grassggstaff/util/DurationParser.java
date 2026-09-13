package me.sbpro.grassggstaff.util;

import java.time.*;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationParser {

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)(m|h|d|w|mo|y)$", Pattern.CASE_INSENSITIVE);

    private DurationParser() {}

    public static long parse(String input) {
        if (input == null || input.equalsIgnoreCase("permanent")) return -1;

        Matcher matcher = PATTERN.matcher(input.trim());
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid duration: " + input);

        long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2).toLowerCase(Locale.ROOT);

        return switch (unit) {
            case "m" -> Math.multiplyExact(amount, 60L);
            case "h" -> Math.multiplyExact(amount, 3600L);
            case "d" -> Math.multiplyExact(amount, 86400L);
            case "w" -> Math.multiplyExact(amount, 604800L);
            case "mo" -> Math.multiplyExact(amount, 0L); // Calendar duration; use expiry().
            case "y" -> Math.multiplyExact(amount, 0L);  // Calendar duration; use expiry().
            default -> throw new IllegalArgumentException("Unknown duration unit: " + unit);
        };
    }

    public static Instant expiry(Instant start, String input) {
        if (input == null || input.equalsIgnoreCase("permanent")) return null;

        Matcher matcher = PATTERN.matcher(input.trim());
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid duration: " + input);

        long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2).toLowerCase(Locale.ROOT);

        ZonedDateTime date = start.atZone(ZoneOffset.UTC);
        ZonedDateTime result = switch (unit) {
            case "m" -> date.plusMinutes(amount);
            case "h" -> date.plusHours(amount);
            case "d" -> date.plusDays(amount);
            case "w" -> date.plusWeeks(amount);
            case "mo" -> date.plusMonths(amount);
            case "y" -> date.plusYears(amount);
            default -> throw new IllegalArgumentException("Unknown duration unit: " + unit);
        };

        return result.toInstant();
    }
}
