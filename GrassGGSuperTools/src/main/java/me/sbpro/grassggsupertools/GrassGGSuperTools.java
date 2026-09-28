package me.sbpro.grassggsupertools;

import me.sbpro.grassggsupertools.commands.SuperToolsCommand;
import me.sbpro.grassggsupertools.listeners.SuperToolListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGSuperTools extends JavaPlugin {

    @Override
    public void onEnable() {
        SuperToolsCommand command = new SuperToolsCommand(this);
        getCommand("supertools").setExecutor(command);
        getCommand("supertools").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new SuperToolListener(this), this);

        getLogger().info("GrassGGSuperTools enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GrassGGSuperTools disabled.");
    }
}
