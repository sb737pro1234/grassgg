package me.sbpro.grassggmissions.commands;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.managers.ResetManager;
import me.sbpro.grassggmissions.menus.DailyMissionsMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DailyMissionsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /dailymissions
        if (args.length == 0) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cOnly players can use this command.");
                return true;
            }

            DailyMissionsMenu.open(player);
            return true;
        }

        // /dailymissions reset <player>
        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {

            if (!sender.hasPermission("grassgg.dailymissions.admin")) {
                sender.sendMessage("§cYou do not have permission.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);

            if (target == null) {
                sender.sendMessage("§cThat player is not online.");
                return true;
            }

            ResetManager.reset(target);

            sender.sendMessage("§aSuccessfully reset daily missions for §e" + target.getName() + "§a.");

            target.sendMessage("§aYour daily missions have been reset.");

            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("complete")) {

            if (!sender.hasPermission("grassgg.dailymissions.admin")) {
                sender.sendMessage("§cYou do not have permission.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);

            if (target == null) {
                sender.sendMessage("§cThat player is not online.");
                return true;
            }

            int slot;

            try {
                slot = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage("§cSlot must be between 1 and 4.");
                return true;
            }

            if (slot < 1 || slot > 4) {
                sender.sendMessage("§cSlot must be between 1 and 4.");
                return true;
            }

            GrassGGMissions.getInstance()
                    .getPlayerMissionManager()
                    .completeMission(
                            target.getUniqueId(),
                            slot,
                            target
                    );

            sender.sendMessage("§aCompleted mission " + slot + " for §e" + target.getName());

            return true;
        }
        sender.sendMessage("§cUsage:");
        sender.sendMessage("§7/dailymissions");
        sender.sendMessage("§7/dailymissions reset <player>");

        return true;
    }

}