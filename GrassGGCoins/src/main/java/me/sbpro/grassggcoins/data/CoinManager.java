package me.sbpro.grassggcoins.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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

    private void setCoinsInternal(UUID uuid, long amount) {
        data.set(path(uuid), amount);
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
