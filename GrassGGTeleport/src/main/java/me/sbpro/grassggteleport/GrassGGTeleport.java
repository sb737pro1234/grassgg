package me.sbpro.grassggteleport;

import me.sbpro.grassggteleport.command.*;
import me.sbpro.grassggteleport.command.tab.PlayerOnlyTabCompleter;
import me.sbpro.grassggteleport.command.tab.RequestPlayerTabCompleter;
import me.sbpro.grassggteleport.command.tab.TeleportTabCompleter;
import me.sbpro.grassggteleport.gui.ConfirmationMenuListener;
import me.sbpro.grassggteleport.listener.TeleportListener;
import me.sbpro.grassggteleport.request.TeleportRequestManager;
import me.sbpro.grassggteleport.settings.TeleportPreferencesManager;
import me.sbpro.grassggteleport.spectate.SpectateManager;
import me.sbpro.grassggteleport.teleport.TeleportManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGTeleport extends JavaPlugin {

    private static GrassGGTeleport instance;

    private TeleportManager teleportManager;
    private TeleportRequestManager teleportRequestManager;
    private TeleportPreferencesManager teleportPreferencesManager;
    private SpectateManager spectateManager;

    @Override
    public void onEnable() {
        instance = this;

        teleportManager = new TeleportManager(this);

        teleportPreferencesManager =
                new TeleportPreferencesManager(this);

        teleportRequestManager =
                new TeleportRequestManager(
                        this,
                        teleportManager,
                        teleportPreferencesManager
                );

        spectateManager = new SpectateManager();

        if (getCommand("tp") != null) {
            getCommand("tp").setExecutor(
                    new TeleportCommand(this)
            );

            getCommand("tp").setTabCompleter(
                    new TeleportTabCompleter()
            );
        }

        if (getCommand("tpall") != null) {
            getCommand("tpall").setExecutor(
                    new TpallCommand(this)
            );
        }

        if (getCommand("tp2p") != null) {
            getCommand("tp2p").setExecutor(
                    new Tp2pCommand(this)
            );

            getCommand("tp2p").setTabCompleter(
                    new PlayerOnlyTabCompleter()
            );
        }

        if (getCommand("tphere") != null) {
            getCommand("tphere").setExecutor(
                    new TphereCommand(this)
            );

            getCommand("tphere").setTabCompleter(
                    new PlayerOnlyTabCompleter()
            );
        }

        if (getCommand("tpa") != null) {
            getCommand("tpa").setExecutor(
                    new TpaCommand(this)
            );

            getCommand("tpa").setTabCompleter(
                    new RequestPlayerTabCompleter()
            );
        }

        if (getCommand("tpahere") != null) {
            getCommand("tpahere").setExecutor(
                    new TpahereCommand(this)
            );

            getCommand("tpahere").setTabCompleter(
                    new RequestPlayerTabCompleter()
            );
        }

        if (getCommand("tpaccept") != null) {
            getCommand("tpaccept").setExecutor(
                    new TpacceptCommand(this)
            );

            getCommand("tpaccept").setTabCompleter(
                    new RequestPlayerTabCompleter()
            );
        }

        if (getCommand("tpdeny") != null) {
            getCommand("tpdeny").setExecutor(
                    new TpdenyCommand(this)
            );
        }

        if (getCommand("tpacancel") != null) {
            getCommand("tpacancel").setExecutor(
                    new TpacancelCommand(this)
            );
        }

        if (getCommand("tptoggle") != null) {
            getCommand("tptoggle").setExecutor(
                    new TptoggleCommand(this)
            );
        }

        if (getCommand("tpauto") != null) {
            getCommand("tpauto").setExecutor(
                    new TpautoCommand(this)
            );
        }

        if (getCommand("spectate") != null) {
            getCommand("spectate").setExecutor(
                    new SpectateCommand(this)
            );

            getCommand("spectate").setTabCompleter(
                    new PlayerOnlyTabCompleter()
            );
        }

        getServer().getPluginManager().registerEvents(
                new TeleportListener(
                        teleportManager,
                        teleportRequestManager,
                        teleportPreferencesManager,
                        spectateManager
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ConfirmationMenuListener(),
                this
        );

        getLogger().info(
                "GrassGGTeleport has been enabled."
        );
    }

    @Override
    public void onDisable() {
        if (teleportRequestManager != null) {
            teleportRequestManager.shutdown();
        }

        if (teleportPreferencesManager != null) {
            teleportPreferencesManager.shutdown();
        }

        if (spectateManager != null) {
            spectateManager.shutdown();
        }

        getLogger().info(
                "GrassGGTeleport has been disabled."
        );

        instance = null;
    }

    public static GrassGGTeleport getInstance() {
        return instance;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public TeleportRequestManager getTeleportRequestManager() {
        return teleportRequestManager;
    }

    public TeleportPreferencesManager
    getTeleportPreferencesManager() {
        return teleportPreferencesManager;
    }

    public SpectateManager getSpectateManager() {
        return spectateManager;
    }
}