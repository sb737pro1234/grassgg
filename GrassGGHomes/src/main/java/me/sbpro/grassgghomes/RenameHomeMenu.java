package me.sbpro.grassgghomes;

import me.sbpro.grassgghomes.*;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RenameHomeMenu {

    private final GrassGGHomes plugin;

    private final Player viewer;

    private final Player target;

    private final int homeNumber;


    public RenameHomeMenu(
            GrassGGHomes plugin,
            Player viewer,
            Player target,
            int homeNumber
    ) {

        this.plugin = plugin;
        this.viewer = viewer;
        this.target = target;
        this.homeNumber = homeNumber;
    }


    public void open() {

        Home home =
                plugin.getHomesManager().getHome(
                        target.getUniqueId(),
                        homeNumber
                );

        if (home == null) {

            viewer.sendMessage(
                    "§2§lHOMES §8»§f That home does not exist."
            );

            return;
        }

        String currentName =
                home.getName() == null || home.getName().isBlank()
                        ? "Home " + homeNumber
                        : home.getName();

        new AnvilGUI.Builder()

                .plugin(plugin)

                .title("Rename Home")

                .itemLeft(new ItemStack(Material.NAME_TAG))

                .text(currentName)

                .onClick((slot, snapshot) -> {

                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return java.util.Collections.emptyList();
                    }

                    String newName =
                            ChatColor.stripColor(snapshot.getText()).trim();

                    if (newName.isBlank()) {

                        return java.util.List.of(
                                AnvilGUI.ResponseAction.replaceInputText(currentName)
                        );
                    }

                    if (newName.length() > 24) {

                        return java.util.List.of(
                                AnvilGUI.ResponseAction.replaceInputText(
                                        newName.substring(0, 24)
                                )
                        );
                    }

                    plugin.getHomesManager().renameHome(
                            target.getUniqueId(),
                            homeNumber,
                            newName
                    );

                    return java.util.List.of(

                            AnvilGUI.ResponseAction.close(),

                            AnvilGUI.ResponseAction.run(() -> {

                                viewer.sendMessage(
                                        "§2§lHOMES §8»§f Home renamed to §2"
                                                + newName
                                                + "§f."
                                );

                                new HomesMenu(
                                        plugin,
                                        viewer,
                                        target
                                ).open();
                            })
                    );

                })

                .open(viewer);
    }

}