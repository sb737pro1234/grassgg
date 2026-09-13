/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.inventory.InventoryHolder
 */
package me.sbpro.grassggalliances.gui;

import java.util.Optional;
import me.sbpro.grassggalliances.ChatToggleService;
import me.sbpro.grassggalliances.MessageService;
import me.sbpro.grassggalliances.data.AllianceStorage;
import me.sbpro.grassggalliances.gui.DeleteGuiHolder;
import me.sbpro.grassggalliances.model.Alliance;
import me.sbpro.grassggalliances.service.AllianceBuffService;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public final class DeleteAllianceListener
implements Listener {
    private final AllianceStorage allianceStorage;
    private final MessageService messageService;
    private final ChatToggleService chatToggleService;
    private final AllianceBuffService buffService;

    public DeleteAllianceListener(AllianceStorage allianceStorage, MessageService messageService, ChatToggleService chatToggleService, AllianceBuffService buffService) {
        this.allianceStorage = allianceStorage;
        this.messageService = messageService;
        this.chatToggleService = chatToggleService;
        this.buffService = buffService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (!(inventoryHolder instanceof DeleteGuiHolder)) {
            return;
        }
        DeleteGuiHolder holder = (DeleteGuiHolder)inventoryHolder;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {
            player.closeInventory();
            player.sendMessage(this.messageService.prefixed("delete-cancelled"));
            return;
        }
        if (event.getCurrentItem().getType() != Material.GREEN_STAINED_GLASS_PANE) {
            return;
        }
        Optional<Alliance> allianceOptional = this.allianceStorage.getAllianceByPlayer(player.getUniqueId());
        if (allianceOptional.isEmpty()) {
            player.closeInventory();
            player.sendMessage(this.messageService.prefixed("no-alliance"));
            return;
        }
        Alliance alliance = allianceOptional.get();
        if (!alliance.getOwner().equals(player.getUniqueId()) || !alliance.getName().equalsIgnoreCase(holder.getAllianceName())) {
            player.closeInventory();
            player.sendMessage(this.messageService.prefixed("owner-only-delete"));
            return;
        }
        this.chatToggleService.disableAll(alliance.getMembers());
        this.buffService.clearAlliance(alliance);
        this.allianceStorage.deleteAlliance(alliance.getName());
        player.closeInventory();
        player.sendMessage(this.messageService.prefixed("deleted", text -> text.replace("%alliance%", alliance.getName())));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof DeleteGuiHolder) {
            event.setCancelled(true);
        }
    }
}

