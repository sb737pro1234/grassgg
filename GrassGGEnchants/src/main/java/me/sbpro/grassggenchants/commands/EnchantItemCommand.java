package me.sbpro.grassggenchants.commands;

import me.sbpro.grassggenchants.GrassGGEnchants;
import me.sbpro.grassggenchants.Messages;
import me.sbpro.grassggenchants.items.EnchantUpgradeItem;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class EnchantItemCommand implements CommandExecutor, TabCompleter {

    private final GrassGGEnchants plugin;

    public EnchantItemCommand(GrassGGEnchants plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("grassgg.enchant.give")) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 3 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(Messages.PREFIX + "Usage: §f/enchantitem give <player> <item>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        ItemStack item;
        if (args[2].equalsIgnoreCase("enchant_upgrade")) {
            item = EnchantUpgradeItem.create(plugin);
        } else {
            sender.sendMessage(Messages.INVALID_ITEM);
            return true;
        }

        target.getInventory().addItem(item);
        sender.sendMessage(Messages.format(
                Messages.GIVEN,
                "%item%", "Enchant Upgrade",
                "%player%", target.getName()
        ));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("grassgg.enchant.give")) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("give");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .sorted()
                    .toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return List.of("enchant_upgrade");
        }

        return List.of();
    }
}
