package me.sbpro.grassggsurvival.settings;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatToggleCommand implements CommandExecutor {

    private final GrassGGSurvival plugin;

    public ChatToggleCommand(GrassGGSurvival plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§2§lGRASS.GG §8» §cOnly players can use this command.");
            return true;
        }

        if (plugin.getChatToggleManager().isChatDisabled(player.getUniqueId())) {
            plugin.getChatToggleManager().enableChat(player.getUniqueId());
            player.sendMessage("§2§lGRASS.GG §8» §2Chat visilblity has been §a§lenabled§2.");
        } else {
            plugin.getChatToggleManager().disableChat(player.getUniqueId());
            player.sendMessage("§2§lGRASS.GG §8» §2Chat visilblity has been §c§ldisabled§2.");
        }

        return true;
    }
}