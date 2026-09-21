package me.sbpro.grassggcoins.listener;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.Messages;
import me.sbpro.grassggcoins.banknote.BankNoteData;
import me.sbpro.grassggcoins.banknote.BankNoteManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public final class BankNoteListener implements Listener {

    private final GrassGGCoins plugin;
    private final BankNoteManager bankNoteManager;

    public BankNoteListener(GrassGGCoins plugin) {
        this.plugin = plugin;
        this.bankNoteManager = plugin.getBankNoteManager();
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = false
    )
    public void onInteract(PlayerInteractEvent event) {

        Action action = event.getAction();

        if (action != Action.LEFT_CLICK_AIR
                && action != Action.LEFT_CLICK_BLOCK
                && action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK) {

            return;
        }

        EquipmentSlot hand = event.getHand();

        if (hand == null) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack item;

        if (hand == EquipmentSlot.OFF_HAND) {
            item = player.getInventory().getItemInOffHand();
        } else {
            item = player.getInventory().getItemInMainHand();
        }

        Optional<BankNoteData> optionalData =
                bankNoteManager.read(item);

        if (optionalData.isEmpty()) {
            return;
        }

        event.setCancelled(true);

        BankNoteData note = optionalData.get();

        if (bankNoteManager.isRedeemed(note.noteId())) {

            player.sendMessage(
                    Messages.bankNoteAlreadyUsed()
            );

            return;
        }

        if (!bankNoteManager.markRedeemed(note.noteId())) {

            player.sendMessage(
                    Messages.bankNoteError()
            );

            return;
        }

        plugin.getCoinManager().addCoins(
                player.getUniqueId(),
                note.amount()
        );

        ItemStack remaining = consumeOne(item);

        if (hand == EquipmentSlot.OFF_HAND) {
            player.getInventory().setItemInOffHand(remaining);
        } else {
            player.getInventory().setItemInMainHand(remaining);
        }

        player.sendMessage(
                Messages.bankNoteRedeemed(note.amount())
        );
    }

    private ItemStack consumeOne(ItemStack item) {

        ItemStack result = item.clone();
        int amount = result.getAmount();

        if (amount <= 1) {
            return null;
        }

        result.setAmount(amount - 1);

        return result;
    }
}