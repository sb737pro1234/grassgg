package me.sbpro.grassggmissions.managers;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.missions.PlayerMission;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {

    private static final Map<UUID, BossBar> BOSS_BARS = new HashMap<>();
    private static final Map<UUID, BukkitTask> REMOVE_TASKS = new HashMap<>();

    private BossBarManager() {
    }

    public static void show(Player player, PlayerMission mission) {

        UUID uuid = player.getUniqueId();

        BossBar bossBar = BOSS_BARS.get(uuid);

        if (bossBar == null) {

            bossBar = Bukkit.createBossBar(
                    "",
                    BarColor.GREEN,
                    BarStyle.SOLID
            );

            bossBar.addPlayer(player);

            BOSS_BARS.put(uuid, bossBar);

        }
        bossBar.setColor(getColor(mission));

        bossBar.setTitle(
                "§2§lDaily Mission §8» §f"
                        + mission.getMission().getDisplayName()
                        + " §8("
                        + mission.getProgressString()
                        + ")"
        );

        bossBar.setProgress(Math.min(1.0, Math.max(0.0, mission.getProgressPercent())));

        BukkitTask oldTask = REMOVE_TASKS.remove(uuid);

        if (oldTask != null) {
            oldTask.cancel();
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(
                GrassGGMissions.getInstance(),
                () -> remove(player),
                100L
        );

        REMOVE_TASKS.put(uuid, task);

    }

    public static void remove(Player player) {

        UUID uuid = player.getUniqueId();

        BossBar bossBar = BOSS_BARS.remove(uuid);

        if (bossBar != null) {
            bossBar.removeAll();
        }

        BukkitTask task = REMOVE_TASKS.remove(uuid);

        if (task != null) {
            task.cancel();
        }

    }

    private static BarColor getColor(PlayerMission mission) {

        return switch (mission.getMission().getCategory()) {

            case MINING -> BarColor.BLUE;

            case WOODCUTTING -> BarColor.GREEN;

            case COMBAT -> BarColor.RED;

            case FARMING -> BarColor.YELLOW;

            case FISHING -> BarColor.PURPLE;

            case CRAFTING -> BarColor.WHITE;

        };

    }

}