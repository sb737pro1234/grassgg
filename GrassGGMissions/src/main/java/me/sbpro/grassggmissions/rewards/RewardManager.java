package me.sbpro.grassggmissions.rewards;

import me.sbpro.grassggmissions.managers.EconomyManager;
import me.sbpro.grassggmissions.missions.PlayerMission;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class RewardManager {

    private RewardManager() {
    }

    public static void reward(Player player, PlayerMission mission) {

        EconomyManager.deposit(player, mission.getMission().getReward());

        String reward = NumberFormat.getInstance()
                .format(mission.getMission().getReward());

        player.sendTitle(
                "§a§lMission Complete!",
                "§6+£" + reward,
                10,
                60,
                20
        );

        player.sendMessage("");
        player.sendMessage("§8[§2Daily Missions§8] §aMission completed!");
        player.sendMessage("§8[§2Daily Missions§8] §aYou earned §6£" + reward);
        player.sendMessage("");

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_PLAYER_LEVELUP,
                1F,
                1F
        );
    }
}