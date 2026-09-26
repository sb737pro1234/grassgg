package me.sbpro.grassggstaff.commands.sign;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SignCommand implements CommandExecutor {

    // Customizable line added under a blank line at the bottom of the item's lore.
    // %player% is replaced with the name of the player who signs the item.
    private static final String SIGN_LINE = "§fSigned by §x§2§9§7§9§F§F%player%§f";

    // The fixed portion of SIGN_LINE before %player% is inserted.
    // Used to detect whether an item has already been signed.
    private static final String SIGN_PREFIX = SIGN_LINE.substring(0, SIGN_LINE.indexOf("%player%"));

    private static final String PERMISSION = "grassgg.staff.sign";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            player.sendMessage("§x§2§9§7§9§F§F§lSTAFF §8»§c You must be holding an item to sign it.");
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage("§x§2§9§7§9§F§F§lSTAFF §8»§c This item cannot have lore added to it.");
            return true;
        }

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        if (isAlreadySigned(lore)) {
            player.sendMessage("§x§2§9§7§9§F§F§lSTAFF §8»§c This item has already been signed.");
            return true;
        }

        String signedLine = SIGN_LINE.replace("%player%", player.getName());

        lore.add("");           // extra blank line
        lore.add(signedLine);   // customizable signed line

        meta.setLore(lore);
        item.setItemMeta(meta);

        player.sendMessage("§x§2§9§7§9§F§F§lSTAFF §8»§a Item signed.");
        return true;
    }

    // Returns true if any existing lore line matches the signed-line prefix.
    private boolean isAlreadySigned(List<String> lore) {
        for (String line : lore) {
            if (line.startsWith(SIGN_PREFIX)) {
                return true;
            }
        }
        return false;
    }
}