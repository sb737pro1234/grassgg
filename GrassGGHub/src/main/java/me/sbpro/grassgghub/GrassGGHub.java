package me.sbpro.grassgghub;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;

import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public final class GrassGGHub extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("Grass.GG » Server Starting.");

        getServer().getPluginManager().registerEvents(this, this);

        startTabTask();
    }

    private void startTabTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {

            int online = Bukkit.getOnlinePlayers().size();

            for (Player player : Bukkit.getOnlinePlayers()) {

                String header =
                        " §f§m                                                §r\n " +
                                "§2§lGRASS.GG NETWORK\n" +
                                "§7ɢʀᴀѕѕɢɢ.ᴍʏ.ᴘᴇʙʙʟᴇ.ʜᴏѕᴛ\n";

                String footer =
                        "\n" +
                                "§7Server: §2Hub §8| §7Online: §2" + online + "\n" +
                                "§7Account: §2" + player.getName() + " §8| §7Ping: §2" + player.getPing() + "\n" +
                                " §f§m                                                §r ";

                player.sendPlayerListHeaderAndFooter(
                        Component.text(header),
                        Component.text(footer)
                );
            }

        }, 0L, 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.getInventory().clear();

        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "zmenu giveopenitem zmenu:server " + player.getName()
        );

        Bukkit.getScheduler().runTaskLater(this, () -> {
            ItemStack item = player.getInventory().getItem(0);

            if (item != null) {
                player.getInventory().setItem(4, item);
                player.getInventory().setItem(0, null);
            }
        }, 1L);

        // Other
        event.setJoinMessage(null);

        World world = Bukkit.getWorld("NewHub");
        if (world != null) {
            event.getPlayer().teleport(
                    new Location(world, 0.5, 1, 0.5, 270F, 0F)
            );
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        event.message(null);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("grassgg.staff")) {
            return;
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.ALLOW);
    }


    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer().hasPermission("grassgg.staff")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {

        // Optional: only affect the hub world
        if (!event.getLocation().getWorld().getName().equals("world")) {
            return;
        }

        // Cancel all hostile mobs
        if (event.getEntity() instanceof Monster || event.getEntity() instanceof Slime) {
            event.setCancelled(true);
        }
    }
}