package me.sbpro.grassggstaff.sus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;

import java.util.ArrayList;
import java.util.List;

public final class SusMenu {

    public static final String BLUE =
            "§x§2§9§7§9§F§F";

    private SusMenu() {
    }

    public static void open(
            Player player,
            SusManager manager,
            int requestedPage
    ) {

        List<SusPlayer> players =
                manager.getPlayers();

        int totalPages =
                Math.max(
                        1,
                        (int) Math.ceil(
                                players.size() / 45.0
                        )
                );

        int page =
                Math.max(
                        0,
                        Math.min(
                                requestedPage,
                                totalPages - 1
                        )
                );

        SusMenuHolder holder =
                new SusMenuHolder(
                        page,
                        players
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        54,
                        BLUE + "§lSUS PLAYERS §8» §f"
                                + (page + 1)
                );

        int start =
                page * 45;

        int end =
                Math.min(
                        start + 45,
                        players.size()
                );

        for (int index = start; index < end; index++) {

            SusPlayer susPlayer =
                    players.get(index);

            int slot =
                    index - start;

            ItemStack skull =
                    new ItemStack(Material.PLAYER_HEAD);

            SkullMeta meta =
                    (SkullMeta) skull.getItemMeta();

            if (meta == null) {
                continue;
            }

            meta.setOwningPlayer(
                    Bukkit.getOfflinePlayer(
                            susPlayer.uuid()
                    )
            );

            skull.setData(
                    DataComponentTypes.TOOLTIP_DISPLAY,
                    TooltipDisplay.tooltipDisplay()
                            .addHiddenComponents(
                                    DataComponentTypes.PROFILE
                            )
                            .build()
            );

            meta.setDisplayName(
                    BLUE + "§l" + susPlayer.name()
            );

            List<String> lore =
                    new ArrayList<>();


            /*
             * SUS reasons.
             */
            for (SusNote note :
                    susPlayer.notes()) {

                lore.add(
                        "§f"
                                + note.reason()
                );
            }

            lore.add("");

            /*
             * Controls.
             */
            lore.add(
                    BLUE
                            + "Left click to spectate player"
            );

            lore.add(
                    BLUE
                            + "Right click to change/add another reason"
            );

            lore.add(
                    BLUE
                            + "Shift click to delete reason"
            );

            meta.setLore(lore);

            skull.setItemMeta(meta);

            inventory.setItem(
                    slot,
                    skull
            );
        }

        /*
         * Previous page.
         */
        if (page > 0) {

            inventory.setItem(
                    45,
                    item(
                            Material.ARROW,
                            BLUE + "§lPrevious Page"
                    )
            );
        }

        /*
         * Page indicator.
         */
        inventory.setItem(
                49,
                item(
                        Material.BOOK,
                        BLUE
                                + "§lPage "
                                + (page + 1)
                                + " §f/ "
                                + totalPages
                )
        );

        /*
         * Next page.
         */
        if (page < totalPages - 1) {

            inventory.setItem(
                    53,
                    item(
                            Material.ARROW,
                            BLUE + "§lNext Page"
                    )
            );
        }

        player.openInventory(inventory);
    }

    private static ItemStack item(
            Material material,
            String name
    ) {

        ItemStack item =
                new ItemStack(material);

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }
}