package me.sbpro.grassggcoins.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class AmountFormatter {

    private static final String[] SUFFIXES = {"", "k", "m", "b", "t", "q"};
    private static final ThreadLocal<DecimalFormat> FORMAT = ThreadLocal.withInitial(() ->
            new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ROOT)));

    private AmountFormatter() {
    }

    public static String format(long amount) {
        if (amount < 1000) {
            return Long.toString(amount);
        }

        double value = amount;
        int index = 0;

        while (value >= 1000.0 && index < SUFFIXES.length - 1) {
            value /= 1000.0;
            index++;
        }

        return FORMAT.get().format(value) + SUFFIXES[index];
    }
}
