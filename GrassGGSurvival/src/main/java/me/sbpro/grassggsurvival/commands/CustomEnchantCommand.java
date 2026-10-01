package me.sbpro.grassggsurvival.commands;

import me.sbpro.grassggsurvival.enchants.AutoSmeltEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantCommand implements CommandExecutor {

    private final AutoSmeltEnchant autoSmelt;

    public CustomEnchantCommand(AutoSmeltEnchant autoSmelt) {
        this.autoSmelt = autoSmelt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("grassgg.admin")) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }

        if (args.length != 3 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /customenchant give <player> <enchant>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        ItemStack item = target.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage("§cThat player is not holding an item.");
            return true;
        }

        switch (args[2].toLowerCase()) {

            case "autosmelt":

                if (!item.getType().name().endsWith("_PICKAXE")) {
                    sender.sendMessage("§cAutoSmelt can only be applied to pickaxes.");
                    return true;
                }

                if (autoSmelt.hasEnchant(item)) {
                    sender.sendMessage("§cThat item already has AutoSmelt.");
                    return true;
                }

                autoSmelt.apply(item);

                sender.sendMessage("§aApplied §6AutoSmelt §ato " + target.getName() + "'s pickaxe.");
                target.sendMessage("§aYour pickaxe has been enchanted with §6AutoSmelt§a.");

                break;

            default:
                sender.sendMessage("§cUnknown enchant.");
                break;
        }

        return true;
    }
}