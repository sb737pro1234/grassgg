package me.sbpro.grassggcoins.command;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.Messages;
import me.sbpro.grassggcoins.banknote.BankNoteManager;
import me.sbpro.grassggcoins.gui.MenuFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CoinCommand implements CommandExecutor, TabCompleter {

    private final GrassGGCoins plugin;

    public CoinCommand(GrassGGCoins plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        boolean isCoinShopCommand =
                command.getName().equalsIgnoreCase("coinshop");

        boolean isWithdrawCoinsCommand =
                command.getName().equalsIgnoreCase("withdrawcoins");

        boolean isCoinTopCommand =
                command.getName().equalsIgnoreCase("cointop");

        if (isCoinShopCommand) {
            return openShop(sender);
        }

        if (isWithdrawCoinsCommand) {
            return withdraw(sender, args);
        }

        if (isCoinTopCommand) {
            return openTop(sender);
        }

        if (!sender.hasPermission("grassgg.coins.use")) {
            sender.sendMessage(Messages.noPermission());
            return true;
        }

        if (args.length == 0) {
            return openCoins(sender);
        }

        if (args[0].equalsIgnoreCase("reload")) {
            return reload(sender);
        }

        if (args[0].equalsIgnoreCase("withdraw")) {
            return withdraw(sender, args);
        }

        if (args[0].equalsIgnoreCase("top")) {
            return openTop(sender);
        }

        if (args[0].equalsIgnoreCase("shop")) {

            if (args.length == 1) {
                return openShop(sender);
            }

            if (args.length >= 2
                    && args[1].equalsIgnoreCase("setDisplayItem")) {

                return setDisplayItem(sender, args);
            }
        }

        if (args[0].equalsIgnoreCase("give")) {
            return modify(sender, args, Modification.GIVE);
        }

        if (args[0].equalsIgnoreCase("take")) {
            return modify(sender, args, Modification.TAKE);
        }

        if (args[0].equalsIgnoreCase("set")) {
            return modify(sender, args, Modification.SET);
        }

        if (args[0].equalsIgnoreCase("balance")) {
            return balance(sender, args);
        }

        sender.sendMessage(Messages.unknownSubcommand());
        return true;
    }

    private boolean openCoins(CommandSender sender) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.playerOnly());
            return true;
        }

        player.openInventory(
                MenuFactory.createCoinsMenu(plugin, player)
        );

        return true;
    }

    private boolean openTop(CommandSender sender) {

        if (!sender.hasPermission("grassgg.coins.use")) {
            sender.sendMessage(Messages.noPermission());
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.playerOnly());
            return true;
        }

        player.openInventory(
                MenuFactory.createTopCoinsMenu(plugin, player)
        );

        return true;
    }

    private boolean openShop(CommandSender sender) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.playerOnly());
            return true;
        }

        if (!sender.hasPermission("grassgg.coins.shop")) {
            sender.sendMessage(Messages.noPermission());
            return true;
        }

        player.openInventory(
                MenuFactory.createShopMenu(plugin, player)
        );

        return true;
    }

    private boolean reload(CommandSender sender) {

        if (!sender.hasPermission("grassgg.coins.reload")) {
            sender.sendMessage(Messages.noPermission());
            return true;
        }

        plugin.getShopManager().reload();

        sender.sendMessage(
                Messages.reloadSuccess()
        );

        return true;
    }

    private boolean setDisplayItem(
            CommandSender sender,
            String[] args
    ) {

        if (!sender.hasPermission(
                "grassgg.coins.shop.setdisplayitem")) {

            sender.sendMessage(Messages.noPermission());
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.playerOnly());
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(
                    Messages.shopIdentifierMissing()
            );
            return true;
        }

        String identifier = args[2];

        if (plugin.getShopManager().getItem(identifier) == null) {
            sender.sendMessage(
                    Messages.shopIdentifierUnknown(identifier)
            );
            return true;
        }

        if (player.getInventory()
                .getItemInMainHand()
                .getType()
                .isAir()) {

            sender.sendMessage(
                    Messages.emptyHand()
            );

            return true;
        }

        plugin.getShopManager().setDisplayItem(
                identifier,
                player.getInventory().getItemInMainHand()
        );

        sender.sendMessage(
                Messages.setDisplaySuccess(identifier)
        );

        return true;
    }

    private boolean modify(
            CommandSender sender,
            String[] args,
            Modification modification
    ) {

        String permission =
                "grassgg.coins."
                        + modification.name().toLowerCase();

        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Messages.noPermission());
            return true;
        }

        if (args.length < 3) {

            sender.sendMessage(
                    Messages.usage(
                            "/coins "
                                    + modification.name().toLowerCase()
                                    + " <player> <amount>"
                    )
            );

            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);

        if (target == null) {
            sender.sendMessage(Messages.playerNotFound());
            return true;
        }

        Long amount = parseLong(sender, args[2]);

        if (amount == null) {
            return true;
        }

        if (amount < 0) {
            sender.sendMessage(Messages.invalidAmount());
            return true;
        }

        switch (modification) {

            case GIVE -> {

                plugin.getCoinManager().addCoins(
                        target.getUniqueId(),
                        amount
                );

                sender.sendMessage(
                        Messages.giveSuccess(
                                target.getName(),
                                amount
                        )
                );
            }

            case TAKE -> {

                long current =
                        plugin.getCoinManager()
                                .getCoins(target.getUniqueId());

                long actual =
                        Math.min(current, amount);

                plugin.getCoinManager().takeCoins(
                        target.getUniqueId(),
                        actual
                );

                sender.sendMessage(
                        Messages.takeSuccess(
                                target.getName(),
                                actual
                        )
                );
            }

            case SET -> {

                plugin.getCoinManager().setCoins(
                        target.getUniqueId(),
                        amount
                );

                sender.sendMessage(
                        Messages.setSuccess(
                                target.getName(),
                                amount
                        )
                );
            }
        }

        return true;
    }

    private boolean withdraw(
            CommandSender sender,
            String[] args
    ) {

        if (!sender.hasPermission(
                "grassgg.coins.withdraw")) {

            sender.sendMessage(Messages.noPermission());
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.playerOnly());
            return true;
        }

        if (args.length < 2) {

            sender.sendMessage(
                    Messages.usage(
                            "/coins withdraw <amount>"
                    )
            );

            return true;
        }

        Long amount = parseLong(sender, args[1]);

        if (amount == null) {
            return true;
        }

        if (amount <= 0) {

            sender.sendMessage(
                    Messages.invalidWithdrawAmount()
            );

            return true;
        }

        long balance =
                plugin.getCoinManager()
                        .getCoins(player.getUniqueId());

        if (balance < amount) {

            sender.sendMessage(
                    Messages.insufficientWithdrawBalance(
                            balance,
                            amount
                    )
            );

            return true;
        }

        if (!plugin.getCoinManager().takeCoins(
                player.getUniqueId(),
                amount
        )) {

            sender.sendMessage(
                    Messages.insufficientWithdrawBalance(
                            balance,
                            amount
                    )
            );

            return true;
        }

        BankNoteManager bankNoteManager =
                plugin.getBankNoteManager();

        ItemStack bankNote =
                bankNoteManager.createBankNote(
                        player,
                        amount
                );

        HashMap<Integer, ItemStack> leftovers =
                player.getInventory().addItem(bankNote);

        if (!leftovers.isEmpty()) {

            plugin.getCoinManager().addCoins(
                    player.getUniqueId(),
                    amount
            );

            sender.sendMessage(
                    Messages.bankNoteInventoryFull()
            );

            return true;
        }

        sender.sendMessage(
                Messages.withdrawSuccess(amount)
        );

        return true;
    }

    private boolean balance(
            CommandSender sender,
            String[] args
    ) {

        if (args.length == 1) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage(Messages.playerOnly());
                return true;
            }

            long amount =
                    plugin.getCoinManager()
                            .getCoins(player.getUniqueId());

            sender.sendMessage(
                    Messages.balanceMessage(
                            player.getName(),
                            amount
                    )
            );

            return true;
        }

        if (!sender.hasPermission(
                "grassgg.coins.balance.others")) {

            sender.sendMessage(Messages.noPermission());
            return true;
        }

        Player target =
                Bukkit.getPlayerExact(args[1]);

        if (target == null) {
            sender.sendMessage(Messages.playerNotFound());
            return true;
        }

        sender.sendMessage(
                Messages.balanceMessage(
                        target.getName(),
                        plugin.getCoinManager()
                                .getCoins(target.getUniqueId())
                )
        );

        return true;
    }

    private Long parseLong(
            CommandSender sender,
            String value
    ) {

        try {
            return Long.parseLong(value);

        } catch (NumberFormatException exception) {

            sender.sendMessage(
                    Messages.invalidNumber()
            );

            return null;
        }
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        boolean isWithdrawCoinsCommand =
                command.getName().equalsIgnoreCase("withdrawcoins");

        if (isWithdrawCoinsCommand) {
            return List.of();
        }

        boolean isCoinShopCommand =
                command.getName()
                        .equalsIgnoreCase("coinshop");

        if (isCoinShopCommand) {

            if (args.length == 1) {

                return partial(
                        args[0],
                        List.of("setDisplayItem")
                );
            }

            if (args.length == 2
                    && args[0].equalsIgnoreCase("setDisplayItem")) {

                return partial(
                        args[1],
                        plugin.getShopManager()
                                .getIdentifiers()
                );
            }

            return List.of();
        }

        if (args.length == 1) {

            List<String> suggestions =
                    new ArrayList<>(
                            List.of(
                                    "shop",
                                    "top",
                                    "give",
                                    "take",
                                    "set",
                                    "balance",
                                    "withdraw",
                                    "reload"
                            )
                    );

            if (!sender.hasPermission(
                    "grassgg.coins.shop")) {
                suggestions.remove("shop");
            }

            if (!sender.hasPermission(
                    "grassgg.coins.give")) {
                suggestions.remove("give");
            }

            if (!sender.hasPermission(
                    "grassgg.coins.take")) {
                suggestions.remove("take");
            }

            if (!sender.hasPermission(
                    "grassgg.coins.set")) {
                suggestions.remove("set");
            }

            if (!sender.hasPermission(
                    "grassgg.coins.withdraw")) {
                suggestions.remove("withdraw");
            }

            if (!sender.hasPermission(
                    "grassgg.coins.reload")) {
                suggestions.remove("reload");
            }

            return partial(args[0], suggestions);
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("shop")) {

            return partial(
                    args[1],
                    List.of("setDisplayItem")
            );
        }

        if (args.length == 3
                && args[0].equalsIgnoreCase("shop")
                && args[1].equalsIgnoreCase("setDisplayItem")) {

            return partial(
                    args[2],
                    plugin.getShopManager()
                            .getIdentifiers()
            );
        }

        if (args.length == 2
                && List.of(
                "give",
                "take",
                "set",
                "balance"
        ).contains(args[0].toLowerCase())) {

            return partial(
                    args[1],
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .toList()
            );
        }

        return List.of();
    }

    private List<String> partial(
            String token,
            List<String> values
    ) {

        return values.stream()
                .filter(value ->
                        value.toLowerCase()
                                .startsWith(
                                        token.toLowerCase()
                                )
                )
                .sorted(
                        String.CASE_INSENSITIVE_ORDER
                )
                .toList();
    }

    private enum Modification {
        GIVE,
        TAKE,
        SET
    }
}