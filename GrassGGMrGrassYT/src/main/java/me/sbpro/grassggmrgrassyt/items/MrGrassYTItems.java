package me.sbpro.grassggmrgrassyt.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class MrGrassYTItems {

    private static final Map<String, Supplier<ItemStack>> ITEMS = new LinkedHashMap<>();

    static {
        /*
         * Add/edit your items here.
         *
         * Example:
         *
         * ITEMS.put("VampireBlade", () -> {
         *     ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
         *     // Configure the item here.
         *     return item;
         * });
         */

        ITEMS.put("VampireBlade", () -> {

            ItemStack VampireBlade = new ItemStack(Material.NETHERITE_SWORD);
            ItemMeta VampireBladeMeta = VampireBlade.getItemMeta();

            VampireBladeMeta.setDisplayName("§4§lVampire Blade");
            VampireBladeMeta.setLore(List.of(
                    "",
                    "§4Vampires long ago came together ",
                    "§4using blood from innocents to craft this blade.",
                    "",
                    "§6Obtained from the Halloween 2026 crate."
            ));


            VampireBladeMeta.addEnchant(Enchantment.SHARPNESS, 6, true);
            VampireBladeMeta.addEnchant(Enchantment.SMITE, 3, true);
            VampireBladeMeta.addEnchant(Enchantment.FIRE_ASPECT, 10, true);
            VampireBladeMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            VampireBladeMeta.addEnchant(Enchantment.UNBREAKING, 3, true);
            VampireBladeMeta.addEnchant(Enchantment.MENDING, 1, true);

            VampireBladeMeta.setRarity(ItemRarity.EPIC);

            VampireBlade.setItemMeta(VampireBladeMeta);

            // {fire_aspect:10,knockback:-1,mending:1,sharpness:6,smite:3,unbreaking:3}]


            return VampireBlade;
        });
    }

    private MrGrassYTItems() {
    }

    public static ItemStack getItem(String identifier) {
        Supplier<ItemStack> supplier = ITEMS.get(identifier);
        return supplier == null ? null : supplier.get();
    }

    public static Set<String> getIdentifiers() {
        return ITEMS.keySet();
    }
}
