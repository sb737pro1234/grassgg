package me.sbpro.grassggsupertools.tools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SuperToolManager {

    private final NamespacedKey toolKey;

    public SuperToolManager(JavaPlugin plugin) {
        this.toolKey = new NamespacedKey(plugin, "super_tool");
    }

    public ItemStack create(SuperToolType type) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§x§A§0§2§0§F§0Super " + type.getShortName());
        meta.setLore(List.of(
                "§f",
                "§x§A§0§2§0§F§0" + type.getInformation()
        ));

        addEnchant(meta, Enchantment.EFFICIENCY, 5);
        addEnchant(meta, Enchantment.UNBREAKING, 5);
        addEnchant(meta, Enchantment.MENDING, 1);

        switch (type) {
            case PICKAXE_SILK, SHOVEL_SILK -> addEnchant(meta, Enchantment.SILK_TOUCH, 1);
            case PICKAXE_FORTUNE, SHOVEL_FORTUNE, AXE, HOE -> addEnchant(meta, Enchantment.FORTUNE, 3);
        }

        if (type == SuperToolType.AXE) {
            addEnchant(meta, Enchantment.SHARPNESS, 5);
        }

        meta.getPersistentDataContainer().set(toolKey, PersistentDataType.STRING, type.getId());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private void addEnchant(ItemMeta meta, Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
    }

    public SuperToolType getType(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }

        String id = item.getItemMeta().getPersistentDataContainer()
                .get(toolKey, PersistentDataType.STRING);

        if (id == null) {
            return null;
        }

        for (SuperToolType type : SuperToolType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        return null;
    }
}
