package me.sbpro.grassgg.commands.heal;

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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.List;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou must be a player to use this command!");
            return true;
        }

        // /heal <player>
        if (args.length == 1) {

            if (!player.hasPermission("grassgg.heal.others")) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou don't have permission to heal other players.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cThat player is not online.");
                return true;
            }

            Inventory heal = Bukkit.createInventory(
                    new HealConfirmHolder(target),
                    27,
                    ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Heal Confirm"
            );

            createHealMenu(player, target, heal);

            player.openInventory(heal);
            return true;
        }

        // /heal
        if (!player.hasPermission("grassgg.heal")) {
            player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou don't have permission to heal yourself.");
            return true;
        }

        Player target = player;

        Inventory heal = Bukkit.createInventory(
                new HealConfirmHolder(target),
                27,
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Heal Confirm"
        );

        createHealMenu(player, target, heal);

        player.openInventory(heal);

        return true;
    }

    private void createHealMenu(Player player, Player target, Inventory heal) {

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack potion = new ItemStack(Material.POTION);
        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        // Confirm
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");

        if (target.equals(player)) {
            confirmMeta.setLore(List.of("§fClicking confirm will heal §cyou."));
        } else {
            confirmMeta.setLore(List.of("§fClicking confirm will heal §c" + target.getName() + "."));
        }

        confirm.setItemMeta(confirmMeta);

        // Potion
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

        if (target.equals(player)) {
            potionMeta.setDisplayName("§fClicking confirm will heal you.");
        } else {
            potionMeta.setDisplayName("§fClicking confirm will heal " + target.getName() + ".");
        }

        potionMeta.setBasePotionType(PotionType.HEALING);
        potion.setItemMeta(potionMeta);

        // Cancel
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§cCancel");
        cancel.setItemMeta(cancelMeta);

        heal.setItem(10, cancel);
        heal.setItem(13, potion);
        heal.setItem(16, confirm);
    }
}