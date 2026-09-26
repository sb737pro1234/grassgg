package me.sbpro.grassgg.color;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ColorsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        sender.sendMessage("");

        sender.sendMessage("§8ChatColors");

        sender.sendMessage(
                "§r§0&0  " +
                        "§r§1&1  " +
                        "§r§2&2  " +
                        "§r§3&3  " +
                        "§r§4&4  " +
                        "§r§5&5  " +
                        "§r§6&6  " +
                        "§r§7&7"
        );

        sender.sendMessage(
                "§r§8&8  " +
                        "§r§9&9  " +
                        "§r§a&a  " +
                        "§r§b&b  " +
                        "§r§c&c  " +
                        "§r§d&d  " +
                        "§r§e&e  " +
                        "§r§f&f"
        );

        sender.sendMessage("");

        sender.sendMessage("§8Formatting");

        sender.sendMessage(
                "§r§f§l&l§r§f §8(Bold)  " +
                        "§r§f§m&m§r§f §8(Strikethrough)"
        );

        sender.sendMessage(
                "§r§f§o&o§r§f §8(Italic)  " +
                        "§r§f§n&n§r§f §8(Underline)"
        );

        sender.sendMessage(
                "§r§f§k&k§r§f §8(Obfuscated) (&k)  " +
                        "§r§f&r§r§f §8(Reset)"
        );

        sender.sendMessage("");

        return true;
    }
}