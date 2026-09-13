package me.sbpro.grassggannouncements;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementManager {

    private final GrassGGAnnouncements plugin;

    private final LegacyComponentSerializer serializer =
            LegacyComponentSerializer.builder()
                    .character('&')
                    .hexColors()
                    .build();

    private List<String> announcements = new ArrayList<>();
    private int interval;
    private int currentAnnouncement;

    private BukkitTask task;

    private File announcementsFile;
    private FileConfiguration announcementsConfig;

    public AnnouncementManager(GrassGGAnnouncements plugin) {
        this.plugin = plugin;

        createAnnouncementsFile();
        load();
    }

    private void createAnnouncementsFile() {
        announcementsFile = new File(plugin.getDataFolder(), "announcements.yml");

        if (!announcementsFile.exists()) {
            plugin.saveResource("announcements.yml", false);
        }

        announcementsConfig = YamlConfiguration.loadConfiguration(announcementsFile);
    }

    public void load() {
        announcementsConfig =
                YamlConfiguration.loadConfiguration(announcementsFile);

        interval = announcementsConfig.getInt("interval", 900);

        if (interval < 1) {
            plugin.getLogger().warning(
                    "The announcement interval must be at least 1 second."
            );

            interval = 900;
        }

        announcements = announcementsConfig.getStringList("announcements");

        currentAnnouncement = 0;

        plugin.getLogger().info(
                "Loaded " + announcements.size() + " announcement(s)."
        );

        plugin.getLogger().info(
                "Announcement interval: " + interval + " seconds."
        );
    }

    public void start() {
        stop();

        if (announcements.isEmpty()) {
            plugin.getLogger().warning(
                    "No announcements have been configured."
            );
            return;
        }

        long intervalTicks = interval * 20L;

        /*
         * The first announcement is deliberately delayed by the
         * configured interval.
         *
         * Example:
         * interval: 900
         *
         * Server starts
         *     ↓
         * 15 minutes
         *     ↓
         * Announcement 1
         *     ↓
         * 15 minutes
         *     ↓
         * Announcement 2
         */

        task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::sendNextAnnouncement,
                intervalTicks,
                intervalTicks
        );
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void reload() {
        stop();

        load();

        start();
    }

    private void sendNextAnnouncement() {
        if (announcements.isEmpty()) {
            return;
        }

        if (currentAnnouncement >= announcements.size()) {
            currentAnnouncement = 0;
        }

        String message = announcements.get(currentAnnouncement);

        Component component = serializer.deserialize(message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(component);
        }

        currentAnnouncement++;

        if (currentAnnouncement >= announcements.size()) {
            currentAnnouncement = 0;
        }
    }

    public int getInterval() {
        return interval;
    }

    public int getAnnouncementCount() {
        return announcements.size();
    }
}