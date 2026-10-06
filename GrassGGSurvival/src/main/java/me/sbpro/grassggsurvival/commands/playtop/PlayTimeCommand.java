package me.sbpro.grassggsurvival.commands.playtop;

import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTimeCommand implements CommandExecutor {

    private static final String PLAYTIME_MESSAGE = "§2§lGRASS.GG §8» §fYour playtime is §2%playtime%§f.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(PlayTopMessages.PLAYER_ONLY);
            return true;
        }

        long playtimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        player.sendMessage(
                PLAYTIME_MESSAGE.replace("%playtime%", PlayTopMessages.formatPlaytime(playtimeTicks))
        );

        return true;
    }
}