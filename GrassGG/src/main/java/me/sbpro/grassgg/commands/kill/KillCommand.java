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

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou must be a player to use this command!");
            return true;
        }

        Player target = player;

        // /kill <player>
        if (args.length == 1) {

            if (!player.hasPermission("grassgg.kill.others")) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou don't have permission to kill other players.");
                return true;
            }

            target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cThat player is not online.");
                return true;
            }
        }

        Inventory death = Bukkit.createInventory(new KillConfirmHolder(target), 27,
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Kill Confirm");

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack skull = new ItemStack(Material.SKELETON_SKULL);
        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");
        if (target.equals(player)) {
            confirmMeta.setLore(List.of(
                    "§aStaff will §c§lnot §arefund any items lost."
            ));
        } else {
            confirmMeta.setLore(List.of(
                    "§aThis will kill §f" + target.getName() + "§a."
            ));
        }
        confirm.setItemMeta(confirmMeta);

        ItemMeta skullMeta = skull.getItemMeta();

        if (target.equals(player)) {
            skullMeta.setDisplayName("§fClicking confirm will kill you.");
        } else {
            skullMeta.setDisplayName("§fClicking confirm will kill " + target.getName() + ".");
        }

        skull.setItemMeta(skullMeta);

        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§cCancel");
        cancel.setItemMeta(cancelMeta);

        death.setItem(10, cancel);
        death.setItem(13, skull);
        death.setItem(16, confirm);

        player.openInventory(death);

        return true;
    }
}