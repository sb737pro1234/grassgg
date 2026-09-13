package me.sbpro.grassggstaff.punishment;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.config.PunishmentConfig;
import me.sbpro.grassggstaff.database.OffenceRepository;
import me.sbpro.grassggstaff.reason.PunishmentReason;
import me.sbpro.grassggstaff.reason.ReasonManager;
import me.sbpro.grassggstaff.util.DurationParser;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PunishmentManager {

    private final GrassGGStaff plugin;
    private final OffenceRepository repository;
    private final PunishmentConfig config;
    private final ReasonManager reasonManager;
    private final PunishmentCalculator calculator;

    public PunishmentManager(
            GrassGGStaff plugin,
            OffenceRepository repository,
            PunishmentConfig config,
            ReasonManager reasonManager,
            PunishmentCalculator calculator
    ) {
        this.plugin = plugin;
        this.repository = repository;
        this.config = config;
        this.reasonManager = reasonManager;
        this.calculator = calculator;
    }

    /**
     * Issues a new punishment.
     *
     * Any currently active punishment is marked as:
     * active = false
     * removed = true
     * replaced_at = current time
     *
     * The old offence remains in the permanent history.
     */
    public CompletableFuture<OffenceResult> issue(
            UUID playerUuid,
            String playerName,
            PunishmentReason reason,
            UUID staffUuid,
            String staffName
    ) {

        return repository.findHistory(playerUuid)
                .thenCompose(history -> {

                    PunishmentTier tier =
                            config.getTier(reason.tier())
                                    .orElse(null);

                    if (tier == null) {
                        return CompletableFuture.completedFuture(
                                OffenceResult.failure(
                                        "Punishment tier '"
                                                + reason.tier()
                                                + "' does not exist."
                                )
                        );
                    }

                    int offenceNumber =
                            calculator.nextOffenceNumber(
                                    history,
                                    tier.id()
                            );

                    Punishment punishment =
                            calculator.calculate(
                                    tier,
                                    offenceNumber
                            );

                    if (punishment == null) {
                        return CompletableFuture.completedFuture(
                                OffenceResult.failure(
                                        "No punishment is configured for "
                                                + tier.id()
                                                + " offence #"
                                                + offenceNumber
                                )
                        );
                    }

                    Instant startedAt = Instant.now();

                    Instant expiresAt;

                    try {
                        expiresAt = DurationParser.expiry(
                                startedAt,
                                punishment.duration()
                        );
                    } catch (Exception exception) {
                        return CompletableFuture.completedFuture(
                                OffenceResult.failure(
                                        "Invalid punishment duration: "
                                                + punishment.duration()
                                )
                        );
                    }

                    return repository
                            .replaceActivePunishments(
                                    playerUuid,
                                    staffUuid,
                                    staffName,
                                    startedAt
                            )
                            .thenCompose(ignored ->
                                    repository.insertOffence(
                                            playerUuid,
                                            playerName,
                                            reason.id(),
                                            reason.displayName(),
                                            tier.id(),
                                            offenceNumber,
                                            punishment,
                                            startedAt,
                                            expiresAt,
                                            staffUuid,
                                            staffName
                                    )
                            )
                            .thenApply(offenceId -> {

                                OffenceRecord record =
                                        new OffenceRecord(
                                                offenceId,
                                                playerUuid,
                                                playerName,

                                                reason.id(),
                                                reason.displayName(),

                                                tier.id(),
                                                offenceNumber,

                                                punishment.type(),
                                                punishment.duration(),

                                                startedAt,
                                                expiresAt,

                                                true,
                                                false,

                                                null,
                                                null,

                                                staffUuid,
                                                staffName,

                                                null
                                        );

                                return new OffenceResult(
                                        true,
                                        record,
                                        null
                                );
                            });
                });
    }

    /**
     * Returns the complete offence history.
     */
    public CompletableFuture<List<OffenceRecord>> getHistory(
            UUID uuid
    ) {
        return repository.findHistory(uuid);
    }

    /**
     * Returns the current active punishment.
     */
    public CompletableFuture<OffenceRecord> getActivePunishment(
            UUID uuid
    ) {
        return repository.findActivePunishment(uuid);
    }

    /**
     * Removes the current active punishment.
     *
     * The historical record is retained.
     */
    public CompletableFuture<OffenceRecord> removeActivePunishment(
            UUID uuid
    ) {
        return removeActivePunishment(
                uuid,
                new UUID(0L, 0L),
                "Console"
        );
    }

    /**
     * Removes the current active punishment and records
     * who performed the removal.
     */
    public CompletableFuture<OffenceRecord> removeActivePunishment(
            UUID uuid,
            UUID staffUuid,
            String staffName
    ) {

        return repository.findActivePunishment(uuid)
                .thenCompose(current -> {

                    if (current == null) {
                        return CompletableFuture.completedFuture(
                                null
                        );
                    }

                    return repository
                            .removeActivePunishment(
                                    uuid,
                                    staffUuid,
                                    staffName
                            )
                            .thenApply(success -> {

                                if (!success) {
                                    return null;
                                }

                                return new OffenceRecord(
                                        current.offenceId(),
                                        current.playerUuid(),
                                        current.playerName(),

                                        current.reasonId(),
                                        current.reasonName(),

                                        current.tier(),
                                        current.offenceNumber(),

                                        current.punishmentType(),
                                        current.duration(),

                                        current.startedAt(),
                                        current.expiresAt(),

                                        false,
                                        true,

                                        staffName,
                                        Instant.now(),

                                        current.staffUuid(),
                                        current.staffName(),

                                        current.replacedAt()
                                );
                            });
                });
    }

    /**
     * Changes the active punishment to another reason.
     *
     * The offence number stays the same when remaining in the
     * same tier. When moving to another tier, the next number
     * for the new tier is calculated from the player's history.
     */
    public CompletableFuture<OffenceRecord> changeActiveReason(
            UUID playerUuid,
            PunishmentReason newReason,
            UUID staffUuid,
            String staffName
    ) {

        return repository.findHistory(playerUuid)
                .thenCompose(history ->
                        repository.findActivePunishment(playerUuid)
                                .thenCompose(current -> {

                                    if (current == null) {
                                        return CompletableFuture.completedFuture(
                                                null
                                        );
                                    }

                                    PunishmentTier newTier =
                                            config.getTier(
                                                    newReason.tier()
                                            ).orElse(null);

                                    if (newTier == null) {
                                        return CompletableFuture.completedFuture(
                                                null
                                        );
                                    }

                                    int offenceNumber;

                                    if (current.tier()
                                            .equalsIgnoreCase(
                                                    newTier.id()
                                            )) {

                                        offenceNumber =
                                                current.offenceNumber();

                                    } else {

                                        offenceNumber =
                                                history.stream()
                                                        .filter(record ->
                                                                record.offenceId()
                                                                        != current.offenceId()
                                                                        &&
                                                                        record.tier()
                                                                                .equalsIgnoreCase(
                                                                                        newTier.id()
                                                                                )
                                                        )
                                                        .mapToInt(
                                                                OffenceRecord::offenceNumber
                                                        )
                                                        .max()
                                                        .orElse(0)
                                                        + 1;
                                    }

                                    Punishment punishment =
                                            calculator.calculate(
                                                    newTier,
                                                    offenceNumber
                                            );

                                    if (punishment == null) {
                                        return CompletableFuture.completedFuture(
                                                null
                                        );
                                    }

                                    Instant startedAt =
                                            Instant.now();

                                    Instant expiresAt;

                                    try {
                                        expiresAt =
                                                DurationParser.expiry(
                                                        startedAt,
                                                        punishment.duration()
                                                );
                                    } catch (Exception exception) {
                                        return CompletableFuture.completedFuture(
                                                null
                                        );
                                    }

                                    return repository
                                            .updateActivePunishment(
                                                    playerUuid,
                                                    current,
                                                    newReason.id(),
                                                    newReason.displayName(),
                                                    newTier.id(),
                                                    offenceNumber,
                                                    punishment,
                                                    startedAt,
                                                    expiresAt,
                                                    staffUuid,
                                                    staffName
                                            );
                                })
                );
    }

    public String getTierDisplayName(
            String tierId
    ) {

        return config.getTier(tierId)
                .map(PunishmentTier::displayName)
                .orElse(tierId);
    }

    public PunishmentConfig getConfig() {
        return config;
    }

    public ReasonManager getReasonManager() {
        return reasonManager;
    }

    public PunishmentCalculator getCalculator() {
        return calculator;
    }

    public OffenceRepository getRepository() {
        return repository;
    }

    public record OffenceResult(
            boolean success,
            OffenceRecord record,
            String error
    ) {

        public static OffenceResult failure(
                String error
        ) {
            return new OffenceResult(
                    false,
                    null,
                    error
            );
        }
    }

    public CompletableFuture<Boolean> resetPlayerHistory(
            UUID playerUuid
    ) {

        return repository.resetPlayerHistory(
                playerUuid
        ).thenApply(success -> {

            if (success &&
                    plugin.getEnforcementManager() != null) {

                plugin.getEnforcementManager()
                        .invalidate(
                                playerUuid
                        );
            }

            return success;
        });
    }
}