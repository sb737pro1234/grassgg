package me.sbpro.grassggsurvival.settings;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ScoreboardManager {

    private final GrassGGSurvival plugin;

    private final File file;
    private final FileConfiguration config;

    public ScoreboardManager(GrassGGSurvival plugin) {
        this.plugin = plugin;

        File folder = new File(plugin.getDataFolder(), "settings/data");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        file = new File(folder, "settings.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isScoreboardEnabled(UUID uuid) {

        String path = "players." + uuid + ".scoreboard";

        if (!config.contains(path)) {
            config.set(path, "enabled");
            save();
            return true;
        }

        return config.getString(path).equalsIgnoreCase("enabled");
    }

    public void enableScoreboard(Player player) {

        config.set("players." + player.getUniqueId() + ".scoreboard", "enabled");

        Bukkit.dispatchCommand(player, "tab scoreboard on");

        save();
    }

    public void disableScoreboard(Player player) {

        config.set("players." + player.getUniqueId() + ".scoreboard", "disabled");

        Bukkit.dispatchCommand(player, "tab scoreboard off");

        save();
    }

    public void toggleScoreboard(Player player) {

        if (isScoreboardEnabled(player.getUniqueId())) {
            disableScoreboard(player);
        } else {
            enableScoreboard(player);
        }

    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}