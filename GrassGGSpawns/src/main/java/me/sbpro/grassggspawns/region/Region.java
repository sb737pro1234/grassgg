package me.sbpro.grassggspawns.region;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.Map;

public class Region {

    private final String name;
    private final int priority;

    private final String worldName;

    private final boolean entireWorld;

    private final int minX;
    private final int minY;
    private final int minZ;

    private final int maxX;
    private final int maxY;
    private final int maxZ;

    private final Map<RegionFlag, Boolean> flags =
            new EnumMap<>(RegionFlag.class);

    public Region(
            String name,
            int priority,
            String worldName,
            boolean entireWorld,
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ
    ) {
        this.name = name;
        this.priority = priority;
        this.worldName = worldName;
        this.entireWorld = entireWorld;

        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean isEntireWorld() {
        return entireWorld;
    }

    public void setFlag(RegionFlag flag, boolean value) {
        flags.put(flag, value);
    }

    /**
     * Returns true if this region explicitly has this flag set.
     */
    public boolean hasFlag(RegionFlag flag) {
        return flags.containsKey(flag);
    }

    /**
     * Returns the value of the flag.
     *
     * true  = allow
     * false = deny
     */
    public boolean getFlag(RegionFlag flag) {
        return flags.getOrDefault(flag, true);
    }

    public boolean contains(Location location) {

        World world = location.getWorld();

        if (world == null) {
            return false;
        }

        if (!world.getName().equalsIgnoreCase(worldName)) {
            return false;
        }

        if (entireWorld) {
            return true;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }
}