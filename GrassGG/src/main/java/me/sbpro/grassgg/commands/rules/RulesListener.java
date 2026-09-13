package me.sbpro.grassgg.commands.rules;

import me.sbpro.grassgg.commands.rules.RulesCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class RulesListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!event.getView().getTitle().equals(RulesCommand.TITLE)) {
            return;
        }

        // Prevent anything from being moved inside the Rules menu
        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
            event.setCancelled(true);
        }

        // When the book is clicked
        if (event.getRawSlot() == 13 && event.getWhoClicked() instanceof Player player) {

            Component topLine = Component.text(
                    "----------------------------------------",
                    TextColor.fromHexString("#555555")
            );

            Component message = Component.text()
                    .append(Component.text(
                            "◆ ",
                            TextColor.fromHexString("#00C2FF")
                    ))
                    .append(Component.text(
                            "[CLICK HERE]",
                            TextColor.fromHexString("#00C2FF"),
                            TextDecoration.BOLD
                    ))
                    .append(Component.text(
                            " Open the Grass.GG rules.",
                            TextColor.fromHexString("#FFFFFF")
                    ))
                    .clickEvent(ClickEvent.openUrl("https://docs.google.com/document/d/1wZWo1C1_vQj826vAjiInfvQced7srQfRcTJrb9H-BoQ/edit?usp=sharing"))
                    .build();

            Component bottomLine = Component.text(
                    "----------------------------------------",
                    TextColor.fromHexString("#555555")
            );

            player.sendMessage(topLine);
            player.sendMessage(message);
            player.sendMessage(bottomLine);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!event.getView().getTitle().equals(RulesCommand.TITLE)) {
            return;
        }

        // Prevent dragging items into the Rules menu
        for (int slot : event.getRawSlots()) {
            if (slot < event.getView().getTopInventory().getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
