package me.sbpro.grassgg.commands.kill;

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

public class KillCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /kill
        if (args.length == 0) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou must specify a player to use this command.");
                return true;
            }

            openConfirmMenu(player, player);
            return true;
        }

        // /kill <player>
        if (args.length == 1) {

            if (!sender.hasPermission("grassgg.kill.others")) {
                sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou do not have permission to kill other players.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }

            // Console (or any non-player sender) can't open an inventory, so
            // skip the confirm menu and kill the target directly.
            if (!(sender instanceof Player player)) {
                killDirectly(sender, target);
                return true;
            }

            openConfirmMenu(player, target);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /kill [player]");
        return true;
    }

    private void killDirectly(CommandSender sender, Player target) {

        target.setHealth(0);

        sender.sendMessage(
                "§x§E§F§4§4§4§4§lADMIN §8» §2" + target.getName() + "§f has been killed."
        );

        target.sendMessage(
                "§x§E§F§4§4§4§4§lADMIN §8» §2You §fhave been killed by §2" + sender.getName() + "§f."
        );
    }

    private void openConfirmMenu(Player player, Player target) {

        Inventory death = Bukkit.createInventory(
                new KillConfirmHolder(target),
                27,
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Kill Confirm"
        );

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack skull = new ItemStack(Material.SKELETON_SKULL);
        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        // Confirm
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");

        if (target.equals(player)) {
            confirmMeta.setLore(List.of(
                    "§aThis will kill §fyou§2.",
                    "§aStaff will not refund any items lost."

            ));
        } else {
            confirmMeta.setLore(List.of(
                    "§aThis will kill §f" + target.getName() + "§a."
            ));
        }

        confirm.setItemMeta(confirmMeta);

        // Skull
        ItemMeta skullMeta = skull.getItemMeta();

        if (target.equals(player)) {
            skullMeta.setDisplayName("§fClicking confirm will kill §2you§f.");
        } else {
            skullMeta.setDisplayName(
                    "§fClicking confirm will kill §2" + target.getName() + "§f."
            );
        }

        skull.setItemMeta(skullMeta);

        // Cancel
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§cCancel");
        cancel.setItemMeta(cancelMeta);

        death.setItem(10, cancel);
        death.setItem(13, skull);
        death.setItem(16, confirm);

        player.openInventory(death);
    }
}