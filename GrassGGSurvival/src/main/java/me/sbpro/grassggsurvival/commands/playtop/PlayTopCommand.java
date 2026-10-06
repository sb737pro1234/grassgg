package me.sbpro.grassggsurvival.commands.playtop;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTopCommand implements CommandExecutor {

    private final GrassGGSurvival plugin;

    public PlayTopCommand(GrassGGSurvival plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("grassgg.admin")) {
                sender.sendMessage(PlayTopMessages.NO_PERMISSION);
                return true;
            }

            // Values in PlayTopMessages.java are compiled into the plugin, so after
            // recompiling, /playtop reload refreshes the cached leaderboard so the
            // menu is rebuilt with the new values and up-to-date playtimes.
            PlayTopMenu.refresh(plugin);
            sender.sendMessage(PlayTopMessages.RELOADED);
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(PlayTopMessages.PLAYER_ONLY);
            return true;
        }

        PlayTopMenu.open(plugin, player);
        return true;
    }
}
