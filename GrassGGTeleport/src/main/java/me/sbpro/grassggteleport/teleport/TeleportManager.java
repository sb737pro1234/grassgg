package me.sbpro.grassggteleport.teleport;

import me.sbpro.grassggteleport.message.Messages;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public final class TeleportManager {

    private final JavaPlugin plugin;
    private final Map<UUID, TeleportTask> activeTeleports = new HashMap<>();

    public TeleportManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void instantTeleport(
            Player player,
            Location destination,
            Player messagePlayer
    ) {
        cancelTeleport(player, false);

        if (!player.isOnline()) {
            return;
        }

        player.teleport(destination);

        if (messagePlayer != null && messagePlayer.isOnline()) {
            messagePlayer.sendMessage(
                    Messages.teleportSuccessful()
            );

            messagePlayer.playSound(
                    messagePlayer.getLocation(),
                    Sound.ENTITY_ENDERMAN_TELEPORT,
                    1.0f,
                    1.0f
            );
        }
    }

    public void startTeleport(
            Player player,
            Location destination
    ) {
        startTeleport(
                player,
                destination,
                player
        );
    }

    public void startTeleport(
            Player player,
            Supplier<Location> destinationSupplier
    ) {
        startTeleport(
                player,
                destinationSupplier,
                player
        );
    }

    public void startTeleport(
            Player player,
            Location destination,
            Player messagePlayer
    ) {
        cancelTeleport(player, false);

        int seconds = TeleportTimeResolver.getTeleportTime(player);

        if (seconds <= 0) {
            completeTeleport(
                    player,
                    destination,
                    messagePlayer
            );
            return;
        }

        TeleportTask teleportTask = new TeleportTask(
                player,
                () -> destination.clone(),
                seconds,
                messagePlayer
        );

        activeTeleports.put(
                player.getUniqueId(),
                teleportTask
        );

        teleportTask.start();
    }

    public void startTeleport(
            Player player,
            Supplier<Location> destinationSupplier,
            Player messagePlayer
    ) {
        cancelTeleport(player, false);

        int seconds = TeleportTimeResolver.getTeleportTime(player);

        if (seconds <= 0) {
            Location destination = destinationSupplier.get();

            if (destination != null) {
                completeTeleport(
                        player,
                        destination,
                        messagePlayer
                );
            }

            return;
        }

        TeleportTask teleportTask = new TeleportTask(
                player,
                destinationSupplier,
                seconds,
                messagePlayer
        );

        activeTeleports.put(
                player.getUniqueId(),
                teleportTask
        );

        teleportTask.start();
    }

    public void requestAdminTeleport(
            Player sender,
            Player teleportedPlayer,
            Location destination,
            String action
    ) {
        me.sbpro.grassggteleport.gui.ConfirmationMenu.open(
                sender,
                action,
                new AdminTeleportAction(
                        this,
                        teleportedPlayer,
                        destination,
                        sender
                )
        );
    }

    public void requestAdminTeleportMultiple(
            Player sender,
            java.util.List<Player> players,
            Location destination,
            String action
    ) {
        me.sbpro.grassggteleport.gui.ConfirmationMenu.open(
                sender,
                action,
                new MultiTeleportAction(
                        this,
                        players,
                        destination,
                        sender
                )
        );
    }

    public void cancelTeleport(
            Player player,
            boolean sendMessage
    ) {
        UUID uuid = player.getUniqueId();

        TeleportTask teleportTask = activeTeleports.remove(uuid);

        if (teleportTask == null) {
            return;
        }

        teleportTask.cancel();

        if (sendMessage && player.isOnline()) {
            player.sendMessage(
                    Messages.teleportCancelled()
            );
        }
    }

    public void cancelForMovement(Player player) {
        UUID uuid = player.getUniqueId();

        TeleportTask teleportTask = activeTeleports.remove(uuid);

        if (teleportTask == null) {
            return;
        }

        teleportTask.cancel();

        if (teleportTask.messagePlayer != null
                && teleportTask.messagePlayer.isOnline()) {
            teleportTask.messagePlayer.sendMessage(
                    Messages.teleportCancelledMovement()
            );
        }
    }

    public void cancelForDamage(Player player) {
        UUID uuid = player.getUniqueId();

        TeleportTask teleportTask = activeTeleports.remove(uuid);

        if (teleportTask == null) {
            return;
        }

        teleportTask.cancel();

        if (teleportTask.messagePlayer != null
                && teleportTask.messagePlayer.isOnline()) {
            teleportTask.messagePlayer.sendMessage(
                    Messages.teleportCancelled()
            );
        }
    }

    public void cancelForWorldChange(Player player) {
        UUID uuid = player.getUniqueId();

        TeleportTask teleportTask = activeTeleports.remove(uuid);

        if (teleportTask == null) {
            return;
        }

        teleportTask.cancel();

        if (teleportTask.messagePlayer != null
                && teleportTask.messagePlayer.isOnline()) {
            teleportTask.messagePlayer.sendMessage(
                    Messages.teleportCancelled()
            );
        }
    }

    public void cleanup(Player player) {
        UUID uuid = player.getUniqueId();

        TeleportTask teleportTask = activeTeleports.remove(uuid);

        if (teleportTask != null) {
            teleportTask.cancel();
        }
    }

    public boolean hasActiveTeleport(Player player) {
        return activeTeleports.containsKey(
                player.getUniqueId()
        );
    }

    private void completeTeleport(
            Player player,
            Location destination,
            Player messagePlayer
    ) {
        if (!player.isOnline()) {
            cleanup(player);
            return;
        }

        player.teleport(destination);

        if (messagePlayer != null
                && messagePlayer.isOnline()) {
            messagePlayer.sendMessage(
                    Messages.teleportSuccessful()
            );

            messagePlayer.playSound(
                    messagePlayer.getLocation(),
                    Sound.ENTITY_ENDERMAN_TELEPORT,
                    1.0f,
                    1.0f
            );
        }
    }

    private final class TeleportTask {

        private final Player player;
        private final Supplier<Location> destinationSupplier;
        private final Player messagePlayer;

        private int remainingSeconds;
        private BukkitTask task;

        private TeleportTask(
                Player player,
                Supplier<Location> destinationSupplier,
                int remainingSeconds,
                Player messagePlayer
        ) {
            this.player = player;
            this.destinationSupplier = destinationSupplier;
            this.remainingSeconds = remainingSeconds;
            this.messagePlayer = messagePlayer;
        }

        private void start() {
            task = plugin.getServer()
                    .getScheduler()
                    .runTaskTimer(
                            plugin,
                            this::tick,
                            0L,
                            20L
                    );
        }

        private void tick() {
            if (!player.isOnline()) {
                cleanup(player);
                return;
            }

            if (remainingSeconds <= 0) {
                finish();
                return;
            }

            if (messagePlayer != null
                    && messagePlayer.isOnline()) {
                messagePlayer.sendActionBar(
                        Messages.teleportingIn(
                                remainingSeconds
                        )
                );

                messagePlayer.playSound(
                        messagePlayer.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_HAT,
                        0.5f,
                        1.5f
                );
            }

            remainingSeconds--;
        }

        private void finish() {
            activeTeleports.remove(
                    player.getUniqueId()
            );

            if (task != null) {
                task.cancel();
            }

            Location destination =
                    destinationSupplier.get();

            if (destination == null) {
                if (messagePlayer != null
                        && messagePlayer.isOnline()) {
                    messagePlayer.sendMessage(
                            Messages.teleportCancelled()
                    );
                }

                return;
            }

            completeTeleport(
                    player,
                    destination,
                    messagePlayer
            );
        }

        private void cancel() {
            if (task != null) {
                task.cancel();
            }
        }
    }
}