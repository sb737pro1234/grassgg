package me.sbpro.grassggevents;

import me.sbpro.grassggevents.commands.EventCommand;
import me.sbpro.grassggevents.commands.PingCommand;
import me.sbpro.grassggevents.events.sumo.SumoManager;
import me.sbpro.grassggevents.tabcompleters.EventTabCompleter;
import me.sbpro.grassggevents.events.sumo.SumoListener;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGEvents extends JavaPlugin implements Listener {


    private SumoManager sumoManager;


    @Override
    public void onEnable() {
        getLogger().info("Welcome to Events!");
        getLogger().info("GrassGG Starting.");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new SumoListener(this), this);
        getCommand("ping").setExecutor(new PingCommand());


        PluginCommand eventCommand = getCommand("event");

        sumoManager = new SumoManager(this);

        getCommand("event").setExecutor(new EventCommand(this));
        getCommand("event").setTabCompleter(new EventTabCompleter());


    }

    @Override
    public void onDisable() {
        getLogger().info("GrassGGEvents has been disabled!");
    }


    public SumoManager getSumoManager() {
        return sumoManager;
    }








    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("§2§lGRASS.GG §8»§2 Welcome to §fevents§2!");
        getLogger().info("A player has joined the server.");
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        event.message(null);
    }
}
