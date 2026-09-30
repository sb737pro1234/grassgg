package me.sbpro.grassggquests.shop;

import me.sbpro.grassggquests.GrassGGQuests;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ShopManager {
    private final GrassGGQuests plugin;

    public ShopManager(GrassGGQuests plugin) {
        this.plugin = plugin;
    }

    public Map<String, ShopItem> getItems() {
        Map<String, ShopItem> result = new LinkedHashMap<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("Shop.Items");
        if (section == null) return result;

        for (String id : section.getKeys(false)) {
            String path = "Shop.Items." + id;
            String name = color(plugin.getConfig().getString(path + ".Display Name", id));
            int cost = plugin.getConfig().getInt(path + ".Cost", 1);
            String command = plugin.getConfig().getString(path + ".Command", "");
            int slot = plugin.getConfig().getInt(path + ".Slot", 11);
            ItemStack display = plugin.getConfig().getItemStack(path + ".Display Item");
            if (display == null || display.getType() == Material.AIR) display = fallbackItem(name);
            result.put(id.toLowerCase(Locale.ROOT), new ShopItem(id, name, cost, command, slot, display));
        }
        return result;
    }

    public ShopItem getItem(String id) {
        if (id == null) return null;
        return getItems().get(id.toLowerCase(Locale.ROOT));
    }

    public boolean setDisplayItem(String id, ItemStack item) {
        ShopItem shopItem = getItem(id);
        if (shopItem == null || item == null || item.getType() == Material.AIR) return false;
        plugin.getConfig().set("Shop.Items." + shopItem.id() + ".Display Item", item.clone());
        plugin.saveConfig();
        return true;
    }

    private ItemStack fallbackItem(String name) {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text).replace("\\u00a7", "§");
    }
}
