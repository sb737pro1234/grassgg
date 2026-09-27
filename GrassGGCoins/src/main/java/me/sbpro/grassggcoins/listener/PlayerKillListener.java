package me.sbpro.grassggcoins.listener;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class PlayerKillListener implements Listener {

    private static final long KILL_REWARD = 5L;

    private final GrassGGCoins plugin;

    public PlayerKillListener(GrassGGCoins plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKill(PlayerDeathEvent event) {

        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        /*
         * Only reward actual player kills.
         * If the player was killed by a mob, fall damage, fire, etc.
         * there will be no player killer.
         */
        if (killer == null) {
            return;
        }

        /*
         * Safety check so a player can never be rewarded for
         * somehow being registered as their own killer.
         */
        if (killer.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }

        plugin.getCoinManager().addCoins(
                killer.getUniqueId(),
                KILL_REWARD
        );

        killer.sendActionBar(
                Messages.playerKillRewardActionBar(
                        victim.getName(),
                        KILL_REWARD
                )
        );

        killer.sendMessage(
                Messages.playerKillRewardChatMessage(
                        victim.getName(),
                        KILL_REWARD
                )
        );
    }
}