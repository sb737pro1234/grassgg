package me.sbpro.grassgg.commands.clear;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClearCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /clear
        if (args.length == 0) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou must specify a player to use this command.");
                return true;
            }

            if (!player.hasPermission("grassgg.clear.self")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            openConfirmMenu(player, player);
            return true;
        }

        // /clear <player>
        if (args.length == 1) {

            if (!sender.hasPermission("grassgg.clear.others")) {
                sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou do not have permission to clear other players.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }

            // Console (or any non-player sender) can't open an inventory, so
            // skip the confirm menu and clear the target's inventory directly.
            if (!(sender instanceof Player player)) {
                clearDirectly(sender, target);
                return true;
            }

            openConfirmMenu(player, target);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /clear [player]");
        return true;
    }

    private void clearDirectly(CommandSender sender, Player target) {

        target.getInventory().clear();

        sender.sendMessage(
                "§x§E§F§4§4§4§4§lADMIN §8» §2" + target.getName() + "§f's inventory has been cleared."
        );

        target.sendMessage(
                "§x§E§F§4§4§4§4§lADMIN §8» §2Your §finventory has been cleared by §2" + sender.getName() + "§f."
        );
    }

    private void openConfirmMenu(Player player, Player target) {

        Inventory clear = Bukkit.createInventory(
                new ClearConfirmHolder(target),
                27,
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Clear Confirm"
        );

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack chest = new ItemStack(Material.CHEST);
        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        // Confirm
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");

        if (target.equals(player)) {
            confirmMeta.setLore(List.of(
                    "§aThis will clear all §fyour§a items."
            ));
        } else {
            confirmMeta.setLore(List.of(
                    "§aThis will clear all §f" + target.getName() + "§a's items."
            ));
        }

        confirm.setItemMeta(confirmMeta);

        // Chest
        ItemMeta chestMeta = chest.getItemMeta();

        if (target.equals(player)) {
            chestMeta.setDisplayName("§fClicking confirm will clear §2your§f inventory.");
        } else {
            chestMeta.setDisplayName(
                    "§fClicking confirm will clear §2" + target.getName() + "§f's inventory."
            );
        }

        chest.setItemMeta(chestMeta);

        // Cancel
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§cCancel");
        cancel.setItemMeta(cancelMeta);

        clear.setItem(10, cancel);
        clear.setItem(13, chest);
        clear.setItem(16, confirm);

        player.openInventory(clear);
    }
}