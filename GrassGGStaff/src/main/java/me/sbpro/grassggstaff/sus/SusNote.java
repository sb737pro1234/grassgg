package me.sbpro.grassggstaff.sus;

import java.time.Instant;
import java.util.UUID;

public record SusNote(
        long susId,
        UUID playerUuid,
        String playerName,
        String reason,
        UUID staffUuid,
        String staffName,
        Instant createdAt
) {
}