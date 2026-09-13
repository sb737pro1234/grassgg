package me.sbpro.grassggstaff.sus;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.reason.PunishmentReason;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SusMenuListener implements Listener {

    private final GrassGGStaff plugin;
    private final SusManager manager;

    private static final String BLUE =
            "§x§2§9§7§9§F§F";

    public SusMenuListener(
            GrassGGStaff plugin,
            SusManager manager
    ) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Inventory top =
                event.getView().getTopInventory();

        /*
         * ============================================================
         *                       MAIN SUS MENU
         * ============================================================
         */

        if (top.getHolder() instanceof SusMenuHolder holder) {

            event.setCancelled(true);

            int slot = event.getRawSlot();

            if (slot < 0 || slot >= top.getSize()) {
                return;
            }

            /*
             * Player skulls.
             */
            if (slot >= 0 && slot < 45) {

                SusPlayer susPlayer =
                        holder.playerAt(slot);

                if (susPlayer == null) {
                    return;
                }

                ClickType click =
                        event.getClick();

                /*
                 * Shift click -> delete a reason.
                 */
                if (click.isShiftClick()) {

                    openDeleteReasonMenu(
                            player,
                            susPlayer
                    );

                    return;
                }

                /*
                 * Right click -> add/change reason.
                 */
                if (click.isRightClick()) {

                    openAddReasonMenu(
                            player,
                            susPlayer
                    );

                    return;
                }

                /*
                 * Left click -> spectate.
                 */
                if (click.isLeftClick()) {

                    player.performCommand(
                            "spectate "
                                    + susPlayer.name()
                    );
                }

                return;
            }

            /*
             * Previous page.
             */
            if (slot == 45
                    && holder.page() > 0) {

                SusMenu.open(
                        player,
                        manager,
                        holder.page() - 1
                );

                return;
            }

            /*
             * Next page.
             */
            if (slot == 53) {

                int maxPage =
                        Math.max(
                                0,
                                (int) Math.ceil(
                                        holder.players().size()
                                                / 45.0
                                ) - 1
                        );

                if (holder.page() < maxPage) {

                    SusMenu.open(
                            player,
                            manager,
                            holder.page() + 1
                    );
                }
            }

            return;
        }

        /*
         * ============================================================
         *                     ADD REASON MENU
         * ============================================================
         */

        if (top.getHolder()
                instanceof SusReasonMenuHolder holder) {

            event.setCancelled(true);

            int slot = event.getRawSlot();

            if (slot < 0 || slot >= top.getSize()) {
                return;
            }

            /*
             * Reasons occupy the first 45 slots.
             */
            if (slot >= 0 && slot < 45) {

                ItemStack item =
                        top.getItem(slot);

                if (item == null
                        || item.getType() == Material.AIR) {
                    return;
                }

                ItemMeta meta =
                        item.getItemMeta();

                if (meta == null
                        || meta.getDisplayName() == null) {
                    return;
                }

                String reason =
                        stripColour(
                                meta.getDisplayName()
                        );

                if (reason.isEmpty()) {
                    return;
                }

                addReason(
                        player,
                        holder.player(),
                        reason
                );

                return;
            }

            /*
             * Back button.
             */
            if (slot == 49) {

                SusMenu.open(
                        player,
                        manager,
                        0
                );
            }

            return;
        }

        /*
         * ============================================================
         *                    DELETE REASON MENU
         * ============================================================
         */

        if (top.getHolder()
                instanceof SusDeleteReasonMenuHolder holder) {

            event.setCancelled(true);

            int slot = event.getRawSlot();

            if (slot < 0 || slot >= top.getSize()) {
                return;
            }

            /*
             * Existing reasons occupy the first 45 slots.
             */
            if (slot >= 0 && slot < 45) {

                SusNote note =
                        getNoteForSlot(
                                holder.player(),
                                slot
                        );

                if (note == null) {
                    return;
                }

                openConfirmation(
                        player,
                        holder.player(),
                        note
                );

                return;
            }

            /*
             * Back button.
             */
            if (slot == 49) {

                SusMenu.open(
                        player,
                        manager,
                        0
                );
            }

            return;
        }

        /*
         * ============================================================
         *                     CONFIRMATION MENU
         * ============================================================
         */

        if (top.getHolder()
                instanceof SusConfirmHolder holder) {

            event.setCancelled(true);

            int slot = event.getRawSlot();

            if (slot < 0 || slot >= top.getSize()) {
                return;
            }

            /*
             * SLOT 16 = CONFIRM
             */
            if (slot == 16) {

                removeReason(
                        player,
                        holder
                );

                return;
            }

            /*
             * SLOT 10 = CANCEL
             */
            if (slot == 10) {

                openDeleteReasonMenu(
                        player,
                        holder.player()
                );
            }
        }
    }

    @EventHandler
    public void onDrag(
            InventoryDragEvent event
    ) {

        Object holder =
                event.getView()
                        .getTopInventory()
                        .getHolder();

        if (holder instanceof SusMenuHolder
                || holder instanceof SusReasonMenuHolder
                || holder instanceof SusDeleteReasonMenuHolder
                || holder instanceof SusConfirmHolder) {

            event.setCancelled(true);
        }
    }

    /*
     * ================================================================
     *                     ADD REASON MENU
     * ================================================================
     */

    private void openAddReasonMenu(
            Player player,
            SusPlayer susPlayer
    ) {

        SusReasonMenuHolder holder =
                new SusReasonMenuHolder(
                        susPlayer
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        54,
                        BLUE
                                + "§lADD SUS REASON §8» §f"
                                + susPlayer.name()
                );

        List<PunishmentReason> reasons =
                new ArrayList<>(
                        plugin.getReasonManager().all()
                );

        reasons.sort(
                Comparator.comparing(
                        PunishmentReason::displayName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        int slot = 0;

        for (PunishmentReason reason : reasons) {

            if (slot >= 45) {
                break;
            }

            inventory.setItem(
                    slot,
                    reasonItem(reason)
            );

            slot++;
        }

        inventory.setItem(
                49,
                simpleItem(
                        Material.BARRIER,
                        BLUE + "§lBack"
                )
        );

        player.openInventory(inventory);
    }

    /*
     * ================================================================
     *                    DELETE REASON MENU
     * ================================================================
     */

    private void openDeleteReasonMenu(
            Player player,
            SusPlayer susPlayer
    ) {

        SusDeleteReasonMenuHolder holder =
                new SusDeleteReasonMenuHolder(
                        susPlayer
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        54,
                        BLUE
                                + "§lDELETE SUS REASON §8» §f"
                                + susPlayer.name()
                );

        List<SusNote> notes =
                susPlayer.notes();

        int slot = 0;

        for (SusNote note : notes) {

            if (slot >= 45) {
                break;
            }

            inventory.setItem(
                    slot,
                    reasonDeleteItem(note)
            );

            slot++;
        }

        inventory.setItem(
                49,
                simpleItem(
                        Material.BARRIER,
                        BLUE + "§lBack"
                )
        );

        player.openInventory(inventory);
    }

    /*
     * ================================================================
     *                     CONFIRMATION MENU
     * ================================================================
     */

    private void openConfirmation(
            Player player,
            SusPlayer susPlayer,
            SusNote note
    ) {

        SusConfirmHolder holder =
                new SusConfirmHolder(
                        susPlayer,
                        note
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        27,
                        BLUE + "§lCONFIRM DELETE"
                );

        /*
         * ============================================================
         * SLOT 10
         * CANCEL
         * ============================================================
         */

        inventory.setItem(
                10,
                simpleItem(
                        Material.RED_STAINED_GLASS_PANE,
                        "§c§lCancel"
                )
        );

        /*
         * ============================================================
         * SLOT 13
         * INFORMATION PAPER
         * ============================================================
         */

        ItemStack information =
                new ItemStack(
                        Material.PAPER
                );

        ItemMeta informationMeta =
                information.getItemMeta();

        if (informationMeta != null) {

            informationMeta.setDisplayName(
                    BLUE + "§lDelete SUS Reason"
            );

            informationMeta.setLore(
                    List.of(
                            "§fPlayer: §x§2§9§7§9§F§F"
                                    + susPlayer.name(),
                            "§fReason: §x§2§9§7§9§F§F"
                                    + note.reason(),
                            "",
                            "§cThis cannot be undone."
                    )
            );

            information.setItemMeta(
                    informationMeta
            );
        }

        inventory.setItem(
                13,
                information
        );

        /*
         * ============================================================
         * SLOT 16
         * CONFIRM
         * ============================================================
         */

        inventory.setItem(
                16,
                simpleItem(
                        Material.LIME_STAINED_GLASS_PANE,
                        "§a§lConfirm"
                )
        );

        player.openInventory(inventory);
    }

    /*
     * ================================================================
     *                       ADD REASON
     * ================================================================
     */

    private void addReason(
            Player player,
            SusPlayer susPlayer,
            String reason
    ) {

        manager.addNote(
                        susPlayer.uuid(),
                        susPlayer.name(),
                        reason,
                        player.getUniqueId(),
                        player.getName()
                )
                .whenComplete(
                        (result, throwable) -> {

                            Bukkit.getScheduler()
                                    .runTask(
                                            plugin,
                                            () -> {

                                                if (throwable != null) {

                                                    plugin.getLogger()
                                                            .warning(
                                                                    "Failed to add SUS reason for "
                                                                            + susPlayer.name()
                                                                            + ": "
                                                                            + getCauseMessage(throwable)
                                                            );

                                                    player.sendMessage(
                                                            Messages.DATABASE_ERROR
                                                    );

                                                    return;
                                                }

                                                if (result.status()
                                                        == SusManager.AddResult.Status.ALREADY_EXISTS) {

                                                    player.sendMessage(
                                                            Messages.susAlreadyExists(
                                                                    susPlayer.name(),
                                                                    reason
                                                            )
                                                    );

                                                    return;
                                                }

                                                player.sendMessage(
                                                        Messages.susAdded(
                                                                result.note()
                                                        )
                                                );

                                                SusMenu.open(
                                                        player,
                                                        manager,
                                                        0
                                                );
                                            }
                                    );
                        }
                );
    }

    /*
     * ================================================================
     *                     REMOVE REASON
     * ================================================================
     */

    private void removeReason(
            Player player,
            SusConfirmHolder holder
    ) {

        SusNote note =
                holder.note();

        manager.removeNote(
                        holder.player().uuid(),
                        note.reason()
                )
                .whenComplete(
                        (result, throwable) -> {

                            Bukkit.getScheduler()
                                    .runTask(
                                            plugin,
                                            () -> {

                                                if (throwable != null) {

                                                    plugin.getLogger()
                                                            .warning(
                                                                    "Failed to remove SUS reason for "
                                                                            + holder.player().name()
                                                                            + ": "
                                                                            + getCauseMessage(throwable)
                                                            );

                                                    player.sendMessage(
                                                            Messages.DATABASE_ERROR
                                                    );

                                                    return;
                                                }

                                                if (result.status()
                                                        == SusManager.RemoveResult.Status.NOT_FOUND) {

                                                    player.sendMessage(
                                                            Messages.susReasonNotFound(
                                                                    holder.player().name(),
                                                                    note.reason()
                                                            )
                                                    );

                                                    return;
                                                }

                                                player.sendMessage(
                                                        Messages.susRemoved(
                                                                result.note()
                                                        )
                                                );

                                                SusMenu.open(
                                                        player,
                                                        manager,
                                                        0
                                                );
                                            }
                                    );
                        }
                );
    }

    /*
     * ================================================================
     *                        HELPERS
     * ================================================================
     */

    private SusNote getNoteForSlot(
            SusPlayer player,
            int slot
    ) {

        List<SusNote> notes =
                player.notes();

        if (slot < 0
                || slot >= notes.size()) {

            return null;
        }

        return notes.get(slot);
    }

    private ItemStack reasonItem(
            PunishmentReason reason
    ) {

        ItemStack item =
                new ItemStack(
                        Material.BOOK
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(
                    BLUE
                            + reason.displayName()
            );

            meta.setLore(
                    List.of(
                            "§fClick to add this SUS reason."
                    )
            );

            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack reasonDeleteItem(
            SusNote note
    ) {

        ItemStack item =
                new ItemStack(
                        Material.PAPER
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(
                    BLUE
                            + note.reason()
            );

            meta.setLore(
                    List.of(
                            "§fClick to remove this reason."
                    )
            );

            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack simpleItem(
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

    private String stripColour(
            String input
    ) {

        return input
                .replaceAll(
                        "§[0-9a-fk-or]",
                        ""
                )
                .replaceAll(
                        "§x(§[0-9a-fA-F]){6}",
                        ""
                )
                .trim();
    }

    private String getCauseMessage(
            Throwable throwable
    ) {

        Throwable cause =
                throwable;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage() != null
                ? cause.getMessage()
                : cause.getClass().getSimpleName();
    }
}