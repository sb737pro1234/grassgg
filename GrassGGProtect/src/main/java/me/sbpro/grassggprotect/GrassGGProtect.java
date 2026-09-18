package me.sbpro.grassggprotect;

import me.sbpro.grassggprotect.command.GrassGGProtectCommand;
import me.sbpro.grassggprotect.region.RegionManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class GrassGGProtect extends JavaPlugin {

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

        GrassGGProtectCommand command =
                new GrassGGProtectCommand(this);

        if (getCommand("grassggprotect") != null) {
            getCommand("grassggprotect").setExecutor(command);
            getCommand("grassggprotect").setTabCompleter(command);
        }

        getLogger().info("GrassGGProtect enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GrassGGProtect disabled.");
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