package me.sbpro.grassggmissions;

import me.sbpro.grassggmissions.commands.DailyMissionsCommand;
import me.sbpro.grassggmissions.listeners.*;
import me.sbpro.grassggmissions.managers.EconomyManager;
import me.sbpro.grassggmissions.managers.PlayerDataManager;
import me.sbpro.grassggmissions.managers.PlayerMissionManager;
import me.sbpro.grassggmissions.missions.MissionRegistry;
import org.bukkit.plugin.java.JavaPlugin;


public final class GrassGGMissions extends JavaPlugin {

    private static GrassGGMissions instance;

    private PlayerDataManager playerDataManager;
    private PlayerMissionManager playerMissionManager;

    @Override
    public void onEnable() {

        instance = this;

        playerDataManager = new PlayerDataManager(this);
        playerMissionManager = new PlayerMissionManager(this);

        MissionRegistry.registerMissions();

        // Commands
        getCommand("dailymissions").setExecutor(new DailyMissionsCommand());

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new CraftProgressListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new FarmingListener(), this);
        getServer().getPluginManager().registerEvents(new WoodcuttingListener(), this);
        getServer().getPluginManager().registerEvents(new AnimalListener(), this);
        getServer().getPluginManager().registerEvents(new FishingListener(), this);

        if (!EconomyManager.setup()) {
            getLogger().severe("Vault economy not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("GrassGGMissions Enabled!");
    }

    @Override
    public void onDisable() {

        playerDataManager.save();

    }

    public static GrassGGMissions getInstance() {
        return instance;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public PlayerMissionManager getPlayerMissionManager() {
        return playerMissionManager;
    }

}