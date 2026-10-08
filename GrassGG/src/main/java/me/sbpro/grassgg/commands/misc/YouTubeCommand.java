package me.sbpro.grassgg.commands.misc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YouTubeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§2§lGRASS.GG §8» §4Only players can use this command.");
            return true;
        }

        Component message = Component.text("§2§lGRASS.GG §8» §c")
                .append(Component.text("youtube.com/@MrGrassYT1")
                        .clickEvent(ClickEvent.openUrl("https://youtube.com/@MrGrassYT1"))
                        .hoverEvent(HoverEvent.showText(Component.text("§cClick to open YouTube")))
                        .color(NamedTextColor.RED));
        player.sendMessage(message);

        return true;
    }
}