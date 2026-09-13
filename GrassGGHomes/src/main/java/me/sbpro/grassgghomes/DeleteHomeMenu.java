package me.sbpro.grassgghomes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeleteHomeMenu {

    private final GrassGGHomes plugin;

    private final Player player;

    private final int homeNumber;

    private Inventory inventory;


    private static final Map<UUID, Integer> openHomeNumbers =
            new HashMap<>();


    public DeleteHomeMenu(
            GrassGGHomes plugin,
            Player player,
            int homeNumber
    ) {

        this.plugin = plugin;
        this.player = player;
        this.homeNumber = homeNumber;

        openHomeNumbers.put(
                player.getUniqueId(),
                homeNumber
        );

        create();
    }


    private void create() {

        inventory = Bukkit.createInventory(
                null,
                27,
                "§2Delete Home §8» §fHome" + homeNumber
        );


        fillBackground();


        Home home =
                plugin.getHomesManager()
                        .getHome(
                                player.getUniqueId(),
                                homeNumber
                        );


        String homeName =
                home == null
                        ? "Home " + homeNumber
                        : home.getName();


        if (homeName == null
                || homeName.isBlank()) {

            homeName =
                    "Home " + homeNumber;
        }


        /*
         * Cancel
         */
        inventory.setItem(
                10,
                createItem(
                        Material.RED_STAINED_GLASS_PANE,
                        "§cCancel",
                        List.of(
                                "§7",
                                "§fClick to cancel."
                        )
                )
        );


        /*
         * Preview
         */
        inventory.setItem(
                13,
                createItem(
                        Material.GREEN_BED,
                        "§2" + homeName,
                        List.of(
                                "§7",
                                "§fThis home will be deleted."
                        )
                )
        );


        /*
         * Confirm
         */
        inventory.setItem(
                16,
                createItem(
                        Material.LIME_STAINED_GLASS_PANE,
                        "§aConfirm",
                        List.of(
                                "§7",
                                "§fClick to delete this home."
                        )
                )
        );
    }


    private void fillBackground() {

        ItemStack filler =
                createItem(
                        Material.GRAY_STAINED_GLASS_PANE,
                        " ",
                        null
                );


        for (int i = 0;
             i < inventory.getSize();
             i++) {

            inventory.setItem(
                    i,
                    filler
            );
        }
    }


    private ItemStack createItem(
            Material material,
            String name,
            List<String> lore
    ) {

        ItemStack item =
                new ItemStack(material);


        ItemMeta meta =
                item.getItemMeta();


        if (meta != null) {

            meta.setDisplayName(name);


            if (lore != null) {

                meta.setLore(lore);
            }


            item.setItemMeta(meta);
        }


        return item;
    }


    public void open() {

        player.openInventory(inventory);
    }


    public Inventory getInventory() {

        return inventory;
    }


    public int getHomeNumber() {

        return homeNumber;
    }


    public static Integer getOpenHomeNumber(
            Player player
    ) {

        return openHomeNumbers.get(
                player.getUniqueId()
        );
    }


    public static void removeOpenHomeNumber(
            Player player
    ) {

        openHomeNumbers.remove(
                player.getUniqueId()
        );
    }
}