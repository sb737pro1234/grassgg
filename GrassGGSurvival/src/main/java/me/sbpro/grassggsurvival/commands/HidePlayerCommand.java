package me.sbpro.grassggsurvival.commands;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HidePlayerCommand implements CommandExecutor, TabCompleter {

    private final GrassGGSurvival plugin;

    public HidePlayerCommand(GrassGGSurvival plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Must be a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§2§lGRASS.GG §8» §cOnly players can use this command.");
            return true;
        }

        boolean isCurrentlyHiding = plugin.isHidingPlayers(player.getUniqueId());

        if (isCurrentlyHiding) {
            // --- SHOW all players ---
            plugin.setHidingPlayers(player.getUniqueId(), false);

            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                player.showPlayer(plugin, other);
            }

            player.sendMessage("§2§lGRASS.GG §8» §2✔ Players are now §a§lshown §2.");
        } else {
            // --- HIDE all players ---
            plugin.setHidingPlayers(player.getUniqueId(), true);

            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.equals(player)) continue;
                player.hidePlayer(plugin, other);
            }

            player.sendMessage("§2§lGRASS.GG §8» §2✔ Players are now §c§lhidden §2.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No arguments needed for this command
        return Collections.emptyList();
    }
}