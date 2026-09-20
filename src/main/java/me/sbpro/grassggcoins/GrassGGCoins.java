package me.sbpro.grassggcoins;

import me.sbpro.grassggcoins.command.CoinCommand;
import me.sbpro.grassggcoins.data.CoinManager;
import me.sbpro.grassggcoins.listener.CoinMenuListener;
import me.sbpro.grassggcoins.placeholder.CoinsPlaceholderExpansion;
import me.sbpro.grassggcoins.shop.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Main plugin class for GrassGGCoins.
 */
public final class GrassGGCoins extends JavaPlugin {

    private CoinManager coinManager;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
        saveResource("shop.yml", false);

        this.coinManager = new CoinManager(this);
        this.shopManager = new ShopManager(this);

        CoinCommand coinCommand = new CoinCommand(this);
        Objects.requireNonNull(getCommand("coins"), "coins command missing from plugin.yml")
                .setExecutor(coinCommand);
        Objects.requireNonNull(getCommand("coins"), "coins command missing from plugin.yml")
                .setTabCompleter(coinCommand);

        Objects.requireNonNull(getCommand("coinshop"), "coinshop command missing from plugin.yml")
                .setExecutor(coinCommand);
        Objects.requireNonNull(getCommand("coinshop"), "coinshop command missing from plugin.yml")
                .setTabCompleter(coinCommand);

        Bukkit.getPluginManager().registerEvents(new CoinMenuListener(this), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new CoinsPlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered: %grassggcoins%.");
        }

        startGlobalRewardTimer();
        getLogger().info("GrassGGCoins enabled.");
    }

    @Override
    public void onDisable() {
        if (coinManager != null) {
            coinManager.save();
        }
        getLogger().info("GrassGGCoins disabled.");
    }

    private void startGlobalRewardTimer() {
        long interval = 15L * 60L * 20L; // 15 minutes in ticks.

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                coinManager.addCoins(player.getUniqueId(), 1L, false);

                player.sendActionBar(Messages.rewardActionBar());
                player.sendMessage(Messages.rewardChatMessage());
            });

            coinManager.save();
        }, interval, interval);
    }

    public CoinManager getCoinManager() {
        return coinManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }
}
