package me.sbpro.grassggteleport.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ConfirmationMenu {

    public static final String TITLE = "Confirm Teleport";

    public static final int CANCEL_SLOT = 10;
    public static final int DISPLAY_SLOT = 13;
    public static final int CONFIRM_SLOT = 16;

    private static final Map<UUID, ConfirmationAction> actions = new HashMap<>();

    private ConfirmationMenu() {
    }

    public static void open(
            Player player,
            String action,
            ConfirmationAction confirmationAction
    ) {
        Inventory inventory = Bukkit.createInventory(
                null,
                27,
                TITLE
        );

        inventory.setItem(
                CANCEL_SLOT,
                createItem(
                        Material.RED_STAINED_GLASS_PANE,
                        "§cCancel"
                )
        );

        inventory.setItem(
                DISPLAY_SLOT,
                createItem(
                        Material.ENDER_PEARL,
                        "§x§0§0§A§8§F§FThis will " + action
                )
        );

        inventory.setItem(
                CONFIRM_SLOT,
                createItem(
                        Material.GREEN_STAINED_GLASS_PANE,
                        "§aConfirm"
                )
        );

        actions.put(player.getUniqueId(), confirmationAction);

        player.openInventory(inventory);
    }

    public static ConfirmationAction getAction(Player player) {
        return actions.get(player.getUniqueId());
    }

    public static ConfirmationAction removeAction(Player player) {
        return actions.remove(player.getUniqueId());
    }

    private static ItemStack createItem(
            Material material,
            String name
    ) {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }
}