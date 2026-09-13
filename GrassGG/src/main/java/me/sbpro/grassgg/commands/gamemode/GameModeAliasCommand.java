package me.sbpro.grassgg.commands.gamemode;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class GameModeAliasCommand extends GameModeCommand {

    private final GamemodeType gamemode;

    public GameModeAliasCommand(GamemodeType gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    protected GamemodeType getForcedGamemode() {
        return gamemode;
    }

    @Override
    protected boolean isAliasCommand() {
        return true;
    }
}
