package me.sbpro.grassggwarp;

import me.sbpro.grassggwarp.commands.crates.CratesCommand;
import me.sbpro.grassggwarp.commands.crates.SetCratesCommand;
import me.sbpro.grassggwarp.commands.spawn.SetSpawnCommand;
import me.sbpro.grassggwarp.commands.spawn.SpawnCommand;
import me.sbpro.grassggwarp.commands.warzone.WarzoneCommand;
import me.sbpro.grassggwarp.commands.warzone.SetWarzoneCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGWarp extends JavaPlugin {

    private WarpHelper warpHelper;

    @Override
    public void onEnable() {

        getLogger().info("Grass.GG Warp » Plugin Starting.");

        saveDefaultConfig();

        // Warp Helper
        warpHelper = new WarpHelper(this);

        // Teleport Commands
        getCommand("spawn").setExecutor(new SpawnCommand(warpHelper));
        getCommand("warzone").setExecutor(new WarzoneCommand(warpHelper));
        getCommand("crates").setExecutor(new CratesCommand(warpHelper));

        // Set Warp Commands
        getCommand("setspawn").setExecutor(new SetSpawnCommand(warpHelper));
        getCommand("setwarzone").setExecutor(new SetWarzoneCommand(warpHelper));
        getCommand("setcrates").setExecutor(new SetCratesCommand(warpHelper));

        // Listeners
        getServer().getPluginManager().registerEvents(warpHelper, this);

        getLogger().info("Grass.GG Warp » Plugin Enabled.");
    }

    @Override
    public void onDisable() {

        if (warpHelper != null) {
            warpHelper.shutdown();
        }

        getLogger().info("Grass.GG Warp » Plugin Disabled.");
    }

    public WarpHelper getWarpHelper() {
        return warpHelper;
    }
}