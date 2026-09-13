package me.sbpro.grassgg.commands.rules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class RulesCommand implements CommandExecutor {

    public static final String TITLE = "§x§0§0§C§2§F§FRules";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Inventory inventory = player.getServer().createInventory(null, 27, TITLE);

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§x§0§0§C§2§F§FRules");

            meta.setLore(Arrays.asList(
                    "§x§0§0§C§2§F§F◆ §fNo chat spam, symbols or caps abuse",
                    "§x§0§0§C§2§F§F◆ §fEnglish only, no swearing or inappropriate topics",
                    "§x§0§0§C§2§F§F◆ §fNo disrespect, harassment or toxicity",
                    "§x§0§0§C§2§F§F◆ §fNo scamming, fake messages or impersonation",
                    "§x§0§0§C§2§F§F◆ §fNo advertising or hackusating",
                    "§x§0§0§C§2§F§F◆ §fNo disallowed mods, hacking, ESP or Xray",
                    "§x§0§0§C§2§F§F◆ §fNo glitch/exploit abuse or duping",
                    "§x§0§0§C§2§F§F◆ §fNo lag machines, bad builds or casinos",
                    "§x§0§0§C§2§F§F◆ §fNo alt abuse, autoclicking or macros",
                    "§x§0§0§C§2§F§F◆ §fNo teleport trapping, command or report abuse",
                    "§x§0§0§C§2§F§F◆ §fNo racism, threats, IRL scamming or leaking info",
                    "",
                    "§x§0§0§C§2§F§FFor more details on the rules. Click here.."
            ));

            book.setItemMeta(meta);
        }

        inventory.setItem(13, book);

        player.openInventory(inventory);

        return true;
    }
}