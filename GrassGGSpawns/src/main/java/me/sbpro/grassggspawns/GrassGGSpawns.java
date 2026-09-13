package me.sbpro.grassggspawns;

import me.sbpro.grassggspawns.command.GrassGGSpawnsCommand;
import me.sbpro.grassggspawns.region.RegionManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class GrassGGSpawns extends JavaPlugin {

    private File regionsFile;
    private FileConfiguration regionsConfig;

    private RegionManager regionManager;

    @Override
    public void onEnable() {

        loadRegions();

        regionManager = new RegionManager(this);
        regionManager.loadRegions();

        getServer().getPluginManager().registerEvents(
                new SpawnProtectionListener(this),
                this
        );

        GrassGGSpawnsCommand command =
                new GrassGGSpawnsCommand(this);

        if (getCommand("grassggspawns") != null) {
            getCommand("grassggspawns").setExecutor(command);
            getCommand("grassggspawns").setTabCompleter(command);
        }

        getLogger().info("GrassGGSpawns enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GrassGGSpawns disabled.");
    }

    public void loadRegions() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        regionsFile = new File(
                getDataFolder(),
                "regions.yml"
        );

        if (!regionsFile.exists()) {
            saveResource("regions.yml", false);
        }

        regionsConfig =
                YamlConfiguration.loadConfiguration(regionsFile);
    }

    public void reloadPlugin() {

        loadRegions();
        regionManager.loadRegions();
    }

    public FileConfiguration getRegionsConfig() {
        return regionsConfig;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }
}