package me.sbpro.grassgg.commands.heal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
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

        // /heal
        if (args.length == 0) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou must specify a player to use this command.");
                return true;
            }

            if (!player.hasPermission("grassgg.heal")) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou do not have permission to use this command.");
                return true;
            }

            openConfirmMenu(player, player);
            return true;
        }

        // /heal <player>
        if (args.length == 1) {

            if (!sender.hasPermission("grassgg.heal.others")) {
                sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou do not have permission to heal other players.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }

            // Console (or any non-player sender) can't open an inventory, so
            // skip the confirm menu and heal the target directly.
            if (!(sender instanceof Player player)) {
                healDirectly(sender, target);
                return true;
            }

            openConfirmMenu(player, target);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /heal [player]");
        return true;
    }

    private void healDirectly(CommandSender sender, Player target) {

        target.setHealth(target.getAttribute(Attribute.MAX_HEALTH).getValue());
        target.setFoodLevel(20);
        target.setSaturation(20);
        target.setFireTicks(0);

        sender.sendMessage(
                "§x§E§F§4§4§4§4§lADMIN §8» §2" + target.getName() + "§f's health has been restored."
        );

        target.sendMessage(
                "§x§E§F§4§4§4§4§lADMIN §8» §2Your §fhealth has been restored by §2" + sender.getName() + "§f."
        );
    }

    private void openConfirmMenu(Player player, Player target) {

        Inventory heal = Bukkit.createInventory(
                new HealConfirmHolder(target),
                27,
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Heal Confirm"
        );

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack potion = new ItemStack(Material.POTION);
        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        // Confirm
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");

        if (target.equals(player)) {
            confirmMeta.setLore(List.of(
                    "§aThis will restore §fyour§a health."
            ));
        } else {
            confirmMeta.setLore(List.of(
                    "§aThis will restore §f" + target.getName() + "§a's health."
            ));
        }

        confirm.setItemMeta(confirmMeta);

        // Potion
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

        if (target.equals(player)) {
            potionMeta.setDisplayName("§fClicking confirm will restore §2your§f health.");
        } else {
            potionMeta.setDisplayName(
                    "§fClicking confirm will restore §2" + target.getName() + "§f's health."
            );
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

        player.openInventory(heal);
    }
}