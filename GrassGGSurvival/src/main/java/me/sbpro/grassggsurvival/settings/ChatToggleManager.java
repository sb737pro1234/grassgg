package me.sbpro.grassggsurvival.settings;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ChatToggleManager {

    private final GrassGGSurvival plugin;

    private final File file;
    private final FileConfiguration config;

    public ChatToggleManager(GrassGGSurvival plugin) {
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

    public boolean isChatDisabled(UUID uuid) {

        String path = "players." + uuid + ".chat";

        if (!config.contains(path)) {
            config.set(path, "enabled");
            save();
            return false;
        }

        return config.getString(path).equalsIgnoreCase("disabled");
    }

    public void disableChat(UUID uuid) {
        config.set("players." + uuid + ".chat", "disabled");
        save();
    }

    public void enableChat(UUID uuid) {
        config.set("players." + uuid + ".chat", "enabled");
        save();
    }

    public void toggleChat(UUID uuid) {

        if (isChatDisabled(uuid)) {
            enableChat(uuid);
        } else {
            disableChat(uuid);
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