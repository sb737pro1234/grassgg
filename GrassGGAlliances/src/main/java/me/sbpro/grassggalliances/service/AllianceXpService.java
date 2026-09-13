/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.sbpro.grassggalliances.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.sbpro.grassggalliances.MessageService;
import me.sbpro.grassggalliances.data.AllianceStorage;
import me.sbpro.grassggalliances.model.Alliance;
import me.sbpro.grassggalliances.service.AllianceBuffService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AllianceXpService {
    private static final double[] LEVEL_THRESHOLDS = new double[]{0.0, 0.0, 500.0, 1250.0, 2250.0, 3500.0, 5000.0, 6750.0, 8750.0, 11000.0, 14000.0};
    private final JavaPlugin plugin;
    private final AllianceStorage allianceStorage;
    private final MessageService messageService;
    private final AllianceBuffService buffService;
    private final File file;
    private final Map<Material, XpEntry> blockEntries = new EnumMap<Material, XpEntry>(Material.class);
    private final Map<Material, XpEntry> fishingEntries = new EnumMap<Material, XpEntry>(Material.class);
    private final Map<EntityType, XpEntry> slayingEntries = new EnumMap<EntityType, XpEntry>(EntityType.class);
    private final List<XpEntry> displayEntries = new ArrayList<XpEntry>();

    public AllianceXpService(JavaPlugin plugin, AllianceStorage allianceStorage, MessageService messageService, AllianceBuffService buffService) {
        this.plugin = plugin;
        this.allianceStorage = allianceStorage;
        this.messageService = messageService;
        this.buffService = buffService;
        this.file = new File(plugin.getDataFolder(), "alliancesxp.yml");
        this.saveDefaultConfig();
        this.load();
        this.syncStoredLevels();
    }

    public void saveDefaultConfig() {
        if (!this.file.exists()) {
            this.plugin.saveResource("alliancesxp.yml", false);
        }
    }

    public void load() {
        this.blockEntries.clear();
        this.fishingEntries.clear();
        this.slayingEntries.clear();
        this.displayEntries.clear();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration((File)this.file);
        ConfigurationSection categories = configuration.getConfigurationSection("categories");
        if (categories == null) {
            return;
        }
        for (String categoryKey : categories.getKeys(false)) {
            List<Map<?, ?>> entries = configuration.getMapList("categories." + categoryKey + ".entries");
            block11: for (Map<?, ?> rawEntry : entries) {
                String materialName = String.valueOf(rawEntry.get("material"));
                Material iconMaterial = Material.matchMaterial((String)materialName);
                if (iconMaterial == null) continue;
                Object nameValue = rawEntry.containsKey("name") ? rawEntry.get("name") : iconMaterial.name();
                String displayName = String.valueOf(nameValue);
                Object xpValue = rawEntry.containsKey("xp") ? rawEntry.get("xp") : Integer.valueOf(0);
                double xp = Double.parseDouble(String.valueOf(xpValue));
                String keyName = rawEntry.containsKey("key") ? String.valueOf(rawEntry.get("key")) : null;
                EntityType entityType = null;
                if (rawEntry.containsKey("entity-type")) {
                    try {
                        entityType = EntityType.valueOf((String)String.valueOf(rawEntry.get("entity-type")).toUpperCase(Locale.ROOT));
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                        // empty catch block
                    }
                }
                XpEntry entry = new XpEntry(categoryKey, iconMaterial, displayName, xp, keyName, entityType);
                this.displayEntries.add(entry);
                switch (categoryKey.toLowerCase(Locale.ROOT)) {
                    case "fishing": {
                        this.fishingEntries.put(iconMaterial, entry);
                        continue block11;
                    }
                    case "slaying": {
                        if (entityType == null) continue block11;
                        this.slayingEntries.put(entityType, entry);
                        continue block11;
                    }
                }
                Material triggerMaterial = keyName == null ? iconMaterial : Material.matchMaterial((String)keyName);
                if (triggerMaterial == null) continue;
                this.blockEntries.put(triggerMaterial, entry);
            }
        }
    }

    public Optional<XpEntry> getBlockEntry(Material material) {
        return Optional.ofNullable(this.blockEntries.get(material));
    }

    public Optional<XpEntry> getFishingEntry(Material material) {
        return Optional.ofNullable(this.fishingEntries.get(material));
    }

    public Optional<XpEntry> getSlayingEntry(EntityType entityType) {
        return Optional.ofNullable(this.slayingEntries.get(entityType));
    }

    public List<XpEntry> getDisplayEntries() {
        return Collections.unmodifiableList(this.displayEntries);
    }

    public int getLevelForXp(double xp) {
        int level = 1;
        for (int current = 2; current < LEVEL_THRESHOLDS.length; ++current) {
            if (!(xp >= LEVEL_THRESHOLDS[current])) continue;
            level = current;
        }
        return Math.min(10, level);
    }

    public double getCurrentLevelBaseXp(int level) {
        return LEVEL_THRESHOLDS[Math.max(1, Math.min(level, 10))];
    }

    public double getNextLevelXp(int level) {
        if (level >= 10) {
            return LEVEL_THRESHOLDS[10];
        }
        return LEVEL_THRESHOLDS[level + 1];
    }

    public double getXpMultiplier(int level) {
        return switch (level) {
            case 2, 3, 4, 5 -> 1.05;
            case 6 -> 1.1;
            case 7 -> 1.15;
            case 8, 9 -> 1.175;
            case 10 -> 1.2;
            default -> 1.0;
        };
    }

    public int getProgressPercent(Alliance alliance) {
        if (alliance.getLevel() >= 10) {
            return 100;
        }
        double currentBase = this.getCurrentLevelBaseXp(alliance.getLevel());
        double nextBase = this.getNextLevelXp(alliance.getLevel());
        double progressed = Math.max(0.0, alliance.getXp() - currentBase);
        double needed = Math.max(1.0, nextBase - currentBase);
        return (int)Math.max(0L, Math.min(100L, Math.round(progressed / needed * 100.0)));
    }

    public String getProgressBar(Alliance alliance) {
        int percent = this.getProgressPercent(alliance);
        int filled = Math.max(0, Math.min(20, (int)Math.round((double)percent / 5.0)));
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < 20; ++index) {
            builder.append(index < filled ? "&a|" : "&c|");
        }
        return builder.toString();
    }

    public void awardXp(Player player, double baseXp) {
        Optional<Alliance> allianceOptional = this.allianceStorage.getAllianceByPlayer(player.getUniqueId());
        if (allianceOptional.isEmpty()) {
            return;
        }
        Alliance alliance = allianceOptional.get();
        int oldLevel = alliance.getLevel();
        double awarded = baseXp * this.getXpMultiplier(oldLevel);
        alliance.addXp(awarded);
        int newLevel = this.getLevelForXp(alliance.getXp());
        alliance.setLevel(newLevel);
        this.allianceStorage.save();
        if (newLevel != oldLevel) {
            this.buffService.refreshAlliance(alliance);
            for (UUID memberId : alliance.getMembers()) {
                Player member = Bukkit.getPlayer((UUID)memberId);
                if (member == null) continue;
                member.sendMessage(this.messageService.prefixed("level-up", text -> text.replace("%level%", String.valueOf(newLevel))));
            }
        }
    }

    public List<Alliance> getTopAlliances() {
        return this.allianceStorage.getAlliances().stream().sorted((left, right) -> Double.compare(right.getXp(), left.getXp())).toList();
    }

    public List<String> getLevelPerksLore() {
        return List.of("&7Level 1: &fHealth Boost I", "&7Level 2: &f+5% Alliance XP", "&7Level 3: &fSpeed I", "&7Level 4: &fJump Boost I", "&7Level 5: &fStrength I", "&7Level 6: &f+10% Alliance XP", "&7Level 7: &f+15% Alliance XP", "&7Level 8: &f+17.5% Alliance XP", "&7Level 9: &fNo extra perk", "&7Level 10: &f+20% XP, Strength II, Speed II, Health Boost II");
    }

    private void syncStoredLevels() {
        boolean changed = false;
        for (Alliance alliance : this.allianceStorage.getAlliances()) {
            int level = this.getLevelForXp(alliance.getXp());
            if (alliance.getLevel() == level) continue;
            alliance.setLevel(level);
            changed = true;
        }
        if (changed) {
            this.allianceStorage.save();
        }
    }

    public record XpEntry(String category, Material iconMaterial, String displayName, double xp, String key, EntityType entityType) {
        public String prettyCategory() {
            return switch (this.category.toLowerCase(Locale.ROOT)) {
                case "woodcutting" -> "Woodcutting";
                case "slaying" -> "Slaying";
                case "fishing" -> "Fishing";
                case "excavating" -> "Excavating";
                case "farming" -> "Farming";
                default -> "Mining";
            };
        }
    }
}

