/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.sbpro.grassggalliances.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.sbpro.grassggalliances.model.Alliance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AllianceStorage {
    private final JavaPlugin plugin;
    private final File file;
    private final Map<String, Alliance> alliancesByKey = new HashMap<String, Alliance>();
    private final Map<UUID, String> playerAllianceKey = new HashMap<UUID, String>();

    public AllianceStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        this.load();
    }

    public synchronized void load() {
        this.alliancesByKey.clear();
        this.playerAllianceKey.clear();
        if (!this.file.exists()) {
            this.save();
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)this.file);
        ConfigurationSection section = config.getConfigurationSection("alliances");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            String path = "alliances." + key;
            String name = config.getString(path + ".name", key);
            String ownerRaw = config.getString(path + ".owner");
            if (ownerRaw == null) continue;
            try {
                UUID owner = UUID.fromString(ownerRaw);
                HashSet<UUID> members = new HashSet<UUID>();
                members.add(owner);
                for (String raw : config.getStringList(path + ".members")) {
                    try {
                        members.add(UUID.fromString(raw));
                    }
                    catch (IllegalArgumentException illegalArgumentException) {}
                }
                double xp = config.getDouble(path + ".xp", 0.0);
                int level = Math.max(1, config.getInt(path + ".level", 1));
                Alliance alliance = new Alliance(name, owner, members, xp, level);
                String normalized = this.normalize(name);
                this.alliancesByKey.put(normalized, alliance);
                for (UUID member : members) {
                    this.playerAllianceKey.put(member, normalized);
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
        }
    }

    public synchronized boolean createAlliance(String name, UUID owner) {
        String key = this.normalize(name);
        if (this.alliancesByKey.containsKey(key) || this.playerAllianceKey.containsKey(owner)) {
            return false;
        }
        HashSet<UUID> members = new HashSet<UUID>();
        members.add(owner);
        Alliance alliance = new Alliance(name, owner, members, 0.0, 1);
        this.alliancesByKey.put(key, alliance);
        this.playerAllianceKey.put(owner, key);
        this.save();
        return true;
    }

    public synchronized Optional<Alliance> getAllianceByPlayer(UUID uuid) {
        String key = this.playerAllianceKey.get(uuid);
        return key == null ? Optional.empty() : Optional.ofNullable(this.alliancesByKey.get(key));
    }

    public synchronized Optional<Alliance> getAllianceByName(String name) {
        return Optional.ofNullable(this.alliancesByKey.get(this.normalize(name)));
    }

    public synchronized boolean deleteAlliance(String name) {
        Alliance removed = this.alliancesByKey.remove(this.normalize(name));
        if (removed == null) {
            return false;
        }
        for (UUID member : removed.getMembers()) {
            this.playerAllianceKey.remove(member);
        }
        this.save();
        return true;
    }

    public synchronized boolean addMember(String allianceName, UUID member) {
        Alliance alliance = this.alliancesByKey.get(this.normalize(allianceName));
        if (alliance == null || this.playerAllianceKey.containsKey(member)) {
            return false;
        }
        if (!alliance.addMember(member)) {
            return false;
        }
        this.playerAllianceKey.put(member, this.normalize(alliance.getName()));
        this.save();
        return true;
    }

    public synchronized boolean removeMember(String allianceName, UUID member) {
        Alliance alliance = this.alliancesByKey.get(this.normalize(allianceName));
        if (alliance == null || alliance.getOwner().equals(member)) {
            return false;
        }
        if (!alliance.removeMember(member)) {
            return false;
        }
        this.playerAllianceKey.remove(member);
        this.save();
        return true;
    }

    public synchronized Collection<Alliance> getAlliances() {
        return Collections.unmodifiableCollection(new ArrayList<Alliance>(this.alliancesByKey.values()));
    }

    public synchronized void save() {
        if (!this.plugin.getDataFolder().exists() && !this.plugin.getDataFolder().mkdirs()) {
            this.plugin.getLogger().warning("Could not create plugin data folder.");
        }
        YamlConfiguration config = new YamlConfiguration();
        for (Alliance alliance : this.alliancesByKey.values()) {
            String path = "alliances." + this.normalize(alliance.getName());
            config.set(path + ".name", (Object)alliance.getName());
            config.set(path + ".owner", (Object)alliance.getOwner().toString());
            config.set(path + ".members", alliance.getMembers().stream().map(UUID::toString).toList());
            config.set(path + ".xp", (Object)alliance.getXp());
            config.set(path + ".level", (Object)alliance.getLevel());
        }
        try {
            config.save(this.file);
        }
        catch (IOException exception) {
            this.plugin.getLogger().severe("Could not save alliance data: " + exception.getMessage());
        }
    }

    private String normalize(String input) {
        return input.toLowerCase(Locale.ROOT);
    }
}

