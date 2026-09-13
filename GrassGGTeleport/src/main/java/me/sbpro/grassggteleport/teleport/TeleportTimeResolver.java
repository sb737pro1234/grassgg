package me.sbpro.grassggteleport.teleport;

import me.sbpro.grassggteleport.permission.Permissions;
import org.bukkit.entity.Player;

public final class TeleportTimeResolver {

    private static final int DEFAULT_TIME = 5;

    private TeleportTimeResolver() {
    }

    public static int getTeleportTime(Player player) {
        int lowestTime = DEFAULT_TIME;

        for (int seconds = 0; seconds <= 300; seconds++) {
            if (player.hasPermission(Permissions.time(seconds))) {
                lowestTime = Math.min(lowestTime, seconds);
            }
        }

        return lowestTime;
    }
}