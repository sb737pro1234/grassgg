package me.sbpro.grassggteleport.teleport;

import me.sbpro.grassggteleport.gui.ConfirmationAction;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public final class MultiTeleportAction implements ConfirmationAction {

    private final TeleportManager teleportManager;
    private final List<Player> players;
    private final Location destination;
    private final Player messagePlayer;

    public MultiTeleportAction(
            TeleportManager teleportManager,
            List<Player> players,
            Location destination,
            Player messagePlayer
    ) {
        this.teleportManager = teleportManager;
        this.players = List.copyOf(players);
        this.destination = destination.clone();
        this.messagePlayer = messagePlayer;
    }

    @Override
    public void confirm() {
        int teleportedCount = 0;

        for (Player player : players) {
            if (!player.isOnline()) {
                continue;
            }

            teleportManager.instantTeleport(
                    player,
                    destination,
                    null
            );

            teleportedCount++;
        }

        if (messagePlayer.isOnline()) {
            messagePlayer.sendMessage(
                    "§x§0§0§A§8§F§F§lTELEPORT §8» §fTeleported "
                            + "§x§0§0§A§8§F§F"
                            + teleportedCount
                            + " §fplayers to you."
            );

            messagePlayer.playSound(
                    messagePlayer.getLocation(),
                    org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT,
                    1.0f,
                    1.0f
            );
        }
    }

    @Override
    public void cancel() {
    }
}