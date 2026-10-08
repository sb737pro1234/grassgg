package me.sbpro.grassggmrgrassyt.commands;

import me.sbpro.grassggmrgrassyt.GrassGGMrGrassYT;
import me.sbpro.grassggmrgrassyt.items.MrGrassYTItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MrGrassYTCommand implements CommandExecutor, TabCompleter {

    private final GrassGGMrGrassYT plugin;

    public MrGrassYTCommand(GrassGGMrGrassYT plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length == 0) {

            Component message = Component.text("§2§lGRASS.GG §8» §c")
                    .append(Component.text("youtube.com/@MrGrassYT1")
                            .clickEvent(ClickEvent.openUrl("https://youtube.com/@MrGrassYT1"))
                            .hoverEvent(HoverEvent.showText(Component.text("§cClick to open YouTube")))
                            .color(NamedTextColor.RED));
            player.sendMessage(message);

            return true;
        }

        if (args[0].equalsIgnoreCase("toolgive")) {
            if (!player.hasPermission("grassgg.mrgrassyt.toolgive")) {
                player.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage("§cUsage: /mrgrassyt toolgive [ITEM]");
                return true;
            }

            String identifier = args[1];
            ItemStack item = MrGrassYTItems.getItem(identifier);

            if (item == null) {
                player.sendMessage("§cUnknown item: §f" + identifier);
                return true;
            }

            player.getInventory().addItem(item);
            player.sendMessage("§2§lGRASS.GG §8» §fYou received §2" + identifier + "§f.");
            return true;
        }

        player.sendMessage("§cUnknown subcommand. Use §f/mrgrassyt toolgive [ITEM]§c.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("toolgive");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("toolgive")) {
            return new ArrayList<>(MrGrassYTItems.getIdentifiers());
        }

        return List.of();
    }
}
