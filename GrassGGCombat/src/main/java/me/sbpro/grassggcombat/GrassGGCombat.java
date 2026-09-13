package me.sbpro.grassggcombat;

import org.bukkit.plugin.java.JavaPlugin;
import me.sbpro.grassggcombat.*;

public final class GrassGGCombat extends JavaPlugin {

    private CombatManager combatManager;

    @Override
    public void onEnable() {
        combatManager = new CombatManager(this);

        getServer().getPluginManager().registerEvents(
                new CombatListener(combatManager),
                this
        );

        getServer().getPluginManager().registerEvents(
                new CombatCommandListener(combatManager),
                this
        );

        getServer().getPluginManager().registerEvents(
                new CombatQuitListener(combatManager),
                this
        );

        getServer().getPluginManager().registerEvents(
                new CombatJoinListener(combatManager),
                this
        );

        getServer().getPluginManager().registerEvents(
                new CombatDeathListener(combatManager),
                this
        );

        combatManager.startTimer();

        getLogger().info("GrassGGCombat has been enabled!");
    }

    @Override
    public void onDisable() {
        if (combatManager != null) {
            combatManager.stopTimer();
        }

        getLogger().info("GrassGGCombat has been disabled!");
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }
}