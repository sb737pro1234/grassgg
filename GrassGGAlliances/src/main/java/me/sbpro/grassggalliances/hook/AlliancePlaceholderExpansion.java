/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.expansion.PlaceholderExpansion
 *  org.bukkit.OfflinePlayer
 *  org.jetbrains.annotations.NotNull
 */
package me.sbpro.grassggalliances.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sbpro.grassggalliances.GrassGGAlliances;
import me.sbpro.grassggalliances.data.AllianceStorage;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class AlliancePlaceholderExpansion
extends PlaceholderExpansion {
    private final GrassGGAlliances plugin;
    private final AllianceStorage allianceStorage;

    public AlliancePlaceholderExpansion(GrassGGAlliances plugin, AllianceStorage allianceStorage) {
        this.plugin = plugin;
        this.allianceStorage = allianceStorage;
    }

    @NotNull
    public String getIdentifier() {
        return "alliances";
    }

    @NotNull
    public String getAuthor() {
        return "alex";
    }

    @NotNull
    public String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }
        if (params.equalsIgnoreCase("name")) {
            return this.allianceStorage.getAllianceByPlayer(player.getUniqueId()).map(alliance -> alliance.getName()).orElse("None");
        }
        return "";
    }
}

