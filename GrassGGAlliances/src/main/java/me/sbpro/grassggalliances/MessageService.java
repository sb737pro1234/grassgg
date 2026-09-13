/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.sbpro.grassggalliances;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MessageService {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration configuration;

    public MessageService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "alliances.yml");
        this.configuration = YamlConfiguration.loadConfiguration((File)this.file);
    }

    public void saveDefaultMessages() {
        if (!this.file.exists()) {
            this.plugin.saveResource("alliances.yml", false);
        }
        this.reload();
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration((File)this.file);
    }

    public String getRaw(String path) {
        return this.configuration.getString("messages." + path, "");
    }

    public List<String> getStringList(String path) {
        return this.configuration.getStringList("messages." + path);
    }

    public Component getComponent(String path) {
        return this.color(this.getRaw(path));
    }

    public Component prefixed(String path) {
        return this.prefixed(path, input -> input);
    }

    public Component prefixed(String path, PlaceholderReplacer replacer) {
        return this.color(this.getRaw("prefix") + replacer.apply(this.getRaw(path)));
    }

    public List<Component> getComponentList(String path, PlaceholderReplacer replacer) {
        ArrayList<Component> components = new ArrayList<Component>();
        for (String line : this.getStringList(path)) {
            components.add(this.color(replacer.apply(line)));
        }
        return components;
    }

    private Component color(String input) {
        return LEGACY.deserialize(input);
    }

    @FunctionalInterface
    public static interface PlaceholderReplacer {
        public String apply(String var1);
    }
}

