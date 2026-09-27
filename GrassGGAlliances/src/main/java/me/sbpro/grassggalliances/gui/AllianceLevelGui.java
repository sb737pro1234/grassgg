package me.sbpro.grassggalliances.gui;

import me.sbpro.grassggalliances.GrassGGAlliances;
import me.sbpro.grassggalliances.Messages;
import me.sbpro.grassggalliances.model.Alliance;
import me.sbpro.grassggalliances.service.AllianceXpService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class AllianceLevelGui {

    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacyAmpersand();

    private static final int PAGE_SIZE = 45;

    private AllianceLevelGui() {
    }

    public static void openMain(
            GrassGGAlliances plugin,
            Player player,
            Alliance alliance
    ) {
        Inventory inventory = Bukkit.createInventory(
                new AllianceLevelGuiHolder(alliance.getName()),
                27,
                plugin.getMessageService().getComponent("level-title")
        );

        inventory.setItem(
                13,
                createAllianceInfoItem(plugin, alliance)
        );

        inventory.setItem(
                22,
                createSimpleItem(
                        Material.ARROW,
                        plugin.getMessageService().getRaw("go-back"),
                        List.of("&fReturn to alliance information")
                )
        );

        // Alliance XP Sources button
        inventory.setItem(
                26,
                createSimpleItem(
                        Material.CHEST,
                        plugin.getMessageService().getRaw("xp-values-button"),
                        List.of("&fView all alliance XP sources")
                )
        );

        player.openInventory(inventory);
    }

    public static void openXpValues(
            GrassGGAlliances plugin,
            Player player,
            Alliance alliance,
            int page
    ) {
        Inventory inventory = Bukkit.createInventory(
                new AllianceXpValuesGuiHolder(alliance.getName(), page),
                54,
                plugin.getMessageService().getComponent("xp-values-title")
        );

        List<AllianceXpService.XpEntry> entries =
                plugin.getXpService().getDisplayEntries();

        int start = page * PAGE_SIZE;
        int end = Math.min(entries.size(), start + PAGE_SIZE);

        /*
         * XP entries start on row 2.
         * This leaves the top row for navigation.
         */
        int slot = 9;

        for (int index = start; index < end; index++) {
            inventory.setItem(
                    slot++,
                    createXpEntryItem(entries.get(index))
            );
        }

        // Previous page
        inventory.setItem(
                0,
                createSimpleItem(
                        Material.ARROW,
                        plugin.getMessageService().getRaw("previous-page"),
                        List.of("&fOpen the previous page")
                )
        );

        // Back
        inventory.setItem(
                4,
                createSimpleItem(
                        Material.BARRIER,
                        plugin.getMessageService().getRaw("go-back"),
                        List.of("&fReturn to alliance level")
                )
        );

        // Next page
        inventory.setItem(
                8,
                createSimpleItem(
                        Material.ARROW,
                        plugin.getMessageService().getRaw("next-page"),
                        List.of("&fOpen the next page")
                )
        );

        player.openInventory(inventory);
    }

    public static int getMaxPage(GrassGGAlliances plugin) {
        int size = plugin.getXpService().getDisplayEntries().size();

        return Math.max(
                0,
                (size - 1) / PAGE_SIZE
        );
    }

    private static ItemStack createAllianceInfoItem(
            GrassGGAlliances plugin,
            Alliance alliance
    ) {
        AllianceXpService xpService = plugin.getXpService();

        ArrayList<String> lore = new ArrayList<>();

        lore.add(
                "&fAlliance: "
                        + Messages.ALLIANCE_COLOR
                        + alliance.getName()
        );

        lore.add(
                "&fCurrent Alliance Level: "
                        + Messages.ALLIANCE_COLOR
                        + alliance.getLevel()
        );

        lore.add(
                "&fCurrent Alliance XP: "
                        + Messages.ALLIANCE_COLOR
                        + Math.round(alliance.getXp())
        );

        lore.add(
                "&fUpgrade Progress: "
                        + xpService.getProgressBar(alliance)
        );

        lore.add(" ");

        lore.addAll(
                xpService.getLevelPerksLore()
        );

        return createSimpleItem(
                Material.GRASS_BLOCK,
                plugin.getMessageService().getRaw("level-gui-title"),
                lore
        );
    }

    private static ItemStack createXpEntryItem(
            AllianceXpService.XpEntry entry
    ) {
        return createSimpleItem(
                entry.iconMaterial(),
                entry.displayName().replace('&', '§'),
                List.of(
                        "&fCategory: "
                                + Messages.ALLIANCE_COLOR
                                + entry.prettyCategory(),

                        "&fAlliance XP: "
                                + Messages.ALLIANCE_COLOR
                                + Math.round(entry.xp())
                )
        );
    }

    private static ItemStack createSimpleItem(
            Material material,
            String name,
            List<String> loreLines
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        /*
         * Explicitly disable italic on item names.
         */
        meta.displayName(
                LEGACY.deserialize(name)
                        .decoration(TextDecoration.ITALIC, false)
        );

        if (!loreLines.isEmpty()) {

            /*
             * Collectors.toList() instead of Stream.toList()
             * for compatibility with the project's configured
             * Java language level.
             */
            List<Component> lore = loreLines.stream()
                    .map(line ->
                            LEGACY.deserialize(line)
                                    .decoration(
                                            TextDecoration.ITALIC,
                                            false
                                    )
                    )
                    .collect(Collectors.toList());

            meta.lore(lore);
        }

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES
        );

        item.setItemMeta(meta);

        return item;
    }
}