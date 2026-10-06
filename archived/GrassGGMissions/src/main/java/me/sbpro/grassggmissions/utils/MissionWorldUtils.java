package me.sbpro.grassggmissions.utils;

import org.bukkit.World;

public class MissionWorldUtils {

    private MissionWorldUtils() {
    }

    public static boolean isMissionWorld(World world) {

        return switch (world.getName()) {
            case "world", "world_nether", "world_the_end" -> true;
            default -> false;
        };

    }

}