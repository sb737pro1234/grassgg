package me.sbpro.grassggspawns.region;

import me.sbpro.grassggspawns.GrassGGSpawns;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RegionManager {

    private final GrassGGSpawns plugin;

    private final List<Region> regions = new ArrayList<>();

    public RegionManager(GrassGGSpawns plugin) {
        this.plugin = plugin;
    }

    public void loadRegions() {

        regions.clear();

        FileConfiguration config = plugin.getRegionsConfig();

        ConfigurationSection regionsSection =
                config.getConfigurationSection("regions");

        if (regionsSection == null) {
            plugin.getLogger().warning("No regions section found in regions.yml.");
            return;
        }

        for (String regionName : regionsSection.getKeys(false)) {

            String path = "regions." + regionName;

            int priority = config.getInt(path + ".priority", 0);

            String world = config.getString(path + ".location.world");

            if (world == null || world.isBlank()) {
                plugin.getLogger().warning(
                        "Region '" + regionName + "' has no world."
                );
                continue;
            }

            String pos1 = config.getString(path + ".location.pos1");
            String pos2 = config.getString(path + ".location.pos2");

            if (pos1 == null || pos2 == null) {
                plugin.getLogger().warning(
                        "Region '" + regionName + "' is missing pos1 or pos2."
                );
                continue;
            }

            boolean entireWorld =
                    pos1.trim().equals("*")
                            && pos2.trim().equals("*");

            int minX = 0;
            int minY = 0;
            int minZ = 0;

            int maxX = 0;
            int maxY = 0;
            int maxZ = 0;

            if (!entireWorld) {

                int[] first = parsePosition(pos1);
                int[] second = parsePosition(pos2);

                if (first == null || second == null) {
                    plugin.getLogger().warning(
                            "Invalid coordinates for region '" + regionName + "'."
                    );
                    continue;
                }

                minX = Math.min(first[0], second[0]);
                minY = Math.min(first[1], second[1]);
                minZ = Math.min(first[2], second[2]);

                maxX = Math.max(first[0], second[0]);
                maxY = Math.max(first[1], second[1]);
                maxZ = Math.max(first[2], second[2]);
            }

            Region region = new Region(
                    regionName,
                    priority,
                    world,
                    entireWorld,
                    minX,
                    minY,
                    minZ,
                    maxX,
                    maxY,
                    maxZ
            );

            ConfigurationSection flags =
                    config.getConfigurationSection(path + ".flags");

            if (flags != null) {

                for (String flagName : flags.getKeys(false)) {

                    RegionFlag flag;

                    try {
                        flag = RegionFlag.valueOf(
                                flagName.toUpperCase()
                        );
                    } catch (IllegalArgumentException exception) {

                        plugin.getLogger().warning(
                                "Unknown flag '" + flagName
                                        + "' in region '" + regionName + "'."
                        );

                        continue;
                    }

                    String value = flags.getString(flagName);

                    if (value == null) {
                        continue;
                    }

                    if (value.equalsIgnoreCase("allow")) {
                        region.setFlag(flag, true);
                    } else if (value.equalsIgnoreCase("deny")) {
                        region.setFlag(flag, false);
                    } else {
                        plugin.getLogger().warning(
                                "Invalid value '" + value
                                        + "' for flag '" + flagName
                                        + "' in region '" + regionName
                                        + "'. Use allow or deny."
                        );
                    }
                }
            }

            regions.add(region);

            plugin.getLogger().info(
                    "Loaded region: " + regionName
                            + " (priority " + priority + ")"
            );
        }

        regions.sort(
                Comparator.comparingInt(Region::getPriority).reversed()
        );
    }

    /**
     * Gets the highest-priority region containing a location.
     */
    public Region getRegion(Location location) {

        for (Region region : regions) {

            if (region.contains(location)) {
                return region;
            }
        }

        return null;
    }

    /**
     * Returns whether an action is allowed at a location.
     *
     * No region = allowed.
     * No flag = allowed.
     * allow = allowed.
     * deny = blocked.
     */
    public boolean isAllowed(Location location, RegionFlag flag) {

        Region region = getRegion(location);

        if (region == null) {
            return true;
        }

        return region.getFlag(flag);
    }

    private int[] parsePosition(String value) {

        String[] parts = value.trim().split("\\s+");

        if (parts.length != 3) {
            return null;
        }

        try {

            return new int[]{
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            };

        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public List<Region> getRegions() {
        return regions;
    }
}