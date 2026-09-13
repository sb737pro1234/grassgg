package me.sbpro.grassggteleport.settings;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TeleportPreferencesManager {

    private static final String TPAUTO_ACTION_BAR =
            "§x§0§0§A§8§F§F§lTPAUTO IS ENABLED";

    private final JavaPlugin plugin;

    private final Map<UUID, Boolean> teleportRequestsDisabled =
            new HashMap<>();

    private final Map<UUID, Boolean> autoAcceptTpa =
            new HashMap<>();

    private BukkitTask actionBarTask;

    public TeleportPreferencesManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startActionBarTask();
    }

    public boolean toggleTeleportRequests(Player player) {
        UUID uuid = player.getUniqueId();

        boolean newValue = !isTeleportRequestsDisabled(player);

        teleportRequestsDisabled.put(uuid, newValue);

        return newValue;
    }

    public boolean isTeleportRequestsDisabled(Player player) {
        return teleportRequestsDisabled.getOrDefault(
                player.getUniqueId(),
                false
        );
    }

    public boolean toggleAutoAcceptTpa(Player player) {
        UUID uuid = player.getUniqueId();

        boolean newValue = !isAutoAcceptTpa(player);

        autoAcceptTpa.put(uuid, newValue);

        return newValue;
    }

    public boolean isAutoAcceptTpa(Player player) {
        return autoAcceptTpa.getOrDefault(
                player.getUniqueId(),
                false
        );
    }

    public boolean shouldAutoAcceptTpa(Player player) {
        return !isTeleportRequestsDisabled(player)
                && isAutoAcceptTpa(player);
    }

    public void cleanup(Player player) {
        UUID uuid = player.getUniqueId();

        teleportRequestsDisabled.remove(uuid);
        autoAcceptTpa.remove(uuid);
    }

    private void startActionBarTask() {
        actionBarTask = plugin.getServer()
                .getScheduler()
                .runTaskTimer(
                        plugin,
                        this::updateActionBars,
                        0L,
                        20L
                );
    }

    private void updateActionBars() {
        for (Player player : plugin.getServer()
                .getOnlinePlayers()) {

            if (!isAutoAcceptTpa(player)) {
                continue;
            }

            player.sendActionBar(TPAUTO_ACTION_BAR);
        }
    }

    public void shutdown() {
        if (actionBarTask != null) {
            actionBarTask.cancel();
            actionBarTask = null;
        }

        teleportRequestsDisabled.clear();
        autoAcceptTpa.clear();
    }
}