package me.sbpro.grassggcombat;

import me.sbpro.grassggcombat.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatDeathListener implements Listener {

    private final CombatManager combatManager;

    public CombatDeathListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player victim = event.getEntity();

        /*
         * If the player wasn't in combat, nothing needs to happen.
         */
        if (!combatManager.isInCombat(victim)) {
            return;
        }

        /*
         * End the victim's combat.
         */
        combatManager.remove(victim);

        /*
         * If they were killed by another player,
         * also end the killer's combat.
         */
        Player killer = victim.getKiller();

        if (killer != null && combatManager.isInCombat(killer)) {
            combatManager.remove(killer);
        }
    }
}
