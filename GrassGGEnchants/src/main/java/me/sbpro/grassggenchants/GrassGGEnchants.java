package me.sbpro.grassggenchants;

import me.sbpro.grassggenchants.commands.EnchantItemCommand;
import me.sbpro.grassggenchants.listeners.EnchantUpgradeListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGEnchants extends JavaPlugin {

    private static GrassGGEnchants instance;
    private NamespacedKey customItemKey;

    @Override
    public void onEnable() {
        instance = this;
        customItemKey = new NamespacedKey(this, "custom_enchant_item");

        getCommand("enchantitem").setExecutor(new EnchantItemCommand(this));
        getCommand("enchantitem").setTabCompleter(new EnchantItemCommand(this));

        getServer().getPluginManager().registerEvents(new EnchantUpgradeListener(this), this);

        getLogger().info("GrassGGEnchants enabled.");
    }

    public static GrassGGEnchants getInstance() {
        return instance;
    }

    public NamespacedKey getCustomItemKey() {
        return customItemKey;
    }
}
