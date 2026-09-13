package me.sbpro.grassggstaff.punishment;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PunishmentEnforcementManager {

    private final GrassGGStaff plugin;

    private final Map<UUID, OffenceRecord> cache =
            new ConcurrentHashMap<>();

    private int syncTaskId = -1;

    public PunishmentEnforcementManager(
            GrassGGStaff plugin
    ) {
        this.plugin = plugin;
    }

    public void start() {

        if (!plugin.getConfig()
                .getBoolean(
                        "synchronisation.enabled",
                        true
                )) {

            return;
        }

        long seconds =
                Math.max(
                        1L,
                        plugin.getConfig()
                                .getLong(
                                        "synchronisation.poll-seconds",
                                        3L
                                )
                );

        syncTaskId =
                Bukkit.getScheduler()
                        .runTaskTimerAsynchronously(
                                plugin,
                                this::synchronise,
                                20L,
                                seconds * 20L
                        )
                        .getTaskId();
    }

    public void restart() {

        stop();

        start();
    }

    private void stop() {

        if (syncTaskId != -1) {

            Bukkit.getScheduler()
                    .cancelTask(
                            syncTaskId
                    );

            syncTaskId = -1;
        }
    }

    private void synchronise() {

        /*
         * Mark expired punishments inactive in the database.
         */
        plugin.getOffenceRepository()
                .expirePunishments()
                .exceptionally(
                        exception -> {

                            plugin.getLogger().warning(
                                    "Failed to expire punishments: "
                                            + exception.getMessage()
                            );

                            return null;
                        }
                );

        if (!plugin.getConfig()
                .getBoolean(
                        "synchronisation.refresh-online-players",
                        true
                )) {

            return;
        }

        /*
         * Refresh every player currently connected to this backend.
         */
        for (Player player :
                Bukkit.getOnlinePlayers()) {

            refresh(
                    player.getUniqueId()
            );
        }
    }

    /**
     * Refresh a player's punishment from MySQL.
     *
     * If another backend has created or changed a punishment,
     * the player is immediately enforced here.
     */
    public void refresh(
            UUID uuid
    ) {

        plugin.getOffenceRepository()
                .findActivePunishment(uuid)
                .whenComplete(
                        (current, throwable) -> {

                            if (throwable != null) {

                                plugin.getLogger().warning(
                                        "Unable to refresh punishment for "
                                                + uuid
                                                + ": "
                                                + throwable.getMessage()
                                );

                                return;
                            }

                            OffenceRecord previous =
                                    cache.get(
                                            uuid
                                    );

                            if (current == null) {

                                cache.remove(
                                        uuid
                                );

                            } else {

                                cache.put(
                                        uuid,
                                        current
                                );
                            }

                            if (current == null ||
                                    !current.enforceable()) {

                                return;
                            }

                            /*
                             * New or changed punishment.
                             */
                            if (hasChanged(
                                    previous,
                                    current
                            )) {

                                Bukkit.getScheduler()
                                        .runTask(
                                                plugin,
                                                () -> enforce(
                                                        current
                                                )
                                        );
                            }
                        }
                );
    }

    private boolean hasChanged(
            OffenceRecord previous,
            OffenceRecord current
    ) {

        if (previous == null) {
            return true;
        }

        return previous.offenceId()
                != current.offenceId()

                || previous.offenceNumber()
                != current.offenceNumber()

                || previous.punishmentType()
                != current.punishmentType()

                || !Objects.equals(
                previous.reasonId(),
                current.reasonId()
        )

                || !Objects.equals(
                previous.expiresAt(),
                current.expiresAt()
        );
    }

    /**
     * Enforce a punishment on an already-connected player.
     */
    private void enforce(
            OffenceRecord punishment
    ) {

        Player player =
                Bukkit.getPlayer(
                        punishment.playerUuid()
                );

        if (player == null ||
                !player.isOnline()) {

            return;
        }

        if (!punishment.enforceable()) {
            return;
        }

        if (punishment.punishmentType()
                == PunishmentType.BAN) {

            player.kickPlayer(
                    Messages.banScreen(
                            punishment
                    )
            );

        } else if (
                punishment.punishmentType()
                        == PunishmentType.MUTE
        ) {

            /*
             * Mutes are allowed to remain connected.
             *
             * We only kick here so the player gets the new
             * punishment screen immediately. The chat listener
             * will enforce the mute after reconnecting.
             */
            player.kickPlayer(
                    Messages.muteScreen(
                            punishment
                    )
            );
        }
    }

    /**
     * Put a punishment directly into the local cache.
     */
    public void set(
            OffenceRecord record
    ) {

        if (record == null) {
            return;
        }

        cache.put(
                record.playerUuid(),
                record
        );
    }

    /**
     * Remove a player from the local cache.
     */
    public void invalidate(
            UUID uuid
    ) {

        cache.remove(
                uuid
        );
    }

    /**
     * Return the cached active punishment.
     */
    public OffenceRecord getActive(
            UUID uuid
    ) {

        OffenceRecord record =
                cache.get(
                        uuid
                );

        if (!isActive(record)) {
            return null;
        }

        return record;
    }

    /**
     * Compatibility method for code that wants the cached
     * punishment without enforcement.
     */
    public OffenceRecord cacheWithoutEnforcement(
            UUID uuid
    ) {

        return cache.get(
                uuid
        );
    }

    public boolean isActive(
            OffenceRecord record
    ) {

        if (record == null) {
            return false;
        }

        if (!record.active() ||
                record.removed() ||
                record.replaced()) {

            return false;
        }

        Instant expiresAt =
                record.expiresAt();

        return expiresAt == null ||
                expiresAt.isAfter(
                        Instant.now()
                );
    }

    public boolean isMuted(
            UUID uuid
    ) {

        OffenceRecord record =
                getActive(uuid);

        return record != null &&
                record.punishmentType()
                        == PunishmentType.MUTE;
    }

    public boolean isBanned(
            UUID uuid
    ) {

        OffenceRecord record =
                getActive(uuid);

        return record != null &&
                record.punishmentType()
                        == PunishmentType.BAN;
    }

    public void shutdown() {

        stop();

        cache.clear();
    }
}