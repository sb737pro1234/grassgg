package me.sbpro.grassggquests.quests;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.Messages;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestBossBarManager {
    private final GrassGGQuests plugin;
    private final Map<UUID, BossBar> bars = new HashMap<>();
    private final Map<UUID, Integer> hideTasks = new HashMap<>();

    public QuestBossBarManager(GrassGGQuests plugin) {
        this.plugin = plugin;
    }

    public void showProgress(Player player, QuestDefinition quest, int progress) {
        UUID uuid = player.getUniqueId();
        BossBar bar = bars.computeIfAbsent(uuid, id -> Bukkit.createBossBar(
                Messages.BOSSBAR_TITLE,
                Messages.BOSSBAR_COLOR,
                Messages.BOSSBAR_STYLE));

        bar.setColor(Messages.BOSSBAR_COLOR);
        bar.setStyle(Messages.BOSSBAR_STYLE);
        // The bossbar text is the quest itself (for example, "Bake 32 bread").
        // The filled portion of the bar represents the player's actual progress.
        bar.setTitle(Messages.BOSSBAR_TITLE
                .replace("%quest%", quest.getTitle())
                .replace("%progress%", String.valueOf(progress))
                .replace("%required%", String.valueOf(quest.getRequiredAmount())));
        bar.setProgress(quest.getRequiredAmount() <= 0
                ? 1.0
                : Math.min(1.0, Math.max(0.0, (double) progress / quest.getRequiredAmount())));

        if (!bar.getPlayers().contains(player)) bar.addPlayer(player);
        bar.setVisible(true);

        Integer oldTask = hideTasks.remove(uuid);
        if (oldTask != null) Bukkit.getScheduler().cancelTask(oldTask);

        int task = Bukkit.getScheduler().runTaskLater(plugin, () -> hide(player), Messages.BOSSBAR_DISPLAY_TICKS).getTaskId();
        hideTasks.put(uuid, task);
    }

    public void hide(Player player) {
        UUID uuid = player.getUniqueId();
        Integer task = hideTasks.remove(uuid);
        if (task != null) Bukkit.getScheduler().cancelTask(task);

        BossBar bar = bars.remove(uuid);
        if (bar != null) {
            bar.removePlayer(player);
            bar.setVisible(false);
        }
    }

    public void removeAll() {
        for (BossBar bar : bars.values()) {
            bar.removeAll();
            bar.setVisible(false);
        }
        bars.clear();
        for (Integer task : hideTasks.values()) Bukkit.getScheduler().cancelTask(task);
        hideTasks.clear();
    }
}
