package me.sbpro.grassggsurvival.commands.baltop;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalTopCommand implements CommandExecutor {

    private final GrassGGSurvival plugin;

    public BalTopCommand(GrassGGSurvival plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("grassgg.admin")) {
                sender.sendMessage(BalTopMessages.NO_PERMISSION);
                return true;
            }

            // Values in BalTopMessages.java are compiled into the plugin, so this
            // reload method is intentionally used to recreate formatting objects.
            // After recompiling the plugin, /baltop reload applies the new values.
            BalTopMessages.createMoneyFormatter();
            BalTopMenu.refresh(plugin);
            sender.sendMessage(BalTopMessages.RELOADED);
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(BalTopMessages.PLAYER_ONLY);
            return true;
        }

        BalTopMenu.open(plugin, player);
        return true;
    }
}
