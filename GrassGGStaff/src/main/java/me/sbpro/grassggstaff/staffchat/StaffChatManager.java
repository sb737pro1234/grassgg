package me.sbpro.grassggstaff.staffchat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import me.sbpro.grassggstaff.GrassGGStaff;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class StaffChatManager {

    private static final TextColor STAFF_BLUE =
            TextColor.color(0x2979FF);

    private static final TextColor STAFF_RED =
            TextColor.color(0xFF5555);

    private static final String STAFF_CHAT_NAME =
            "STAFF CHAT";

    private final GrassGGStaff plugin;

    private final Set<UUID> enabled =
            ConcurrentHashMap.newKeySet();

    private BukkitTask actionBarTask;

    public StaffChatManager(
            GrassGGStaff plugin
    ) {
        this.plugin = plugin;
    }

    public void start() {

        /*
         * Keep the action bar visible permanently
         * while staff chat is enabled.
         */
        actionBarTask =
                Bukkit.getScheduler().runTaskTimer(
                        plugin,
                        () -> {

                            for (UUID uuid : enabled) {

                                Player player =
                                        Bukkit.getPlayer(uuid);

                                if (player == null
                                        || !player.isOnline()) {

                                    enabled.remove(uuid);
                                    continue;
                                }

                                player.sendActionBar(
                                        actionBar()
                                );
                            }
                        },
                        0L,
                        20L
                );
    }

    public void stop() {

        if (actionBarTask != null) {
            actionBarTask.cancel();
            actionBarTask = null;
        }

        enabled.clear();
    }

    public boolean isEnabled(
            Player player
    ) {

        return enabled.contains(
                player.getUniqueId()
        );
    }

    public boolean isEnabled(
            UUID uuid
    ) {

        return enabled.contains(uuid);
    }

    public void toggle(
            Player player
    ) {

        UUID uuid =
                player.getUniqueId();

        if (enabled.contains(uuid)) {

            enabled.remove(uuid);

        } else {

            enabled.add(uuid);
        }
    }

    public void disable(
            Player player
    ) {

        enabled.remove(
                player.getUniqueId()
        );
    }

    public void disable(
            UUID uuid
    ) {

        enabled.remove(uuid);
    }

    public void sendStaffChat(
            AsyncChatEvent event
    ) {

        Player sender =
                event.getPlayer();

        Component message =
                staffChatMessage(
                        sender,
                        event.message()
                );

        /*
         * AsyncChatEvent can run asynchronously, so
         * send the actual player messages on the main thread.
         */
        Bukkit.getScheduler().runTask(
                plugin,
                () -> {

                    for (Player player :
                            Bukkit.getOnlinePlayers()) {

                        if (!player.hasPermission(
                                "grassgg.staff.chat"
                        )) {

                            continue;
                        }

                        player.sendMessage(message);
                    }
                }
        );
    }

    private Component staffChatMessage(
            Player player,
            Component message
    ) {

        return Component.text(
                        STAFF_CHAT_NAME,
                        STAFF_BLUE
                )
                .decorate(
                        net.kyori.adventure.text.format.TextDecoration.BOLD
                )
                .append(
                        Component.text(" ")
                )
                .append(
                        player.displayName()
                )
                .append(
                        Component.text(
                                " » ",
                                STAFF_RED
                        )
                )
                .append(
                        message
                );
    }

    private Component actionBar() {

        return Component.text(
                        "STAFF CHAT",
                        STAFF_BLUE
                )
                .decorate(
                        net.kyori.adventure.text.format.TextDecoration.BOLD
                )
                .append(
                        Component.text(
                                " §r§7Enabled"
                        )
                );
    }
}