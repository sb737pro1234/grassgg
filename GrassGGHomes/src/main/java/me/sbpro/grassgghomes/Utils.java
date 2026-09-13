package me.sbpro.grassgghomes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Utils {


    public static ItemStack createItem(
            Material material,
            String name,
            List<String> lore
    ) {


        ItemStack item =
                new ItemStack(material);


        ItemMeta meta =
                item.getItemMeta();


        if (meta != null) {

            meta.setDisplayName(name);


            if (lore != null) {
                meta.setLore(lore);
            }


            item.setItemMeta(meta);
        }


        return item;
    }

}