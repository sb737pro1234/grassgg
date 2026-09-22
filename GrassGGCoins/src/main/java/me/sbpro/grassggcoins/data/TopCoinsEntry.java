package me.sbpro.grassggcoins.data;

import java.util.UUID;

public record TopCoinsEntry(
        UUID uuid,
        String playerName,
        long coins
) {
}
