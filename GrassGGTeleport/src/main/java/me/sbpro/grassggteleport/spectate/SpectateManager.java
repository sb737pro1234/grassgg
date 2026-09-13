package me.sbpro.grassggteleport.spectate;

import me.sbpro.grassggteleport.message.Messages;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpectateManager {

    private final Map<UUID, GameMode> previousGameModes =
            new HashMap<>();

    public void startSpectating(
            Player player,
            Player target
    ) {
        previousGameModes.putIfAbsent(
                player.getUniqueId(),
                player.getGameMode()
        );

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(target.getLocation());

        player.sendMessage(
                Messages.spectating(target)
        );

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_ENDERMAN_TELEPORT,
                1.0f,
                1.0f
        );
    }

    public boolean stopSpectating(Player player) {
        GameMode previousGameMode =
                previousGameModes.remove(
                        player.getUniqueId()
                );

        if (previousGameMode == null) {
            return false;
        }

        player.setGameMode(previousGameMode);

        player.sendMessage(
                Messages.spectateStopped()
        );

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_ENDERMAN_TELEPORT,
                1.0f,
                1.0f
        );

        return true;
    }

    public boolean isSpectating(Player player) {
        return previousGameModes.containsKey(
                player.getUniqueId()
        );
    }

    public void cleanup(Player player) {
        previousGameModes.remove(
                player.getUniqueId()
        );
    }

    public void shutdown() {
        previousGameModes.clear();
    }
}