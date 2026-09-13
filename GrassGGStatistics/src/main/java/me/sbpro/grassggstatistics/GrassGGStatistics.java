package me.sbpro.grassggstatistics;

import me.sbpro.grassggstatistics.stats.StatisticsCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGStatistics extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("statistics").setExecutor(new StatisticsCommand(this));
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
