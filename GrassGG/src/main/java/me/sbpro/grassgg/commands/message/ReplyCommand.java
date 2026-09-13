package me.sbpro.grassgg.commands.message;

import me.sbpro.grassgg.GrassGGGlobal;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReplyCommand implements CommandExecutor {

    private final GrassGGGlobal main;

    public ReplyCommand(GrassGGGlobal main) {
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

        if (args.length < 1) {
            player.sendMessage("§cInvalid usage. Usage: /r [message]");
            return true;
        }

        UUID partnerUuid = main.getMessageManager().getLastPartner(player.getUniqueId());

        if (partnerUuid == null) {
            player.sendMessage("§cYou have nobody to reply to.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        Player target = Bukkit.getPlayer(partnerUuid);

        if (target == null || !target.isOnline()) {
            player.sendMessage("§cThat player is no longer online.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            main.getMessageManager().clearPartner(player.getUniqueId());
            return true;
        }

        if (main.getMessageManager().isMessagesDisabled(target.getUniqueId())) {
            player.sendMessage("§c" + target.getName() + " has private messages disabled.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        String message = String.join(" ", args);

        main.getMessageManager().sendMessage(player, target, message);

        return true;
    }
}