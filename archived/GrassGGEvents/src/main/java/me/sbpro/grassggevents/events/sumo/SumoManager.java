package me.sbpro.grassggevents.events.sumo;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class SumoManager implements Listener {

    private final List<BukkitTask> scheduledTasks = new ArrayList<>();

    private final Plugin plugin;


    private Location pos1;
    private Location pos2;


    private boolean running;


    private final Map<Location, Material> savedBlocks = new HashMap<>();


    private File file;
    private FileConfiguration config;



    public SumoManager(Plugin plugin) {

        this.plugin = plugin;

        setupFile();

        loadArena();

    }





    private void setupFile() {


        File folder = new File(
                plugin.getDataFolder(),
                "sumo"
        );


        if (!folder.exists()) {
            folder.mkdirs();
        }


        file = new File(
                folder,
                "arena.yml"
        );


        if (!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        config = YamlConfiguration.loadConfiguration(file);

    }





    public void setPos1(Location location) {
        pos1 = location;
    }


    public void setPos2(Location location) {
        pos2 = location;
    }




    public void saveArena() {


        if (pos1 == null || pos2 == null) {
            Bukkit.broadcastMessage(
                    "§cSet both positions first."
            );
            return;
        }


        savedBlocks.clear();


        World world = pos1.getWorld();



        for (int x = Math.min(pos1.getBlockX(), pos2.getBlockX());
             x <= Math.max(pos1.getBlockX(), pos2.getBlockX());
             x++) {


            for (int y = Math.min(pos1.getBlockY(), pos2.getBlockY());
                 y <= Math.max(pos1.getBlockY(), pos2.getBlockY());
                 y++) {


                for (int z = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
                     z <= Math.max(pos1.getBlockZ(), pos2.getBlockZ());
                     z++) {


                    Block block = world.getBlockAt(x,y,z);



                    if (isSumoBlock(block.getType())) {


                        savedBlocks.put(
                                block.getLocation(),
                                block.getType()
                        );

                    }

                }
            }
        }



        saveToFile();



        Bukkit.broadcastMessage(
                "§2§lSUMO §8» §fSaved §a"
                        + savedBlocks.size()
                        + " §fblocks."
        );

    }




    private void saveToFile() {


        config.set("blocks", null);


        for (Map.Entry<Location, Material> entry : savedBlocks.entrySet()) {


            Location loc = entry.getKey();


            String key =
                    loc.getWorld().getName()
                            + ","
                            + loc.getBlockX()
                            + ","
                            + loc.getBlockY()
                            + ","
                            + loc.getBlockZ();



            config.set(
                    "blocks." + key,
                    entry.getValue().name()
            );

        }



        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }






    private void loadArena() {


        savedBlocks.clear();


        if (!config.contains("blocks"))
            return;



        for (String key : config.getConfigurationSection("blocks").getKeys(false)) {


            String[] split = key.split(",");


            World world = Bukkit.getWorld(split[0]);


            if (world == null)
                continue;



            Location location = new Location(
                    world,
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    Integer.parseInt(split[3])
            );



            Material material =
                    Material.valueOf(
                            config.getString(
                                    "blocks." + key
                            )
                    );


            savedBlocks.put(
                    location,
                    material
            );

        }


    }






    public void start() {

        if (running)
            return;

        if (savedBlocks.isEmpty()) {
            Bukkit.broadcastMessage("§cNo Sumo arena saved.");
            return;
        }

        running = true;

        Bukkit.broadcastMessage(
                "§2§lSUMO §8» §fLayers will slowly disappear as the match goes on."
        );

        giveSticks();

        Bukkit.broadcastMessage(
                "§2§lSUMO §8» §fGreen will disappear in §220§f seconds."
        );

        removeLayer(Material.GREEN_STAINED_GLASS, 20);

        removeLayer(Material.YELLOW_STAINED_GLASS, 40);

        removeLayer(Material.BLUE_STAINED_GLASS, 60);

        removeLayer(Material.RED_STAINED_GLASS, 80);
    }






    private void removeLayer(Material material, int seconds) {

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {

            if (!running)
                return;

            for (Location location : savedBlocks.keySet()) {

                if (location.getBlock().getType() == material) {
                    location.getBlock().setType(Material.AIR, false);
                }

            }

            String colour;

            switch (material) {
                case GREEN_STAINED_GLASS -> colour = "§2Green";
                case YELLOW_STAINED_GLASS -> colour = "§eYellow";
                case BLUE_STAINED_GLASS -> colour = "§9Blue";
                case RED_STAINED_GLASS -> colour = "§cRed";
                default -> colour = material.name();
            }

            Bukkit.broadcastMessage("§2§lSUMO §8» §f" + colour + " §fhas disappeared.");

            switch (material) {

                case GREEN_STAINED_GLASS ->
                        Bukkit.broadcastMessage("§2§lSUMO §8» §fYellow will disappear in §220§f seconds.");

                case YELLOW_STAINED_GLASS ->
                        Bukkit.broadcastMessage("§2§lSUMO §8» §fBlue will disappear in §220§f seconds.");

                case BLUE_STAINED_GLASS ->
                        Bukkit.broadcastMessage("§2§lSUMO §8» §fRed will disappear in §220§f seconds.");

                case RED_STAINED_GLASS ->
                        Bukkit.broadcastMessage("§2§lSUMO §8» §fAll layers have disappeared! §7Use §a/event sumo end §7to reset the arena.");

            }

        }, seconds * 20L);

        scheduledTasks.add(task);

    }





    public void end() {

        running = false;

        Bukkit.broadcastMessage("§2§lSUMO §8» §fThe event has been §cended§f.");

        for (BukkitTask task : scheduledTasks) {
            task.cancel();
        }

        scheduledTasks.clear();

        for (Map.Entry<Location, Material> entry : savedBlocks.entrySet()) {
            entry.getKey().getBlock().setType(entry.getValue(), false);
        }

        Bukkit.broadcastMessage("§2§lSUMO §8» §aArena restored.");
    }






    private void giveSticks() {

        ItemStack stick = new ItemStack(Material.STICK);

        ItemMeta meta = stick.getItemMeta();

        if (meta != null) {
            meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            meta.setDisplayName("§aSumo Stick");
            stick.setItemMeta(meta);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(stick.clone());
        }
    }






    private boolean isSumoBlock(Material material) {


        return material == Material.GREEN_STAINED_GLASS
                || material == Material.YELLOW_STAINED_GLASS
                || material == Material.BLUE_STAINED_GLASS
                || material == Material.RED_STAINED_GLASS;

    }




    public boolean isRunning() {
        return running;
    }

}