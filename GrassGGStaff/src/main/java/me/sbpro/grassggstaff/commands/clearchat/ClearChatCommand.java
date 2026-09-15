package me.sbpro.grassggstaff.commands.clearchat;

import me.sbpro.grassggstaff.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class ClearChatCommand
        implements CommandExecutor {

    private static final int CLEAR_LINES = 50;

    private static final String BLUE =
            "§x§2§9§7§9§F§F";

    private static final String CLEAR_MESSAGE_PREFIX =
            BLUE + "§lSTAFF §8» §fChat has been cleared by ";

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission(
                "grassgg.staff.clearchat"
        )) {

            sender.sendMessage(
                    Messages.NO_PERMISSION
            );

            return true;
        }

        /*
         * Console does not need confirmation.
         */
        if (!(sender instanceof Player player)) {

            clearChat("Console");

            return true;
        }

        /*
         * Players must confirm.
         */
        openConfirmMenu(player);

        return true;
    }

    private void openConfirmMenu(
            Player player
    ) {

        ClearChatConfirmHolder holder =
                new ClearChatConfirmHolder();

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        27,
                        BLUE + "§lCLEAR CHAT"
                );

        holder.setInventory(inventory);

        /*
         * Slot 10 = CANCEL
         */
        inventory.setItem(
                10,
                item(
                        Material.RED_STAINED_GLASS_PANE,
                        "§c§lCANCEL"
                )
        );

        /*
         * Slot 13 = DISPLAY ITEM
         */
        ItemStack display =
                new ItemStack(
                        Material.PAPER
                );

        ItemMeta displayMeta =
                display.getItemMeta();

        if (displayMeta != null) {

            displayMeta.setDisplayName(
                    BLUE + "§lClear Chat"
            );

            displayMeta.setLore(
                    List.of(
                            "§fThis will clear the public chat",
                            "§ffor everyone currently online.",
                            "",
                            "§fYou will send §x§2§9§7§9§F§F"
                                    + CLEAR_LINES
                                    + " §fblank lines."
                    )
            );

            display.setItemMeta(
                    displayMeta
            );
        }

        inventory.setItem(
                13,
                display
        );

        /*
         * Slot 16 = CONFIRM
         */
        inventory.setItem(
                16,
                item(
                        Material.LIME_STAINED_GLASS_PANE,
                        "§a§lCONFIRM"
                )
        );

        player.openInventory(inventory);
    }

    public static void clearChat(
            String clearer
    ) {

        /*
         * Send the blank lines first.
         */
        for (int i = 0; i < CLEAR_LINES; i++) {

            Bukkit.broadcastMessage("");
        }

        /*
         * Then show who cleared it.
         */
        Bukkit.broadcastMessage(
                CLEAR_MESSAGE_PREFIX
                        + BLUE
                        + clearer
        );
    }

    private static ItemStack item(
            Material material,
            String name
    ) {

        ItemStack item =
                new ItemStack(material);

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(name);

            item.setItemMeta(meta);
        }

        return item;
    }
}