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

        // /feed
        if (args.length == 0) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou must specify a player to use this command.");
                return true;
            }

            if (!player.hasPermission("grassgg.feed")) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou do not have permission to use this command.");
                return true;
            }

            openConfirmMenu(player, player);
            return true;
        }

        // /feed <player>
        if (args.length == 1) {

            if (!sender.hasPermission("grassgg.feed.others")) {
                sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou do not have permission to feed other players.");
                return true;
            }
// 
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }

            // Console (or any non-player sender) can't open an inventory, so
            // skip the confirm menu and feed the target directly.
            if (!(sender instanceof Player player)) {
                feedDirectly(sender, target);
                return true;
            }

            openConfirmMenu(player, target);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /feed [player]");
        return true;
    }

    private void feedDirectly(CommandSender sender, Player target) {

        target.setFoodLevel(20);
        target.setSaturation(20);

        sender.sendMessage(
                "§x§E§F§4§4§4§4§lADMIN §8» §2" + target.getName() + "§f's hunger has been restored."
        );

        target.sendMessage(
                "§x§E§F§4§4§4§4§lADMIN §8» §2Your §fhunger has been restored by §2" + sender.getName() + "§f."
        );
    }

    private void openConfirmMenu(Player player, Player target) {

        Inventory feed = Bukkit.createInventory(
                new FeedConfirmHolder(target),
                27,
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Feed Confirm"
        );

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack food = new ItemStack(Material.COOKED_BEEF);
        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        // Confirm
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");

        if (target.equals(player)) {
            confirmMeta.setLore(List.of(
                    "§aThis will restore §fyour§a hunger."
            ));
        } else {
            confirmMeta.setLore(List.of(
                    "§aThis will restore §f" + target.getName() + "§a's hunger."
            ));
        }

        confirm.setItemMeta(confirmMeta);

        // Food
        ItemMeta foodMeta = food.getItemMeta();

        if (target.equals(player)) {
            foodMeta.setDisplayName("§fClicking confirm will restore §2your§f hunger.");
        } else {
            foodMeta.setDisplayName(
                    "§fClicking confirm will restore §2" + target.getName() + "§f's hunger."
            );
        }

        food.setItemMeta(foodMeta);

        // Cancel
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§cCancel");
        cancel.setItemMeta(cancelMeta);

        feed.setItem(10, cancel);
        feed.setItem(13, food);
        feed.setItem(16, confirm);

        player.openInventory(feed);
    }
}