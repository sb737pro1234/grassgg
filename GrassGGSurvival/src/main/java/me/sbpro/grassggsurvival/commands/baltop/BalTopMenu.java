package me.sbpro.grassggsurvival.commands.baltop;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BalTopMenu {

    private static final List<BalanceEntry> cachedTopPlayers = new ArrayList<>();
    private static final Map<UUID, Integer> cachedPositions = new ConcurrentHashMap<>();
    private static final Map<UUID, Double> cachedBalances = new ConcurrentHashMap<>();

    private BalTopMenu() {
    }

    public static void open(GrassGGSurvival plugin, Player player) {
        BalTopMenuHolder holder = new BalTopMenuHolder();

        Inventory inventory = Bukkit.createInventory(
                holder,
                54,
                BalTopMessages.MENU_TITLE
        );

        holder.setInventory(inventory);

        if (cachedTopPlayers.isEmpty()) {
            refresh(plugin);
        }

        synchronized (cachedTopPlayers) {
            for (int slot = 0; slot < cachedTopPlayers.size() && slot < 45; slot++) {
                BalanceEntry entry = cachedTopPlayers.get(slot);
                int position = slot + 1;

                inventory.setItem(
                        slot,
                        createPlayerBalanceHead(
                                entry.uuid(),
                                entry.playerName(),
                                entry.balance(),
                                position
                        )
                );
            }
        }

        double playerBalance = cachedBalances.getOrDefault(player.getUniqueId(), plugin.getEconomy().getBalance(player));
        int playerPosition = cachedPositions.getOrDefault(player.getUniqueId(), -1);

        inventory.setItem(
                49,
                createPlayerBalanceHead(
                        player,
                        playerBalance,
                        playerPosition
                )
        );

        player.openInventory(inventory);
    }

    public static void refresh(GrassGGSurvival plugin) {
        Economy economy = plugin.getEconomy();

        if (economy == null) {
            return;
        }

        List<BalanceEntry> balances = new ArrayList<>();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (!player.hasPlayedBefore() && !player.isOnline()) {
                continue;
            }

            double balance = economy.getBalance(player);
            String playerName = player.getName() != null ? player.getName() : "Unknown";

            balances.add(new BalanceEntry(
                    player.getUniqueId(),
                    playerName,
                    balance
            ));
        }

        balances.sort(
                Comparator.comparingDouble(BalanceEntry::balance)
                        .reversed()
        );

        Map<UUID, Integer> positions = new ConcurrentHashMap<>();
        Map<UUID, Double> playerBalances = new ConcurrentHashMap<>();

        for (int i = 0; i < balances.size(); i++) {
            BalanceEntry entry = balances.get(i);
            positions.put(entry.uuid(), i + 1);
            playerBalances.put(entry.uuid(), entry.balance());
        }

        synchronized (cachedTopPlayers) {
            cachedTopPlayers.clear();
            cachedTopPlayers.addAll(
                    balances.subList(0, Math.min(45, balances.size()))
            );
        }

        cachedPositions.clear();
        cachedPositions.putAll(positions);

        cachedBalances.clear();
        cachedBalances.putAll(playerBalances);
    }

    private static ItemStack createPlayerBalanceHead(
            UUID uuid,
            String playerName,
            double balance,
            int position
    ) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        meta.setOwningPlayer(player);

        meta.setDisplayName(
                BalTopMessages.topPlayerName(position, playerName)
        );

        List<String> lore = new ArrayList<>();
        lore.add(BalTopMessages.TOP_PLAYER_BLANK_LORE);
        lore.add(BalTopMessages.topPlayerBalance(balance));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack createPlayerBalanceHead(
            Player player,
            double balance,
            int position
    ) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwningPlayer(player);
        meta.setDisplayName(BalTopMessages.YOUR_BALANCE_NAME);

        List<String> lore = new ArrayList<>();
        lore.add(BalTopMessages.YOUR_BALANCE_BLANK_LORE);
        lore.add(BalTopMessages.yourBalanceBalance(balance));
        lore.add(BalTopMessages.yourBalancePosition(position));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private record BalanceEntry(
            UUID uuid,
            String playerName,
            double balance
    ) {
    }
}
