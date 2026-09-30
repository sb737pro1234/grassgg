package me.sbpro.grassggquests.commands;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.Messages;
import me.sbpro.grassggquests.quests.QuestDefinition;
import me.sbpro.grassggquests.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class QuestsCommand implements CommandExecutor, TabCompleter {
    private final GrassGGQuests plugin;

    public QuestsCommand(GrassGGQuests plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) { sender.sendMessage(Messages.PREFIX + Messages.PLAYER_ONLY); return true; }
            plugin.getMenus().openMain(player);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "shop" -> {
                if (args.length == 1) {
                    if (!(sender instanceof Player player)) { sender.sendMessage(Messages.PREFIX + Messages.PLAYER_ONLY); return true; }
                    plugin.getMenus().openShop(player);
                    return true;
                }
                if (args.length == 3 && args[1].equalsIgnoreCase("setdisplayitem")) {
                    if (!isAdmin(sender)) return true;
                    if (!(sender instanceof Player player)) { sender.sendMessage(Messages.PREFIX + Messages.PLAYER_ONLY); return true; }
                    String id = args[2];
                    ItemStack held = player.getInventory().getItemInMainHand();
                    if (held.getType().isAir()) { sender.sendMessage(Messages.PREFIX + Messages.INVALID_SHOP_ITEM); return true; }
                    if (!plugin.getShopManager().setDisplayItem(id, held)) { sender.sendMessage(Messages.PREFIX + Messages.INVALID_SHOP_ITEM); return true; }
                    sender.sendMessage(Messages.PREFIX + Messages.DISPLAY_ITEM_CHANGED.replace("%item%", id));
                    return true;
                }
            }
            case "level" -> {
                if (!(sender instanceof Player player)) { sender.sendMessage(Messages.PREFIX + Messages.PLAYER_ONLY); return true; }
                plugin.getMenus().openLevel(player);
                return true;
            }
            case "reload" -> {
                if (!isAdmin(sender)) return true;
                plugin.reloadPluginConfig();
                sender.sendMessage(Messages.PREFIX + Messages.RELOAD_SUCCESS);
                return true;
            }
            case "setlevel" -> {
                if (!isAdmin(sender)) return true;
                if (args.length != 3) { sender.sendMessage(Messages.PREFIX + "§cUsage: /quests setlevel <player> <level>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage(Messages.PREFIX + Messages.INVALID_PLAYER); return true; }
                int level;
                try { level = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sender.sendMessage(Messages.PREFIX + Messages.INVALID_NUMBER); return true; }
                plugin.getQuestManager().setLevel(target, level);
                sender.sendMessage(Messages.PREFIX + Messages.SET_LEVEL_SUCCESS.replace("%player%", target.getName()).replace("%level%", String.valueOf(Math.max(1, level))));
                return true;
            }
            case "reset" -> {
                if (!isAdmin(sender)) return true;
                if (args.length != 2) { sender.sendMessage(Messages.PREFIX + "§cUsage: /quests reset <player>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage(Messages.PREFIX + Messages.INVALID_PLAYER); return true; }
                plugin.getQuestManager().reset(target);
                sender.sendMessage(Messages.PREFIX + Messages.RESET_SUCCESS.replace("%player%", target.getName()));
                return true;
            }
            case "give" -> {
                if (!isAdmin(sender)) return true;
                if (args.length != 3) { sender.sendMessage(Messages.PREFIX + "§cUsage: /quests give <player> <quest>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage(Messages.PREFIX + Messages.INVALID_PLAYER); return true; }
                QuestDefinition quest = plugin.getQuestManager().getDefinition(args[2]);
                if (quest == null) { sender.sendMessage(Messages.PREFIX + Messages.INVALID_QUEST); return true; }
                if (!plugin.getQuestManager().giveQuest(target, quest.getId(), true)) {
                    sender.sendMessage(Messages.PREFIX + Messages.ALREADY_ACTIVE);
                    return true;
                }
                sender.sendMessage(Messages.PREFIX + Messages.GIVE_SUCCESS.replace("%quest%", quest.getTitle()).replace("%player%", target.getName()));
                return true;
            }
            default -> sender.sendMessage(Messages.PREFIX + Messages.USAGE);
        }
        return true;
    }

    private boolean isAdmin(CommandSender sender) {
        if (!sender.hasPermission("grassggquests.admin")) {
            sender.sendMessage(Messages.PREFIX + Messages.NO_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return partial(args[0], List.of("shop", "level", "reload", "setlevel", "reset", "give"));
        if (args.length == 2 && args[0].equalsIgnoreCase("shop")) return partial(args[1], List.of("setDisplayItem"));
        if (args.length == 3 && args[0].equalsIgnoreCase("shop") && args[1].equalsIgnoreCase("setDisplayItem")) return partial(args[2], new ArrayList<>(plugin.getShopManager().getItems().keySet()));
        if (args.length == 2 && Set.of("setlevel", "reset", "give").contains(args[0].toLowerCase(Locale.ROOT))) return onlinePlayers(args[1]);
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) return partial(args[2], new ArrayList<>(me.sbpro.grassggquests.Quests.QUESTS.keySet()));
        return Collections.emptyList();
    }

    private List<String> onlinePlayers(String input) {
        List<String> names = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
        return partial(input, names);
    }

    private List<String> partial(String input, List<String> options) {
        List<String> result = new ArrayList<>();
        for (String option : options) if (option.toLowerCase(Locale.ROOT).startsWith(input.toLowerCase(Locale.ROOT))) result.add(option);
        return result;
    }
}
