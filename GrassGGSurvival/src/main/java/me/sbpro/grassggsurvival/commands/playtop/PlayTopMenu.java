package me.sbpro.grassggsurvival.commands.playtop;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
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

public final class PlayTopMenu {

    private static final List<PlaytimeEntry> cachedTopPlayers = new ArrayList<>();
    private static final Map<UUID, Integer> cachedPositions = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> cachedPlaytimes = new ConcurrentHashMap<>();

    private PlayTopMenu() {
    }

    public static void open(GrassGGSurvival plugin, Player player) {
        PlayTopMenuHolder holder = new PlayTopMenuHolder();

        Inventory inventory = Bukkit.createInventory(
                holder,
                54,
                PlayTopMessages.MENU_TITLE
        );

        holder.setInventory(inventory);

        if (cachedTopPlayers.isEmpty()) {
            refresh(plugin);
        }

        synchronized (cachedTopPlayers) {
            for (int slot = 0; slot < cachedTopPlayers.size() && slot < 45; slot++) {
                PlaytimeEntry entry = cachedTopPlayers.get(slot);
                int position = slot + 1;

                inventory.setItem(
                        slot,
                        createPlayerPlaytimeHead(
                                entry.uuid(),
                                entry.playerName(),
                                entry.playtimeTicks(),
                                position
                        )
                );
            }
        }

        long playerPlaytime = cachedPlaytimes.getOrDefault(player.getUniqueId(), getPlaytimeTicks(player));
        int playerPosition = cachedPositions.getOrDefault(player.getUniqueId(), -1);

        inventory.setItem(
                49,
                createPlayerPlaytimeHead(
                        player,
                        playerPlaytime,
                        playerPosition
                )
        );

        player.openInventory(inventory);
    }

    public static void refresh(GrassGGSurvival plugin) {
        List<PlaytimeEntry> playtimes = new ArrayList<>();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (!player.hasPlayedBefore() && !player.isOnline()) {
                continue;
            }

            long playtimeTicks = getPlaytimeTicks(player);
            String playerName = player.getName() != null ? player.getName() : "Unknown";

            playtimes.add(new PlaytimeEntry(
                    player.getUniqueId(),
                    playerName,
                    playtimeTicks
            ));
        }

        playtimes.sort(
                Comparator.comparingLong(PlaytimeEntry::playtimeTicks)
                        .reversed()
        );

        Map<UUID, Integer> positions = new ConcurrentHashMap<>();
        Map<UUID, Long> playerPlaytimes = new ConcurrentHashMap<>();

        for (int i = 0; i < playtimes.size(); i++) {
            PlaytimeEntry entry = playtimes.get(i);
            positions.put(entry.uuid(), i + 1);
            playerPlaytimes.put(entry.uuid(), entry.playtimeTicks());
        }

        synchronized (cachedTopPlayers) {
            cachedTopPlayers.clear();
            cachedTopPlayers.addAll(
                    playtimes.subList(0, Math.min(45, playtimes.size()))
            );
        }

        cachedPositions.clear();
        cachedPositions.putAll(positions);

        cachedPlaytimes.clear();
        cachedPlaytimes.putAll(playerPlaytimes);
    }

    /**
     * Total time played, in ticks. Despite its name, PLAY_ONE_MINUTE is the
     * statistic that tracks total ticks played.
     */
    private static long getPlaytimeTicks(OfflinePlayer player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
    }

    private static ItemStack createPlayerPlaytimeHead(
            UUID uuid,
            String playerName,
            long playtimeTicks,
            int position
    ) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        meta.setOwningPlayer(player);

        meta.setDisplayName(
                PlayTopMessages.topPlayerName(position, playerName)
        );

        List<String> lore = new ArrayList<>();
        lore.add(PlayTopMessages.TOP_PLAYER_BLANK_LORE);
        lore.add(PlayTopMessages.topPlayerPlaytime(playtimeTicks));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack createPlayerPlaytimeHead(
            Player player,
            long playtimeTicks,
            int position
    ) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwningPlayer(player);
        meta.setDisplayName(PlayTopMessages.YOUR_PLAYTIME_NAME);

        List<String> lore = new ArrayList<>();
        lore.add(PlayTopMessages.YOUR_PLAYTIME_BLANK_LORE);
        lore.add(PlayTopMessages.yourPlaytimePlaytime(playtimeTicks));
        lore.add(PlayTopMessages.yourPlaytimePosition(position));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private record PlaytimeEntry(
            UUID uuid,
            String playerName,
            long playtimeTicks
    ) {
    }
}
