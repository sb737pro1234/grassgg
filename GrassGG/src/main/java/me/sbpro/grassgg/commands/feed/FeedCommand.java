package me.sbpro.grassgg.commands.feed;

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

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou must be a player to use this command!");
            return true;
        }

        // /feed <player>
        if (args.length == 1) {

            if (!player.hasPermission("grassgg.feed.others")) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou don't have permission to feed other players.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cThat player is not online.");
                return true;
            }

            Inventory feed = Bukkit.createInventory(
                    new FeedConfirmHolder(target),
                    27,
                    ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Feed Confirm"
            );

            createFeedMenu(player, target, feed);

            player.openInventory(feed);
            return true;
        }

        // /feed
        if (!player.hasPermission("grassgg.feed")) {
            player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou don't have permission to feed yourself.");
            return true;
        }

        Player target = player;

        Inventory feed = Bukkit.createInventory(
                new FeedConfirmHolder(target),
                27,
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Feed Confirm"
        );

        createFeedMenu(player, target, feed);

        player.openInventory(feed);

        return true;
    }

    private void createFeedMenu(Player player, Player target, Inventory feed) {

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack food = new ItemStack(Material.COOKED_BEEF);
        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        // Confirm
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");

        if (target.equals(player)) {
            confirmMeta.setLore(List.of("§fClicking confirm will feed §cyou."));
        } else {
            confirmMeta.setLore(List.of("§fClicking confirm will feed §c" + target.getName() + "."));
        }

        confirm.setItemMeta(confirmMeta);

        // Food
        ItemMeta foodMeta = food.getItemMeta();

        if (target.equals(player)) {
            foodMeta.setDisplayName("§fClicking confirm will feed you.");
        } else {
            foodMeta.setDisplayName("§fClicking confirm will feed " + target.getName() + ".");
        }

        food.setItemMeta(foodMeta);

        // Cancel
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§cCancel");
        cancel.setItemMeta(cancelMeta);

        feed.setItem(10, cancel);
        feed.setItem(13, food);
        feed.setItem(16, confirm);
    }
}