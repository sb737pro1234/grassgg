package me.sbpro.grassgghomes;

import me.sbpro.grassgghomes.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomesManager {

    private final GrassGGHomes plugin;

    private final File homesFile;

    private final YamlConfiguration homesConfig;

    private final Map<UUID, Map<Integer, Home>> homes = new HashMap<>();


    public HomesManager(GrassGGHomes plugin) {

        this.plugin = plugin;

        File folder = new File(
                plugin.getDataFolder(),
                "homes/data"
        );

        if (!folder.exists()) {
            folder.mkdirs();
        }

        homesFile = new File(
                folder,
                "homes.yml"
        );

        if (!homesFile.exists()) {

            try {
                homesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        homesConfig = YamlConfiguration.loadConfiguration(homesFile);

        loadHomes();
    }


    public void loadHomes() {

        homes.clear();

        if (!homesConfig.contains("homes")) {
            return;
        }

        for (String uuidString : homesConfig.getConfigurationSection("homes").getKeys(false)) {

            UUID uuid = UUID.fromString(uuidString);

            Map<Integer, Home> playerHomes = new HashMap<>();

            for (int number = 1; number <= 7; number++) {

                String path =
                        "homes."
                                + uuidString
                                + ".Home"
                                + number;

                if (!homesConfig.contains(path)) {
                    continue;
                }

                World world =
                        Bukkit.getWorld(
                                homesConfig.getString(path + ".world")
                        );

                if (world == null) {
                    continue;
                }

                Location location =
                        new Location(
                                world,
                                homesConfig.getDouble(path + ".x"),
                                homesConfig.getDouble(path + ".y"),
                                homesConfig.getDouble(path + ".z"),
                                (float) homesConfig.getDouble(path + ".yaw"),
                                (float) homesConfig.getDouble(path + ".pitch")
                        );

                String name =
                        homesConfig.getString(
                                path + ".name",
                                "Home " + number
                        );

                playerHomes.put(
                        number,
                        new Home(
                                location,
                                name
                        )
                );
            }

            homes.put(
                    uuid,
                    playerHomes
            );
        }
    }


    public void saveHomes() {

        homesConfig.set(
                "homes",
                null
        );

        for (Map.Entry<UUID, Map<Integer, Home>> playerEntry : homes.entrySet()) {

            String uuid =
                    playerEntry
                            .getKey()
                            .toString();

            for (Map.Entry<Integer, Home> homeEntry : playerEntry.getValue().entrySet()) {

                int number = homeEntry.getKey();

                Home home = homeEntry.getValue();

                Location location = home.getLocation();

                String path =
                        "homes."
                                + uuid
                                + ".Home"
                                + number;

                homesConfig.set(
                        path + ".name",
                        home.getName()
                );

                homesConfig.set(
                        path + ".world",
                        location.getWorld().getName()
                );

                homesConfig.set(
                        path + ".x",
                        location.getX()
                );

                homesConfig.set(
                        path + ".y",
                        location.getY()
                );

                homesConfig.set(
                        path + ".z",
                        location.getZ()
                );

                homesConfig.set(
                        path + ".yaw",
                        location.getYaw()
                );

                homesConfig.set(
                        path + ".pitch",
                        location.getPitch()
                );
            }
        }

        try {

            homesConfig.save(homesFile);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public void setHome(
            UUID uuid,
            int number,
            Location location
    ) {

        homes.computeIfAbsent(
                uuid,
                k -> new HashMap<>()
        );

        String defaultName =
                "Home " + number;

        Home existing =
                getHome(
                        uuid,
                        number
                );

        if (existing != null) {
            defaultName = existing.getName();
        }

        homes.get(uuid).put(
                number,
                new Home(
                        location,
                        defaultName
                )
        );

        saveHomes();
    }
    public void deleteHome(
            UUID uuid,
            int number
    ) {

        Map<Integer, Home> playerHomes =
                homes.get(uuid);

        if (playerHomes == null) {
            return;
        }

        playerHomes.remove(number);

        if (playerHomes.isEmpty()) {
            homes.remove(uuid);
        }

        saveHomes();
    }


    public boolean hasHome(
            UUID uuid,
            int number
    ) {

        return homes.containsKey(uuid)
                && homes.get(uuid).containsKey(number);
    }


    public Home getHome(
            UUID uuid,
            int number
    ) {

        if (!homes.containsKey(uuid)) {
            return null;
        }

        return homes.get(uuid).get(number);
    }


    public Map<Integer, Home> getHomes(UUID uuid) {

        return homes.getOrDefault(
                uuid,
                new HashMap<>()
        );
    }


    public void renameHome(
            UUID uuid,
            int number,
            String newName
    ) {

        Home home =
                getHome(
                        uuid,
                        number
                );

        if (home == null) {
            return;
        }

        home.setName(newName.trim());

        saveHomes();
    }


    public int getMaxHomes(UUID uuid) {

        if (Bukkit.getPlayer(uuid) == null) {
            return 0;
        }

        return getMaxHomes(
                Bukkit.getPlayer(uuid)
        );
    }


    public int getMaxHomes(org.bukkit.entity.Player player) {

        for (int i = 7; i >= 1; i--) {

            if (player.hasPermission("grassgg.homes." + i)) {
                return i;
            }
        }

        return 0;
    }
    public void reloadHomes() {

        try {

            homesConfig.load(homesFile);

            loadHomes();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public Home getHomeByName(UUID uuid, String name) {

        Map<Integer, Home> playerHomes = homes.get(uuid);

        if (playerHomes == null) {
            return null;
        }

        for (Home home : playerHomes.values()) {

            if (home.getName() != null
                    && home.getName().equalsIgnoreCase(name)) {

                return home;
            }
        }

        return null;
    }

    public int getHomeNumber(UUID uuid, String name) {

        Map<Integer, Home> playerHomes = homes.get(uuid);

        if (playerHomes == null) {
            return -1;
        }

        for (Map.Entry<Integer, Home> entry : playerHomes.entrySet()) {

            Home home = entry.getValue();

            if (home.getName() != null
                    && home.getName().equalsIgnoreCase(name)) {

                return entry.getKey();
            }
        }

        return -1;
    }

    public int getNextAvailableHome(UUID uuid, int maxHomes) {

        Map<Integer, Home> playerHomes = homes.get(uuid);

        for (int number = 1; number <= maxHomes; number++) {

            if (playerHomes == null
                    || !playerHomes.containsKey(number)) {

                return number;
            }
        }

        return -1;
    }
}