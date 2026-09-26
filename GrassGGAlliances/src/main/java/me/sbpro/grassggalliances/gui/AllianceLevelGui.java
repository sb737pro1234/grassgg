/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package me.sbpro.grassggalliances.gui;

import java.util.ArrayList;
import java.util.List;
import me.sbpro.grassggalliances.GrassGGAlliances;
import me.sbpro.grassggalliances.gui.AllianceLevelGuiHolder;
import me.sbpro.grassggalliances.gui.AllianceXpValuesGuiHolder;
import me.sbpro.grassggalliances.model.Alliance;
import me.sbpro.grassggalliances.service.AllianceXpService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class AllianceLevelGui {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final int PAGE_SIZE = 18;

    private AllianceLevelGui() {
    }

    public static void openMain(GrassGGAlliances plugin, Player player, Alliance alliance) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new AllianceLevelGuiHolder(alliance.getName()), (int)27, (Component)plugin.getMessageService().getComponent("level-title"));
        inventory.setItem(13, AllianceLevelGui.createAllianceInfoItem(plugin, alliance));
        inventory.setItem(22, AllianceLevelGui.createSimpleItem(Material.ARROW, plugin.getMessageService().getRaw("go-back"), List.of("&7Return to alliance information")));
        inventory.setItem(25, AllianceLevelGui.createSimpleItem(Material.CHEST, plugin.getMessageService().getRaw("xp-values-button"), List.of("&7View all alliance XP sources")));
        player.openInventory(inventory);
    }

    public static void openXpValues(GrassGGAlliances plugin, Player player, Alliance alliance, int page) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new AllianceXpValuesGuiHolder(alliance.getName(), page), (int)27, (Component)plugin.getMessageService().getComponent("xp-values-title"));
        List<AllianceXpService.XpEntry> entries = plugin.getXpService().getDisplayEntries();
        int start = page * PAGE_SIZE;
        int end = Math.min(entries.size(), start + PAGE_SIZE);
        int slot = 0;
        for (int index = start; index < end; ++index) {
            inventory.setItem(slot++, AllianceLevelGui.createXpEntryItem(entries.get(index)));
        }
        inventory.setItem(18, AllianceLevelGui.createSimpleItem(Material.ARROW, plugin.getMessageService().getRaw("previous-page"), List.of("&7Open the previous page")));
        inventory.setItem(22, AllianceLevelGui.createSimpleItem(Material.BARRIER, plugin.getMessageService().getRaw("go-back"), List.of("&7Return to alliance information")));
        inventory.setItem(26, AllianceLevelGui.createSimpleItem(Material.ARROW, plugin.getMessageService().getRaw("next-page"), List.of("&7Open the next page")));
        player.openInventory(inventory);
    }

    public static int getMaxPage(GrassGGAlliances plugin) {
        int size = plugin.getXpService().getDisplayEntries().size();
        return Math.max(0, (size - 1) / PAGE_SIZE);
    }

    private static ItemStack createAllianceInfoItem(GrassGGAlliances plugin, Alliance alliance) {
        AllianceXpService xpService = plugin.getXpService();
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("&7Alliance: " + alliance.getName());
        lore.add("&7Current Alliance Level: &e" + alliance.getLevel());
        lore.add("&7Current Alliance XP: &f" + Math.round(alliance.getXp()));
        lore.add("&7Upgrade Progress: " + xpService.getProgressBar(alliance));
        lore.add(" ");
        lore.addAll(xpService.getLevelPerksLore());
        return AllianceLevelGui.createSimpleItem(Material.GRASS_BLOCK, plugin.getMessageService().getRaw("level-gui-title"), lore);
    }

    private static ItemStack createXpEntryItem(AllianceXpService.XpEntry entry) {
        return AllianceLevelGui.createSimpleItem(entry.iconMaterial(), entry.displayName(), List.of("&7Category: &f" + entry.prettyCategory(), "&7Alliance XP: &e" + Math.round(entry.xp())));
    }

    private static ItemStack createSimpleItem(Material material, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName((Component)LEGACY.deserialize(name));
        if (!loreLines.isEmpty()) {
            List<Component> lore = loreLines.stream().map(line -> (Component)LEGACY.deserialize(line)).toList();
            meta.lore(lore);
        }
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
        item.setItemMeta(meta);
        return item;
    }
}

