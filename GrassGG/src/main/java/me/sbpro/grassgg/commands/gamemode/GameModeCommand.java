package me.sbpro.grassgg.commands.gamemode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GameModeCommand implements CommandExecutor, TabCompleter {

    private static final Map<UUID, PendingGamemodeChange> pending = new HashMap<>();

    public static Map<UUID, PendingGamemodeChange> getPending() {
        return pending;
    }

    protected GamemodeType getForcedGamemode() {
        return null;
    }

    protected boolean isAliasCommand() {
        return false;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou must be a player.");
            return true;
        }

        GamemodeType type;

        if (isAliasCommand()) {

            type = getForcedGamemode();

        } else {

            if (args.length < 1) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cUsage: /gamemode <gamemode> [player]");
                return true;
            }

            type = GamemodeType.fromString(args[0]);

            if (type == null) {
                player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cUnknown gamemode.");
                return true;
            }

        }

        if (type == null) {
            player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cUnknown gamemode.");
            return true;
        }

        if (!player.hasPermission("grassgg.gamemode." + type.getName())) {
            player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou don't have permission to use this gamemode.");
            return true;
        }

        // Self
        int expectedArguments = isAliasCommand() ? 0 : 1;

        if (args.length == expectedArguments) {

            player.setGameMode(type.getGameMode());

            player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §2Your gamemode has been changed to §f"
                    + type.getDisplayName() + "§2.");

            return true;
        }

        // Others

        if (!player.hasPermission("grassgg.gamemode.others")) {
            player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cYou don't have permission to change other players' gamemodes.");
            return true;
        }

        String targetName = isAliasCommand()
                ? args[0]
                : args[1];

        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cThat player is not online.");
            return true;
        }

        Inventory menu = Bukkit.createInventory(null, 27,
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Gamemode Confirm");

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack icon = new ItemStack(type.getIcon());
        ItemStack confirm = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§cCancel");
        cancelMeta.setLore(List.of("§cCancel this action."));
        cancel.setItemMeta(cancelMeta);

        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");
        confirmMeta.setLore(List.of(
                "§aThis will change",
                "§f" + target.getName(),
                "§ato " + type.getDisplayName() + " mode."
        ));
        confirm.setItemMeta(confirmMeta);

        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName("§f§lSwitch " + target.getName() + " to " + type.getDisplayName());
        icon.setItemMeta(iconMeta);

        menu.setItem(10, cancel);
        menu.setItem(13, icon);
        menu.setItem(16, confirm);

        pending.put(player.getUniqueId(),
                new PendingGamemodeChange(target, type));

        player.openInventory(menu);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,
                                      Command command,
                                      String alias,
                                      String[] args) {

        // Alias commands (/gmc, /gms, etc.)
        if (isAliasCommand()) {

            if (args.length == 1) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted()
                        .toList();
            }

            return Collections.emptyList();
        }

        // /gamemode
        if (args.length == 1) {
            return Arrays.asList(
                            "creative",
                            "survival",
                            "adventure",
                            "spectator"
                    ).stream()
                    .filter(mode -> mode.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .sorted()
                    .toList();
        }

        return Collections.emptyList();
    }

}