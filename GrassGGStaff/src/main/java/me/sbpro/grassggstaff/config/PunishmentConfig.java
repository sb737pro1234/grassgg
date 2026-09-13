package me.sbpro.grassggstaff.config;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.punishment.Punishment;
import me.sbpro.grassggstaff.punishment.PunishmentTier;
import me.sbpro.grassggstaff.punishment.PunishmentType;
import me.sbpro.grassggstaff.util.DurationParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public final class PunishmentConfig {

    private final GrassGGStaff plugin;
    private YamlConfiguration config;
    private final Map<String, PunishmentTier> tiers = new LinkedHashMap<>();

    public PunishmentConfig(GrassGGStaff plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "punishments.yml");
        config = YamlConfiguration.loadConfiguration(file);
        tiers.clear();

        ConfigurationSection section = config.getConfigurationSection("tiers");
        if (section == null) {
            plugin.getLogger().severe("punishments.yml is missing 'tiers'.");
            return;
        }

        for (String id : section.getKeys(false)) {
            String display = section.getString(id + ".display-name", id);
            Map<Integer, Punishment> offences = new TreeMap<>();
            ConfigurationSection offenceSection = section.getConfigurationSection(id + ".offences");
            if (offenceSection == null) {
                plugin.getLogger().severe("Tier " + id + " has no offences section.");
                continue;
            }

            for (String key : offenceSection.getKeys(false)) {
                try {
                    int number = Integer.parseInt(key);
                    String typeRaw = offenceSection.getString(key + ".type");
                    if (typeRaw == null) throw new IllegalArgumentException("missing type");

                    PunishmentType type = PunishmentType.valueOf(typeRaw.toUpperCase(Locale.ROOT));
                    String duration = offenceSection.getString(key + ".duration", "permanent");
                    DurationParser.parse(duration); // Validate now.
                    offences.put(number, new Punishment(type, duration));
                } catch (Exception ex) {
                    plugin.getLogger().severe("Invalid punishment " + id + "/" + key + ": " + ex.getMessage());
                }
            }

            tiers.put(id.toUpperCase(Locale.ROOT), new PunishmentTier(id.toUpperCase(Locale.ROOT), display, offences));
        }
    }

    public Optional<PunishmentTier> getTier(String id) {
        return Optional.ofNullable(tiers.get(id.toUpperCase(Locale.ROOT)));
    }

    public Collection<PunishmentTier> getTiers() {
        return Collections.unmodifiableCollection(tiers.values());
    }

    public boolean isValid() {
        return !tiers.isEmpty();
    }

    public YamlConfiguration getRawConfig() {
        return config;
    }
}
