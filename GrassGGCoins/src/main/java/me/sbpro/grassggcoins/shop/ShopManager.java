package me.sbpro.grassggcoins.shop;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ShopManager {

    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration config;

    public ShopManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "shop.yml");
        reload();
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public List<ShopItem> getItems() {
        ConfigurationSection section = config.getConfigurationSection("Shop.Items");
        if (section == null) {
            return Collections.emptyList();
        }

        List<ShopItem> items = new ArrayList<>();
        for (String identifier : section.getKeys(false)) {
            String path = "Shop.Items." + identifier;
            long cost = Math.max(0L, config.getLong(path + ".Cost", 0L));
            String command = config.getString(path + ".Command", "");
            ItemStack displayItem = config.getItemStack(path + ".Display Item");
            items.add(new ShopItem(identifier, cost, command, displayItem));
        }

        return items;
    }

    public ShopItem getItem(String identifier) {
        for (ShopItem item : getItems()) {
            if (item.identifier().equalsIgnoreCase(identifier)) {
                return item;
            }
        }
        return null;
    }

    public List<String> getIdentifiers() {
        return getItems().stream().map(ShopItem::identifier).toList();
    }

    public boolean setDisplayItem(String identifier, ItemStack itemStack) {
        ShopItem item = getItem(identifier);
        if (item == null) {
            return false;
        }

        config.set("Shop.Items." + item.identifier() + ".Display Item", itemStack.clone());
        save();
        return true;
    }

    private void save() {
        try {
            config.save(file);
            reload();
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save shop.yml: " + exception.getMessage());
        }
    }

    public record ShopItem(String identifier, long cost, String command, ItemStack displayItem) {
        public boolean hasDisplayItem() {
            return displayItem != null && displayItem.getType() != Material.AIR;
        }
    }
}
