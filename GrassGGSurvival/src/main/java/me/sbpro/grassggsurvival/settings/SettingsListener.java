package me.sbpro.grassggsurvival.settings;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SettingsListener implements Listener {

    private final GrassGGSurvival plugin;

    public SettingsListener(GrassGGSurvival plugin) {
        this.plugin = plugin;
    }

}