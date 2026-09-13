package me.sbpro.grassgghomes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomesListener implements Listener {

    private final GrassGGHomes plugin;

    /*
     * Viewer UUID -> Target UUID
     *
     * Used to remember whose Homes GUI is currently being viewed.
     */
    private static final Map<UUID, UUID> openHomes = new HashMap<>();


    public HomesListener(GrassGGHomes plugin) {
        this.plugin = plugin;
    }


    public static void registerMenu(
            Player viewer,
            Player target
    ) {

        openHomes.put(
                viewer.getUniqueId(),
                target.getUniqueId()
        );
    }


    @EventHandler
    public void onInventoryClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }


        String title =
                event.getView().getTitle();


        /*
         * Homes menu
         */
        if (title.equals("§2ʜᴏᴍᴇѕ")
                || title.startsWith("§2ʜᴏᴍᴇѕ §8»")) {

            event.setCancelled(true);


            UUID targetUUID =
                    openHomes.getOrDefault(
                            player.getUniqueId(),
                            player.getUniqueId()
                    );


            Player target =
                    plugin.getServer()
                            .getPlayer(targetUUID);


            if (target == null) {
                return;
            }


            HomesMenu menu =
                    new HomesMenu(
                            plugin,
                            player,
                            target
                    );


            handleHomesClick(
                    player,
                    target,
                    event.getSlot(),
                    event.getClick(),
                    menu
            );

            return;
        }


        /*
         * Delete confirmation menu
         */
        if (title.startsWith("§2Delete Home")) {

            event.setCancelled(true);


            ItemStack item =
                    event.getCurrentItem();


            if (item == null) {
                return;
            }


            /*
             * Get the home number directly from
             * the DeleteHomeMenu currently open.
             */
            Integer homeNumber =
                    DeleteHomeMenu.getOpenHomeNumber(
                            player
                    );


            if (homeNumber == null) {

                player.closeInventory();

                return;
            }


            /*
             * CONFIRM
             */
            if (item.getType()
                    == Material.LIME_STAINED_GLASS_PANE) {


                plugin.getHomesManager()
                        .deleteHome(
                                player.getUniqueId(),
                                homeNumber
                        );


                DeleteHomeMenu.removeOpenHomeNumber(
                        player
                );


                player.sendMessage(
                        "§2§lHOMES §8»§f Home deleted."
                );


                player.playSound(
                        player.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_PLING,
                        1,
                        1
                );


                new HomesMenu(
                        plugin,
                        player,
                        player
                ).open();


                return;
            }


            /*
             * CANCEL
             */
            if (item.getType()
                    == Material.RED_STAINED_GLASS_PANE) {


                DeleteHomeMenu.removeOpenHomeNumber(
                        player
                );


                UUID targetUUID =
                        openHomes.getOrDefault(
                                player.getUniqueId(),
                                player.getUniqueId()
                        );


                Player target =
                        plugin.getServer()
                                .getPlayer(targetUUID);


                if (target != null) {

                    new HomesMenu(
                            plugin,
                            player,
                            target
                    ).open();
                }
            }
        }
    }


    private void handleHomesClick(
            Player viewer,
            Player target,
            int slot,
            ClickType click,
            HomesMenu menu
    ) {

        for (int i = 0; i < 7; i++) {

            int number = i + 1;


            /*
             * Bed
             */
            if (slot == menu.getBedSlots()[i]) {

                handleBedClick(
                        viewer,
                        target,
                        number
                );

                return;
            }


            /*
             * Button
             */
            if (slot == menu.getButtonSlots()[i]) {

                handleButtonClick(
                        viewer,
                        target,
                        number,
                        click
                );

                return;
            }
        }
    }


    private void handleBedClick(
            Player viewer,
            Player target,
            int number
    ) {

        /*
         * Permission
         */
        if (getHomeLimit(target) < number) {

            viewer.sendMessage(
                    "§2§lHOMES §8»§c No permission."
            );

            return;
        }


        Home home =
                plugin.getHomesManager()
                        .getHome(
                                target.getUniqueId(),
                                number
                        );


        /*
         * Home does not exist
         */
        if (home == null) {

            viewer.sendMessage(
                    "§2§lHOMES §8»§f Set your home first."
            );

            return;
        }


        /*
         * Start teleport
         */
        viewer.closeInventory();


        new TeleportManager(
                plugin,
                viewer,
                home.getLocation()
        ).start();
    }


    private void handleButtonClick(
            Player viewer,
            Player target,
            int number,
            ClickType click
    ) {

        /*
         * Staff viewing another player
         */
        if (!viewer.equals(target)) {

            viewer.sendMessage(
                    "§2§lHOMES §8»§f You cannot modify this player's homes."
            );

            return;
        }


        /*
         * Permission
         */
        if (getHomeLimit(target) < number) {

            viewer.sendMessage(
                    "§2§lHOMES §8»§c No permission."
            );

            return;
        }


        Home home =
                plugin.getHomesManager()
                        .getHome(
                                target.getUniqueId(),
                                number
                        );


        /*
         * Existing home
         */
        if (home != null) {


            /*
             * Right click = Rename
             */
            if (click.isRightClick()) {

                new RenameHomeMenu(
                        plugin,
                        viewer,
                        target,
                        number
                ).open();

                return;
            }


            /*
             * Left click = Delete confirmation
             */
            new DeleteHomeMenu(
                    plugin,
                    viewer,
                    number
            ).open();

            return;
        }


        /*
         * Create home
         */
        Location location =
                viewer.getLocation();


        if (!isAllowedWorld(
                location.getWorld()
        )) {

            viewer.sendMessage(
                    "§2§lHOMES §8»§c You cannot set a home here."
            );

            return;
        }


        plugin.getHomesManager()
                .setHome(
                        target.getUniqueId(),
                        number,
                        location
                );


        viewer.sendMessage(
                "§2§lHOMES §8»§f Home created."
        );


        new HomesMenu(
                plugin,
                viewer,
                target
        ).open();
    }


    private boolean isAllowedWorld(
            World world
    ) {

        return world != null
                && List.of(
                "world",
                "world_nether",
                "world_the_end"
        ).contains(
                world.getName()
        );
    }


    private int getHomeLimit(
            Player player
    ) {

        for (int i = 7; i >= 1; i--) {

            if (player.hasPermission(
                    "grassgg.homes." + i
            )) {

                return i;
            }
        }

        return 0;
    }


    /*
     * Teleport movement cancellation
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        if (!TeleportManager.isTeleporting(event.getPlayer())) {
            return;
        }

        if (event.getTo() == null) {
            return;
        }

        // Only X and Z movement cancels the teleport.
        // Y movement (jumping/falling) is allowed.
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {

            TeleportManager.cancel(event.getPlayer());
        }
    }
}