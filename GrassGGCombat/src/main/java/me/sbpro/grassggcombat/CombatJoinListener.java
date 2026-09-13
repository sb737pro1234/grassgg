package me.sbpro.grassggcombat;

import me.sbpro.grassggcombat.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CombatJoinListener implements Listener {

    private final CombatManager combatManager;

    public CombatJoinListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (!combatManager.wasCombatLoggedOut(player)) {
            return;
        }

        player.showTitle(
                net.kyori.adventure.title.Title.title(
                        net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                                .legacySection()
                                .deserialize("§f☠ YOU DIED ☠"),
                        net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                                .legacySection()
                                .deserialize("§fYou logged out during combat."),
                        net.kyori.adventure.title.Title.Times.times(
                                java.time.Duration.ofMillis(500),
                                java.time.Duration.ofSeconds(3),
                                java.time.Duration.ofMillis(500)
                        )
                )
        );

        combatManager.clearCombatLoggedOut(player);
    }
}
