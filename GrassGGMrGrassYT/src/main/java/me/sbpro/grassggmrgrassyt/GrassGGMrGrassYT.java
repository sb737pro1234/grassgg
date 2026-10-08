package me.sbpro.grassggmrgrassyt;

import me.sbpro.grassggmrgrassyt.commands.MrGrassYTCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGMrGrassYT extends JavaPlugin {

    @Override
    public void onEnable() {
        MrGrassYTCommand command = new MrGrassYTCommand(this);

        getCommand("mrgrassyt").setExecutor(command);
        getCommand("mrgrassyt").setTabCompleter(command);

        getLogger().info("MrGrassYT's special plugin has been loaded.");
    }
}
