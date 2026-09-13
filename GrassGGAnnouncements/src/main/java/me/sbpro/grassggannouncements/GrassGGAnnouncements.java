package me.sbpro.grassggannouncements;

import me.sbpro.grassggannouncements.command.AnnounceCommand;
import me.sbpro.grassggannouncements.command.ChatLockManager;
import me.sbpro.grassggannouncements.listener.ChatLockListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGAnnouncements extends JavaPlugin {

    private AnnouncementManager announcementManager;

    @Override
    public void onEnable() {
        announcementManager = new AnnouncementManager(this);

        getCommand("announcements").setExecutor(new AnnouncementsCommand(this));

        announcementManager.start();

        getLogger().info("GrassGGAnnouncements has been enabled!");

        ChatLockManager chatLockManager = new ChatLockManager();

        getCommand("announce").setExecutor(
                new AnnounceCommand(chatLockManager)
        );

        getServer().getPluginManager().registerEvents(
                new ChatLockListener(chatLockManager),
                this
        );
    }

    @Override
    public void onDisable() {
        if (announcementManager != null) {
            announcementManager.stop();
        }

        getLogger().info("GrassGGAnnouncements has been disabled!");
    }

    public AnnouncementManager getAnnouncementManager() {
        return announcementManager;
    }
}