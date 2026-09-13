package me.sbpro.fastplace.commands;

import me.sbpro.fastplace.manager.FastPlaceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FPCommand implements CommandExecutor {
    private final FastPlaceManager manager;
    private static final int[] VALID_TIERS = {16, 32, 64, 128};

    public FPCommand(FastPlaceManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            this.manager.disable(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&x&A&7&F&3&D&0&lFASTPLACE &8» &c&l(!) &cFastplace disabled."));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&x&A&7&F&3&D&0&lFASTPLACE &8» &cUsage: /fp [1-16|1-32|1-64|1-128]"));
            return true;
        }

        int maxAllowed = this.getMaxAmountForPlayer(player);
        if (maxAllowed <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&x&A&7&F&3&D&0&lFASTPLACE &8» &cYou don't have permission to use fastplace."));
            return true;
        }

        if (amount < 1 || amount > maxAllowed) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&x&A&7&F&3&D&0&lFASTPLACE &8» &cUsage: /fp [1-" + maxAllowed + "]"));
            return true;
        }

        this.manager.enable(player.getUniqueId(), amount);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&x&A&7&F&3&D&0&lFASTPLACE &8» &6&l(!) &6Fastplace enabled with block range &e" + amount + "&6."));
        return true;
    }

    private int getMaxAmountForPlayer(Player player) {
        for (int i = VALID_TIERS.length - 1; i >= 0; i--) {
            int tier = VALID_TIERS[i];
            if (player.hasPermission("grassgg.fastplace.use." + tier)) {
                return tier;
            }
        }
        if (player.hasPermission("grassgg.fastplace.use")) {
            return 16;
        }
        return 0;
    }
}
