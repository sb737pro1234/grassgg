package me.sbpro.grassgg.commands.gamemode;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GamemodeListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!event.getView().getTitle().equals(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Gamemode Confirm")) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null) {
            return;
        }

        PendingGamemodeChange pending =
                GameModeCommand.getPending().get(player.getUniqueId());

        if (pending == null) {
            player.closeInventory();
            return;
        }

        switch (event.getCurrentItem().getType()) {

            case GREEN_STAINED_GLASS_PANE -> {

                Player target = pending.getTarget();

                if (target == null || !target.isOnline()) {
                    player.sendMessage("§x§E§F§4§4§4§4§lADMIN §8» §cThat player is no longer online.");
                    player.closeInventory();
                    GameModeCommand.getPending().remove(player.getUniqueId());
                    return;
                }

                target.setGameMode(pending.getGamemode().getGameMode());

                player.sendMessage(
                        "§x§E§F§4§4§4§4§lADMIN §8» §2Changed §f"
                                + target.getName()
                                + "§2 to "
                                + pending.getGamemode().getDisplayName()
                                + " mode."
                );

                target.sendMessage(
                        "§x§E§F§4§4§4§4§lADMIN §8» §2Your gamemode has been changed to §f"
                                + pending.getGamemode().getDisplayName()
                                + "§2."
                );

                GameModeCommand.getPending().remove(player.getUniqueId());
                player.closeInventory();
            }

            case RED_STAINED_GLASS_PANE -> {
                GameModeCommand.getPending().remove(player.getUniqueId());
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        GameModeCommand.getPending().remove(event.getPlayer().getUniqueId());
    }

}