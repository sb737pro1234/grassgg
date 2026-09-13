package me.sbpro.grassggchat.chatcolor;

import me.sbpro.grassggchat.GrassGGChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ChatColorManager {

    private final GrassGGChat plugin;

    private File file;
    private FileConfiguration config;

    public ChatColorManager(GrassGGChat plugin) {
        this.plugin = plugin;

        setupFile();
    }

    private void setupFile() {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        file = new File(
                plugin.getDataFolder(),
                "chatcolors.yml"
        );

        if (!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {

                plugin.getLogger().severe(
                        "Could not create chatcolors.yml!"
                );

                e.printStackTrace();
            }
        }

        config =
                YamlConfiguration.loadConfiguration(file);
    }

    public PlayerChatColor getColor(UUID uuid) {

        String stored =
                config.getString(
                        "players." + uuid + ".color"
                );

        if (stored == null) {
            return PlayerChatColor.WHITE;
        }

        try {

            return PlayerChatColor.valueOf(
                    stored.toUpperCase()
            );

        } catch (IllegalArgumentException exception) {

            return PlayerChatColor.WHITE;
        }
    }

    public void setColor(
            UUID uuid,
            PlayerChatColor color
    ) {

        config.set(
                "players." + uuid + ".color",
                color.name()
        );

        save();
    }

    public boolean isBold(UUID uuid) {

        return config.getBoolean(
                "players." + uuid + ".bold",
                false
        );
    }

    public void setBold(
            UUID uuid,
            boolean bold
    ) {

        config.set(
                "players." + uuid + ".bold",
                bold
        );

        save();
    }

    private void save() {

        try {

            config.save(file);

        } catch (IOException e) {

            plugin.getLogger().severe(
                    "Could not save chatcolors.yml!"
            );

            e.printStackTrace();
        }
    }

    public void reload() {

        config =
                YamlConfiguration.loadConfiguration(file);
    }
}