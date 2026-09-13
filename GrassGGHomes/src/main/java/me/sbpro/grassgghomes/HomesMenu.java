package me.sbpro.grassgghomes;

import me.sbpro.grassgghomes.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class HomesMenu {

    private final GrassGGHomes plugin;

    private final Player viewer;
    private final Player target;

    private Inventory inventory;


    private final int[] bedSlots = {
            10, 11, 12, 13, 14, 15, 16
    };

    private final int[] buttonSlots = {
            19, 20, 21, 22, 23, 24, 25
    };


    public HomesMenu(
            GrassGGHomes plugin,
            Player viewer,
            Player target
    ) {

        this.plugin = plugin;
        this.viewer = viewer;
        this.target = target;

        create();
    }


    private void create() {

        String title;

        if (viewer.equals(target)) {

            title = "§2ʜᴏᴍᴇѕ";

        } else {

            title =
                    "§2ʜᴏᴍᴇѕ §8» §f"
                            + target.getName();
        }


        inventory = Bukkit.createInventory(
                null,
                36,
                title
        );


        fillBackground();


        for (int i = 0; i < 7; i++) {

            int homeNumber = i + 1;

            inventory.setItem(
                    bedSlots[i],
                    createBed(homeNumber)
            );

            inventory.setItem(
                    buttonSlots[i],
                    createButton(homeNumber)
            );
        }
    }



    private void fillBackground() {

        ItemStack filler =
                new ItemStack(
                        Material.GRAY_STAINED_GLASS_PANE
                );

        ItemMeta meta =
                filler.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(" ");

            filler.setItemMeta(meta);
        }


        for (int i = 0; i < inventory.getSize(); i++) {

            inventory.setItem(
                    i,
                    filler
            );
        }
    }



    private ItemStack createBed(int number) {

        boolean hasPermission =
                getHomeLimit(target) >= number;


        Home home =
                plugin
                        .getHomesManager()
                        .getHome(
                                target.getUniqueId(),
                                number
                        );


        boolean hasHome =
                home != null;


        Material material;


        if (!hasPermission) {

            material = Material.RED_BED;

        } else if (hasHome) {

            material = Material.GREEN_BED;

        } else {

            material = Material.GRAY_BED;
        }


        ItemStack item =
                new ItemStack(material);


        ItemMeta meta =
                item.getItemMeta();


        if (meta != null) {


            if (!hasPermission) {


                meta.setDisplayName(
                        "§cHome " + number
                );


                meta.setLore(List.of(
                        "§7",
                        "§cYou do not have access",
                        "§cto this home slot."
                ));


            } else if (hasHome) {


                meta.setDisplayName(
                        "§2" + getHomeName(home, number)
                );


                meta.setLore(List.of(
                        "§7",
                        "§fClick to teleport.",
                        "§7",
                        "§2Teleport takes 5 seconds."
                ));


            } else {


                meta.setDisplayName(
                        "§7Home " + number
                );


                meta.setLore(List.of(
                        "§7",
                        "§cThis home has not been set.",
                        "§7",
                        "§fUse the button below to create it."
                ));
            }


            item.setItemMeta(meta);
        }


        return item;
    }



    private ItemStack createButton(int number) {


        boolean hasPermission =
                getHomeLimit(target) >= number;


        Home home =
                plugin
                        .getHomesManager()
                        .getHome(
                                target.getUniqueId(),
                                number
                        );


        boolean hasHome =
                home != null;


        Material material;


        if (!hasPermission) {

            material = Material.RED_DYE;

        } else if (hasHome) {

            material = Material.GREEN_DYE;

        } else {

            material = Material.GRAY_DYE;
        }


        ItemStack item =
                new ItemStack(material);


        ItemMeta meta =
                item.getItemMeta();


        if (meta != null) {


            if (!hasPermission) {


                meta.setDisplayName(
                        "§cNo Permission"
                );


                meta.setLore(List.of(
                        "§7",
                        "§cYou cannot use this slot."
                ));


            } else if (hasHome) {


                meta.setDisplayName(
                        "§2Manage " + getHomeName(home, number)
                );


                meta.setLore(List.of(
                        "§7",
                        "§fLeft click to delete.",
                        "§fRight click to rename."
                ));


            } else {


                meta.setDisplayName(
                        "§7Set Home " + number
                );


                meta.setLore(List.of(
                        "§7",
                        "§fClick to create this home."
                ));
            }


            item.setItemMeta(meta);
        }


        return item;
    }



    private String getHomeName(
            Home home,
            int number
    ) {

        if (home == null || home.getName() == null || home.getName().isEmpty()) {

            return "Home " + number;
        }

        return home.getName();
    }



    private int getHomeLimit(Player player) {

        for (int i = 7; i >= 1; i--) {

            if (player.hasPermission(
                    "grassgg.homes." + i
            )) {

                return i;
            }
        }

        return 0;
    }



    public void open() {

        HomesListener.registerMenu(
                viewer,
                target
        );

        viewer.openInventory(inventory);
    }



    public Inventory getInventory() {
        return inventory;
    }


    public Player getViewer() {
        return viewer;
    }


    public Player getTarget() {
        return target;
    }


    public int[] getBedSlots() {
        return bedSlots;
    }


    public int[] getButtonSlots() {
        return buttonSlots;
    }

}