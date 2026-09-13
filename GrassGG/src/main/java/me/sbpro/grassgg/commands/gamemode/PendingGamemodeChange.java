package me.sbpro.grassgg.commands.gamemode;

import org.bukkit.entity.Player;

public class PendingGamemodeChange {

    private final Player target;
    private final GamemodeType gamemode;

    public PendingGamemodeChange(Player target, GamemodeType gamemode) {
        this.target = target;
        this.gamemode = gamemode;
    }

    public Player getTarget() {
        return target;
    }

    public GamemodeType getGamemode() {
        return gamemode;
    }
}