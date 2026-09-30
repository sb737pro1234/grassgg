package me.sbpro.grassggquests.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sbpro.grassggquests.GrassGGQuests;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestExpansion extends PlaceholderExpansion {
    private final GrassGGQuests plugin;

    public QuestExpansion(GrassGGQuests plugin) { this.plugin = plugin; }

    @Override public @NotNull String getIdentifier() { return "grassggquests"; }
    @Override public @NotNull String getAuthor() { return "GrassGG"; }
    @Override public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }
    @Override public boolean canRegister() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        if (params.equalsIgnoreCase("level")) return String.valueOf(plugin.getQuestManager().getData(player.getUniqueId()).getLevel());
        if (params.equalsIgnoreCase("xp")) return String.valueOf(plugin.getQuestManager().getData(player.getUniqueId()).getXp());
        if (params.equalsIgnoreCase("points")) return String.valueOf(plugin.getQuestManager().getData(player.getUniqueId()).getQuestPoints());
        return null;
    }
}
