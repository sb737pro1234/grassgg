package me.sbpro.grassggteleport.spectate;

import me.sbpro.grassggteleport.gui.ConfirmationAction;
import org.bukkit.entity.Player;

public final class SpectateAction implements ConfirmationAction {

    private final SpectateManager spectateManager;
    private final Player player;
    private final Player target;

    public SpectateAction(
            SpectateManager spectateManager,
            Player player,
            Player target
    ) {
        this.spectateManager = spectateManager;
        this.player = player;
        this.target = target;
    }

    @Override
    public void confirm() {
        spectateManager.startSpectating(
                player,
                target
        );
    }

    @Override
    public void cancel() {
    }
}