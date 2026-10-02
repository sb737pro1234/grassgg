package me.sbpro.grassggenchants.items;

import me.sbpro.grassggenchants.GrassGGEnchants;
import me.sbpro.grassggenchants.Messages;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class EnchantUpgradeItem {

    private EnchantUpgradeItem() {
    }

    public static ItemStack create(GrassGGEnchants plugin) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Messages.ENCHANT_UPGRADE_NAME);
        meta.setLore(List.of(Messages.ENCHANT_UPGRADE_LORE));
        meta.getPersistentDataContainer().set(
                plugin.getCustomItemKey(),
                PersistentDataType.STRING,
                "enchant_upgrade"
        );

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isEnchantUpgrade(GrassGGEnchants plugin, ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }

        String value = item.getItemMeta().getPersistentDataContainer().get(
                plugin.getCustomItemKey(),
                PersistentDataType.STRING
        );

        return "enchant_upgrade".equals(value);
    }
}
