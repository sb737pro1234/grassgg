package me.sbpro.grassggcoins.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.util.AmountFormatter;
import org.bukkit.OfflinePlayer;

public final class CoinsPlaceholderExpansion extends PlaceholderExpansion {

    private final GrassGGCoins plugin;

    public CoinsPlaceholderExpansion(GrassGGCoins plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "grassggcoins";
    }

    @Override
    public String getAuthor() {
        return "GrassGG";
    }

    @Override
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "0";
        }

        if (params.equalsIgnoreCase("balance") || params.equalsIgnoreCase("coins")) {
            return AmountFormatter.format(plugin.getCoinManager().getCoins(player.getUniqueId()));
        }

        return null;
    }
}
