package me.sbpro.grassgghomes;

import me.sbpro.grassgghomes.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private static final Map<UUID, BukkitRunnable> ACTIVE = new HashMap<>();

    private final GrassGGHomes plugin;
    private final Player player;
    private final Location destination;
    private final Location startLocation;

    public TeleportManager(GrassGGHomes plugin, Player player, Location destination) {
        this.plugin = plugin;
        this.player = player;
        this.destination = destination.clone();
        this.startLocation = player.getLocation().clone();
    }

    public void start() {
        cancel(player, false);

        player.sendMessage("§2§lHOMES §8»§f Teleporting in §25§f seconds.");
        player.playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1f, 0.8f);

        BukkitRunnable task = new BukkitRunnable() {
            int seconds = 5;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    ACTIVE.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                Location current = player.getLocation();

                double xDifference = Math.abs(current.getX() - startLocation.getX());
                double zDifference = Math.abs(current.getZ() - startLocation.getZ());

                if (xDifference > 0.3 || zDifference > 0.3) {

                    player.sendMessage("§2§lHOMES §8»§c Teleport cancelled because you moved.");
                    player.playSound(
                            player.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_BASS,
                            1f,
                            1f
                    );

                    player.sendActionBar(Component.empty());

                    ACTIVE.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                if (seconds == 0) {
                    player.teleport(destination);
                    player.playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

                    player.sendMessage("§2§lHOMES §8»§f You have been teleported.");

                    // Clear action bar
                    player.sendActionBar(Component.empty());

                    ACTIVE.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                // Action bar countdown
                player.sendActionBar(
                        Component.text("§2§lHOMES §8»§f Teleporting in §2" + seconds + "§fs")
                                .color(TextColor.color(0x55FF55))
                );

                player.playSound(
                        player.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_HAT,
                        1f,
                        1.2f
                );

                seconds--;
            }
        };

        ACTIVE.put(player.getUniqueId(), task);
        task.runTaskTimer(plugin, 20L, 20L);
    }

    public static boolean isTeleporting(Player player) {
        return ACTIVE.containsKey(player.getUniqueId());
    }

    public static void cancel(Player player) {
        cancel(player, true);
    }

    private static void cancel(Player player, boolean message) {
        BukkitRunnable task = ACTIVE.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
            if (message) {
                player.sendMessage("§2§lHOMES §8»§c Teleport cancelled.");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            }
        }
    }
}
