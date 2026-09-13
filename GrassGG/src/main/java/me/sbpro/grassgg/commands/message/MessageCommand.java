package me.sbpro.grassgg.commands.message;

import me.sbpro.grassgg.GrassGGGlobal;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageCommand implements CommandExecutor, TabCompleter {

    private final GrassGGGlobal main;

    public MessageCommand(GrassGGGlobal main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (main.getMessageManager().isMessagesDisabled(player.getUniqueId())) {
            player.sendMessage("§cYou have private messages disabled. Use /msgtoggle to re-enable them.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cInvalid usage. Usage: /msg [player] [message]");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        // Null-check BEFORE anything else touches target - this was the crash risk before.
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cPlayer not found.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage("§cYou cannot message yourself.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        if (main.getMessageManager().isMessagesDisabled(target.getUniqueId())) {
            player.sendMessage("§c" + target.getName() + " has private messages disabled.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        main.getMessageManager().sendMessage(player, target, message);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .filter(name -> !(sender instanceof Player) || !name.equalsIgnoreCase(sender.getName()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}