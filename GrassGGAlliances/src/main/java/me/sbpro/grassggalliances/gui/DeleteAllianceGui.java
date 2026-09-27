package me.sbpro.grassggalliances.gui;

import me.sbpro.grassggalliances.GrassGGAlliances;
import me.sbpro.grassggalliances.Messages;
import me.sbpro.grassggalliances.gui.DeleteGuiHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class DeleteAllianceGui {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private DeleteAllianceGui() {
    }

    public static void open(GrassGGAlliances plugin, Player player, String allianceName) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new DeleteGuiHolder(allianceName), (int)27, (Component)plugin.getMessageService().getComponent("delete-title"));
        inventory.setItem(11, DeleteAllianceGui.createButton(Material.RED_STAINED_GLASS_PANE, "§cCancel"));
        inventory.setItem(15, DeleteAllianceGui.createButton(Material.GREEN_STAINED_GLASS_PANE, "§aConfirm"));
        player.openInventory(inventory);
    }

    private static ItemStack createButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName((Component)LEGACY.deserialize(name.replace('§', '&')).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }
}

