package me.sbpro.grassggalliances.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.sbpro.grassggalliances.MessageService;
import me.sbpro.grassggalliances.Messages;
import me.sbpro.grassggalliances.data.AllianceStorage;
import me.sbpro.grassggalliances.model.Alliance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class AllianceXpService {
    private static final double[] LEVEL_THRESHOLDS = new double[]{0.0, 0.0, 500.0, 1250.0, 2250.0, 3500.0, 5000.0, 6750.0, 8750.0, 11000.0, 14000.0};
    private final AllianceStorage allianceStorage;
    private final MessageService messageService;
    private final AllianceBuffService buffService;
    private final Map<Material, XpEntry> blockEntries = new EnumMap<>(Material.class);
    private final Map<Material, XpEntry> fishingEntries = new EnumMap<>(Material.class);
    private final Map<EntityType, XpEntry> slayingEntries = new EnumMap<>(EntityType.class);
    private final List<XpEntry> displayEntries = new ArrayList<>();

    public AllianceXpService(org.bukkit.plugin.java.JavaPlugin plugin, AllianceStorage allianceStorage, MessageService messageService, AllianceBuffService buffService) {
        this.allianceStorage = allianceStorage;
        this.messageService = messageService;
        this.buffService = buffService;
        this.load();
        this.syncStoredLevels();
    }

    private void load() {
        this.blockEntries.clear();
        this.fishingEntries.clear();
        this.slayingEntries.clear();
        this.displayEntries.clear();

        // Mining
        addEntry("mining", Material.COAL_ORE, "&8Coal Ore", 3, null, null);
        addEntry("mining", Material.DEEPSLATE_COAL_ORE, "&8Deepslate Coal Ore", 4, null, null);
        addEntry("mining", Material.IRON_ORE, "&fIron Ore", 4, null, null);
        addEntry("mining", Material.DEEPSLATE_IRON_ORE, "&fDeepslate Iron Ore", 5, null, null);
        addEntry("mining", Material.COPPER_ORE, "&6Copper Ore", 4, null, null);
        addEntry("mining", Material.DEEPSLATE_COPPER_ORE, "&6Deepslate Copper Ore", 5, null, null);
        addEntry("mining", Material.GOLD_ORE, "&6Gold Ore", 5, null, null);
        addEntry("mining", Material.DEEPSLATE_GOLD_ORE, "&6Deepslate Gold Ore", 6, null, null);
        addEntry("mining", Material.REDSTONE_ORE, "&cRedstone Ore", 5, null, null);
        addEntry("mining", Material.DEEPSLATE_REDSTONE_ORE, "&cDeepslate Redstone Ore", 6, null, null);
        addEntry("mining", Material.LAPIS_ORE, "&9Lapis Ore", 5, null, null);
        addEntry("mining", Material.DEEPSLATE_LAPIS_ORE, "&9Deepslate Lapis Ore", 6, null, null);
        addEntry("mining", Material.DIAMOND_ORE, "&bDiamond Ore", 8, null, null);
        addEntry("mining", Material.DEEPSLATE_DIAMOND_ORE, "&bDeepslate Diamond Ore", 10, null, null);
        addEntry("mining", Material.EMERALD_ORE, "&aEmerald Ore", 8, null, null);
        addEntry("mining", Material.DEEPSLATE_EMERALD_ORE, "&aDeepslate Emerald Ore", 10, null, null);
        addEntry("mining", Material.NETHER_QUARTZ_ORE, "&fNether Quartz Ore", 4, null, null);
        addEntry("mining", Material.NETHER_GOLD_ORE, "&6Nether Gold Ore", 4, null, null);
        addEntry("mining", Material.ANCIENT_DEBRIS, "&4Ancient Debris", 20, null, null);
        // Excavating
        addEntry("excavating", Material.ROOTED_DIRT, "&6Rooted Dirt", 2, null, null);
        addEntry("excavating", Material.RED_SAND, "&cRed Sand", 1, null, null);
        addEntry("excavating", Material.CLAY, "&9Clay", 2, null, null);
        addEntry("excavating", Material.SNOW_BLOCK, "&fSnow Block", 1, null, null);
        addEntry("excavating", Material.SOUL_SAND, "&6Soul Sand", 2, null, null);
        addEntry("excavating", Material.SOUL_SOIL, "&6Soul Soil", 2, null, null);
        addEntry("excavating", Material.MYCELIUM, "&5Mycelium", 3, null, null);
        // Woodcutting
        addEntry("woodcutting", Material.OAK_LOG, "&6Oak Log", 4, null, null);
        addEntry("woodcutting", Material.SPRUCE_LOG, "&6Spruce Log", 4, null, null);
        addEntry("woodcutting", Material.BIRCH_LOG, "&6Birch Log", 4, null, null);
        addEntry("woodcutting", Material.JUNGLE_LOG, "&6Jungle Log", 5, null, null);
        addEntry("woodcutting", Material.ACACIA_LOG, "&6Acacia Log", 5, null, null);
        addEntry("woodcutting", Material.DARK_OAK_LOG, "&6Dark Oak Log", 5, null, null);
        addEntry("woodcutting", Material.MANGROVE_LOG, "&6Mangrove Log", 5, null, null);
        addEntry("woodcutting", Material.CHERRY_LOG, "&dCherry Log", 5, null, null);
        addEntry("woodcutting", Material.CRIMSON_STEM, "&cCrimson Stem", 6, null, null);
        addEntry("woodcutting", Material.WARPED_STEM, "&bWarped Stem", 6, null, null);
        // Farming
        addEntry("farming", Material.WHEAT, "&aWheat", 3, "WHEAT", null);
        addEntry("farming", Material.CARROT, "&aCarrots", 3, "CARROTS", null);
        addEntry("farming", Material.POTATO, "&aPotatoes", 3, "POTATOES", null);
        addEntry("farming", Material.BEETROOT, "&cBeetroots", 3, "BEETROOTS", null);
        addEntry("farming", Material.MELON_SLICE, "&aMelon", 2, "MELON", null);
        addEntry("farming", Material.PUMPKIN, "&6Pumpkin", 4, "PUMPKIN", null);
        addEntry("farming", Material.SUGAR_CANE, "&aSugar Cane", 2, "SUGAR_CANE", null);
        addEntry("farming", Material.NETHER_WART, "&cNether Wart", 4, "NETHER_WART", null);
        addEntry("farming", Material.COCOA_BEANS, "&6Cocoa", 3, "COCOA", null);
        addEntry("farming", Material.CACTUS, "&aCactus", 2, "CACTUS", null);
        addEntry("farming", Material.BAMBOO, "&aBamboo", 2, "BAMBOO", null);
        addEntry("farming", Material.SWEET_BERRIES, "&cSweet Berry Bush", 2, "SWEET_BERRY_BUSH", null);
        addEntry("farming", Material.KELP, "&2Kelp", 2, "KELP", null);
        addEntry("farming", Material.SEA_PICKLE, "&aSea Pickle", 2, "SEA_PICKLE", null);
        // Fishing
        addEntry("fishing", Material.COD, "&eCod", 3, null, null);
        addEntry("fishing", Material.SALMON, "&eSalmon", 3, null, null);
        addEntry("fishing", Material.TROPICAL_FISH, "&eTropical Fish", 5, null, null);
        addEntry("fishing", Material.PUFFERFISH, "&ePufferfish", 5, null, null);
        addEntry("fishing", Material.BOW, "&6Treasure Bow", 8, null, null);
        addEntry("fishing", Material.ENCHANTED_BOOK, "&dEnchanted Book", 10, null, null);
        addEntry("fishing", Material.FISHING_ROD, "&6Fishing Rod", 8, null, null);
        addEntry("fishing", Material.NAME_TAG, "&eName Tag", 8, null, null);
        addEntry("fishing", Material.SADDLE, "&6Saddle", 8, null, null);
        // Slaying
        addEntry("slaying", Material.ROTTEN_FLESH, "&cZombie", 4, null, EntityType.ZOMBIE);
        addEntry("slaying", Material.BONE, "&fSkeleton", 4, null, EntityType.SKELETON);
        addEntry("slaying", Material.STRING, "&fSpider", 4, null, EntityType.SPIDER);
        addEntry("slaying", Material.GUNPOWDER, "&7Creeper", 5, null, EntityType.CREEPER);
        addEntry("slaying", Material.ENDER_PEARL, "&5Enderman", 8, null, EntityType.ENDERMAN);
        addEntry("slaying", Material.BLAZE_ROD, "&6Blaze", 10, null, EntityType.BLAZE);
        addEntry("slaying", Material.PHANTOM_MEMBRANE, "&9Phantom", 8, null, EntityType.PHANTOM);
        addEntry("slaying", Material.SLIME_BALL, "&aSlime", 5, null, EntityType.SLIME);
        addEntry("slaying", Material.MAGMA_CREAM, "&6Magma Cube", 6, null, EntityType.MAGMA_CUBE);
        addEntry("slaying", Material.GHAST_TEAR, "&fGhast", 12, null, EntityType.GHAST);
        addEntry("slaying", Material.PRISMARINE_SHARD, "&3Guardian", 8, null, EntityType.GUARDIAN);
        addEntry("slaying", Material.WITHER_SKELETON_SKULL, "&8Wither Skeleton", 12, null, EntityType.WITHER_SKELETON);
    }

    private void addEntry(String category, Material iconMaterial, String displayName, double xp, String keyName, EntityType entityType) {
        XpEntry entry = new XpEntry(category, iconMaterial, displayName, xp, keyName, entityType);
        this.displayEntries.add(entry);

        switch (category.toLowerCase(java.util.Locale.ROOT)) {
            case "fishing" -> this.fishingEntries.put(iconMaterial, entry);
            case "slaying" -> {
                if (entityType != null) {
                    this.slayingEntries.put(entityType, entry);
                }
            }
            default -> {
                Material triggerMaterial = keyName == null ? iconMaterial : Material.matchMaterial(keyName);
                if (triggerMaterial != null) {
                    this.blockEntries.put(triggerMaterial, entry);
                }
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
            builder.append(index < filled ? Messages.ALLIANCE_COLOR + "|" : "§c|");
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
        return List.of("§fLevel 1: " + Messages.ALLIANCE_COLOR + "Health Boost I", "§fLevel 2: " + Messages.ALLIANCE_COLOR + "+5% Alliance XP", "§fLevel 3: " + Messages.ALLIANCE_COLOR + "Speed I", "§fLevel 4: " + Messages.ALLIANCE_COLOR + "Haste I", "§fLevel 5: " + Messages.ALLIANCE_COLOR + "Strength I", "§fLevel 6: " + Messages.ALLIANCE_COLOR + "+10% Alliance XP", "§fLevel 7: " + Messages.ALLIANCE_COLOR + "+15% Alliance XP", "§fLevel 8: " + Messages.ALLIANCE_COLOR + "+17.5% Alliance XP", "§fLevel 9: " + Messages.ALLIANCE_COLOR + "No extra perk", "§fLevel 10: " + Messages.ALLIANCE_COLOR + "+20% XP, Strength II, Speed II, Health Boost II");
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

