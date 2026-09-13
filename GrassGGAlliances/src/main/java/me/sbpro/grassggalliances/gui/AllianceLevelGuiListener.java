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
import me.sbpro.grassggalliances.GrassGGAlliances;
import me.sbpro.grassggalliances.MessageService;
import me.sbpro.grassggalliances.data.AllianceStorage;
import me.sbpro.grassggalliances.gui.AllianceLevelGui;
import me.sbpro.grassggalliances.gui.AllianceLevelGuiHolder;
import me.sbpro.grassggalliances.gui.AllianceXpValuesGuiHolder;
import me.sbpro.grassggalliances.model.Alliance;
import me.sbpro.grassggalliances.service.AllianceXpService;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public final class AllianceLevelGuiListener
implements Listener {
    private final GrassGGAlliances plugin;
    private final AllianceStorage allianceStorage;
    private final MessageService messageService;
    private final AllianceXpService xpService;

    public AllianceLevelGuiListener(GrassGGAlliances plugin, AllianceStorage allianceStorage, MessageService messageService, AllianceXpService xpService) {
        this.plugin = plugin;
        this.allianceStorage = allianceStorage;
        this.messageService = messageService;
        this.xpService = xpService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Object allianceOptional;
        Object holder;
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder instanceof AllianceLevelGuiHolder) {
            holder = (AllianceLevelGuiHolder)inventoryHolder;
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            if (event.getCurrentItem().getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }
            if (event.getCurrentItem().getType() == Material.CHEST) {
                allianceOptional = this.allianceStorage.getAllianceByName(((AllianceLevelGuiHolder)holder).getAllianceName());
                ((Optional<Alliance>)allianceOptional).ifPresent(alliance -> AllianceLevelGui.openXpValues(this.plugin, player, alliance, 0));
            }
            return;
        }
        allianceOptional = event.getInventory().getHolder();
        if (!(allianceOptional instanceof AllianceXpValuesGuiHolder)) {
            return;
        }
        holder = (AllianceXpValuesGuiHolder)allianceOptional;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }
        allianceOptional = this.allianceStorage.getAllianceByName(((AllianceXpValuesGuiHolder)holder).getAllianceName());
        if (((Optional)allianceOptional).isEmpty()) {
            player.closeInventory();
            return;
        }
        if (event.getCurrentItem().getType() == Material.BARRIER) {
            AllianceLevelGui.openMain(this.plugin, player, (Alliance)((Optional)allianceOptional).get());
            return;
        }
        if (event.getCurrentItem().getType() != Material.ARROW) {
            return;
        }
        int maxPage = AllianceLevelGui.getMaxPage(this.plugin);
        if (event.getSlot() == 18) {
            if (((AllianceXpValuesGuiHolder)holder).getPage() <= 0) {
                player.sendMessage(this.messageService.prefixed("no-previous-page"));
                return;
            }
            AllianceLevelGui.openXpValues(this.plugin, player, (Alliance)((Optional)allianceOptional).get(), ((AllianceXpValuesGuiHolder)holder).getPage() - 1);
            return;
        }
        if (event.getSlot() == 26) {
            if (((AllianceXpValuesGuiHolder)holder).getPage() >= maxPage) {
                player.sendMessage(this.messageService.prefixed("no-next-page"));
                return;
            }
            AllianceLevelGui.openXpValues(this.plugin, player, (Alliance)((Optional)allianceOptional).get(), ((AllianceXpValuesGuiHolder)holder).getPage() + 1);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AllianceLevelGuiHolder || event.getInventory().getHolder() instanceof AllianceXpValuesGuiHolder) {
            event.setCancelled(true);
        }
    }
}

