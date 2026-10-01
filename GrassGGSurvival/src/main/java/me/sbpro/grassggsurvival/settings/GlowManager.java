package me.sbpro.grassggsurvival.settings;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class GlowManager {

    private final GrassGGSurvival plugin;

    private final File file;
    private final FileConfiguration config;

    public GlowManager(GrassGGSurvival plugin) {
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

    public boolean isGlowEnabled(UUID uuid) {

        String path = "players." + uuid + ".glow";

        if (!config.contains(path)) {
            config.set(path, "disabled");
            save();
            return false;
        }

        return config.getString(path).equalsIgnoreCase("enabled");
    }

    public void enableGlow(Player player) {

        config.set("players." + player.getUniqueId() + ".glow", "enabled");

        player.setGlowing(true);

        save();
    }

    public void disableGlow(Player player) {

        config.set("players." + player.getUniqueId() + ".glow", "disabled");

        player.setGlowing(false);

        save();
    }

    public void toggleGlow(Player player) {

        if (isGlowEnabled(player.getUniqueId())) {
            disableGlow(player);
        } else {
            enableGlow(player);
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