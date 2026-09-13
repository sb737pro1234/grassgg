package me.sbpro.grassggcombat;

import me.sbpro.grassggcombat.CombatManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener implements Listener {

    private final CombatManager combatManager;

    public CombatListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        Player attacker = getAttackingPlayer(event.getDamager());

        if (attacker == null) {
            return;
        }

        if (attacker.equals(victim)) {
            return;
        }

        // Put both players into combat.
        combatManager.tag(attacker);
        combatManager.tag(victim);
    }

    /**
     * Gets the attacking player.
     *
     * Supports direct player attacks and projectiles such as arrows.
     */
    private Player getAttackingPlayer(Entity damager) {

        if (damager instanceof Player player) {
            return player;
        }

        if (damager instanceof org.bukkit.entity.Projectile projectile) {

            if (projectile.getShooter() instanceof Player player) {
                return player;
            }
        }

        return null;
    }
}