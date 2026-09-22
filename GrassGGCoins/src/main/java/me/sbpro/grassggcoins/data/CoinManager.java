package me.sbpro.grassggcoins.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class CoinManager {

    private final JavaPlugin plugin;
    private final File file;
    private final YamlConfiguration data;

    public CoinManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.yml");

        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder.");
        }

        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public synchronized long getCoins(UUID uuid) {
        return data.getLong(path(uuid), 0L);
    }

    public synchronized void addCoins(UUID uuid, long amount) {
        addCoins(uuid, amount, true);
    }

    public synchronized void addCoins(UUID uuid, long amount, boolean saveImmediately) {
        long current = getCoins(uuid);
        long result;

        if (amount > 0 && current > Long.MAX_VALUE - amount) {
            result = Long.MAX_VALUE;
        } else if (amount < 0 && current < Long.MIN_VALUE - amount) {
            result = Long.MIN_VALUE;
        } else {
            result = current + amount;
        }

        setCoinsInternal(uuid, Math.max(0L, result));
        if (saveImmediately) {
            save();
        }
    }

    public synchronized boolean takeCoins(UUID uuid, long amount) {
        if (amount < 0) {
            return false;
        }

        long current = getCoins(uuid);
        if (current < amount) {
            return false;
        }

        setCoinsInternal(uuid, current - amount);
        save();
        return true;
    }

    public synchronized void setCoins(UUID uuid, long amount) {
        setCoinsInternal(uuid, Math.max(0L, amount));
        save();
    }

    /**
     * Returns the highest positive coin balances, sorted descending.
     * Ties are sorted alphabetically by player name.
     */
    public synchronized List<TopCoinsEntry> getTopCoins(int limit) {
        if (limit <= 0) {
            return List.of();
        }

        ConfigurationSection players = data.getConfigurationSection("players");
        if (players == null) {
            return List.of();
        }

        List<TopCoinsEntry> entries = new ArrayList<>();

        for (String uuidString : players.getKeys(false)) {
            UUID uuid;

            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException exception) {
                continue;
            }

            long coins = getCoins(uuid);

            // Only players who currently have coins are shown.
            if (coins <= 0) {
                continue;
            }

            String playerName = data.getString("players." + uuid + ".name");

            if (playerName == null || playerName.isBlank()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                playerName = offlinePlayer.getName();
            }

            if (playerName == null || playerName.isBlank()) {
                playerName = "Unknown Player";
            }

            entries.add(new TopCoinsEntry(uuid, playerName, coins));
        }

        entries.sort(
                Comparator.comparingLong(TopCoinsEntry::coins)
                        .reversed()
                        .thenComparing(
                                TopCoinsEntry::playerName,
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        if (entries.size() > limit) {
            return new ArrayList<>(entries.subList(0, limit));
        }

        return entries;
    }

    /**
     * Returns the player's position across the full positive-balance leaderboard.
     * Returns 0 when the player has no positive balance / is not ranked.
     */
    public synchronized int getPosition(UUID uuid) {
        List<TopCoinsEntry> entries = getTopCoins(Integer.MAX_VALUE);

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).uuid().equals(uuid)) {
                return i + 1;
            }
        }

        return 0;
    }

    private void setCoinsInternal(UUID uuid, long amount) {
        data.set(path(uuid), amount);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String playerName = offlinePlayer.getName();

        if (playerName != null && !playerName.isBlank()) {
            data.set("players." + uuid + ".name", playerName);
        }
    }

    private String path(UUID uuid) {
        return "players." + uuid + ".coins";
    }

    public synchronized void save() {
        try {
            data.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save players.yml: " + exception.getMessage());
        }
    }
}
