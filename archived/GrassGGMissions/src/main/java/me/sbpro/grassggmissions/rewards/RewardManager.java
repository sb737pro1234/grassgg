package me.sbpro.grassggmissions.rewards;

import me.sbpro.grassggmissions.Messages;
import me.sbpro.grassggmissions.missions.PlayerMission;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class RewardManager {

    private RewardManager() {
    }

    public static void reward(Player player, PlayerMission mission) {

        String command = Messages.MISSION_REWARD_COMMAND
                .replace("%player%", player.getName());

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        player.sendTitle(
                Messages.MISSION_COMPLETE_TITLE,
                Messages.MISSION_COMPLETE_SUBTITLE,
                10,
                60,
                20
        );

        player.sendMessage("");
        player.sendMessage(Messages.PREFIX + Messages.MISSION_COMPLETE_MESSAGE);
        player.sendMessage(Messages.PREFIX + Messages.MISSION_REWARD_MESSAGE);
        player.sendMessage("");

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_PLAYER_LEVELUP,
                1F,
                1F
        );
    }
}
