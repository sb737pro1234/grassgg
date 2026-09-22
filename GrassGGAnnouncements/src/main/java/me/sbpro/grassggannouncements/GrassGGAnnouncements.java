package me.sbpro.grassggannouncements;

import me.sbpro.grassggannouncements.command.AnnounceCommand;
import me.sbpro.grassggannouncements.command.ChatLockManager;
import me.sbpro.grassggannouncements.command.ContentCommand;
import me.sbpro.grassggannouncements.command.LivestreamCommand;
import me.sbpro.grassggannouncements.listener.ChatLockListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGAnnouncements extends JavaPlugin {

    private AnnouncementManager announcementManager;
    private ChatLockManager chatLockManager;

    @Override
    public void onEnable() {
        announcementManager = new AnnouncementManager(this);
        chatLockManager = new ChatLockManager();

        getCommand("announce").setExecutor(new AnnounceCommand(chatLockManager));
        getCommand("livestream").setExecutor(new LivestreamCommand(chatLockManager));
        getCommand("content").setExecutor(new ContentCommand(chatLockManager));

        getServer().getPluginManager().registerEvents(
                new ChatLockListener(chatLockManager),
                this
        );

        announcementManager.start();

        getLogger().info("GrassGGAnnouncements has been enabled!");
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