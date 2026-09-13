package me.sbpro.grassggstaff.punishment;

import java.time.Instant;
import java.util.UUID;

public record OffenceRecord(
        long offenceId,
        UUID playerUuid,
        String playerName,

        String reasonId,
        String reasonName,

        String tier,
        int offenceNumber,

        PunishmentType punishmentType,
        String duration,

        Instant startedAt,
        Instant expiresAt,

        boolean active,
        boolean removed,

        String removedBy,
        Instant removedAt,

        UUID staffUuid,
        String staffName,

        Instant replacedAt
) {

    /**
     * Whether this offence has been replaced by another punishment.
     */
    public boolean replaced() {
        return replacedAt != null;
    }

    /**
     * Whether this punishment has an expiry time.
     */
    public boolean temporary() {
        return expiresAt != null;
    }

    /**
     * Whether the punishment has expired.
     */
    public boolean expired() {
        return expiresAt != null &&
                expiresAt.isBefore(Instant.now());
    }

    /**
     * Whether this offence is currently enforceable.
     */
    public boolean enforceable() {
        return active &&
                !removed &&
                !replaced() &&
                !expired();
    }
}