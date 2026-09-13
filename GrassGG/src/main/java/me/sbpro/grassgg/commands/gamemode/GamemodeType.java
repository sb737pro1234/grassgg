package me.sbpro.grassgg.commands.gamemode;

import org.bukkit.GameMode;
import org.bukkit.Material;

public enum GamemodeType {

    SURVIVAL(
            "survival",
            GameMode.SURVIVAL,
            Material.GRASS_BLOCK,
            "Survival"
    ),

    CREATIVE(
            "creative",
            GameMode.CREATIVE,
            Material.DIAMOND_BLOCK,
            "Creative"
    ),

    ADVENTURE(
            "adventure",
            GameMode.ADVENTURE,
            Material.MAP,
            "Adventure"
    ),

    SPECTATOR(
            "spectator",
            GameMode.SPECTATOR,
            Material.ENDER_EYE,
            "Spectator"
    );

    private final String name;
    private final GameMode gameMode;
    private final Material icon;
    private final String displayName;

    GamemodeType(String name, GameMode gameMode, Material icon, String displayName) {
        this.name = name;
        this.gameMode = gameMode;
        this.icon = icon;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static GamemodeType fromString(String input) {
        for (GamemodeType type : values()) {
            if (type.name.equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null;
    }
}