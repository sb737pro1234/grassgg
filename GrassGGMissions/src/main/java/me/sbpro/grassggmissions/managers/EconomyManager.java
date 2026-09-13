package me.sbpro.grassggmissions.managers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

    private static Economy economy;

    private EconomyManager() {
    }

    public static boolean setup() {

        RegisteredServiceProvider<Economy> provider =
                Bukkit.getServicesManager().getRegistration(Economy.class);

        if (provider == null) {
            return false;
        }

        economy = provider.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static boolean has(Player player, double amount) {
        return economy != null && economy.has(player, amount);
    }

    public static boolean withdraw(Player player, double amount) {

        if (economy == null) {
            return false;
        }

        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public static void deposit(Player player, double amount) {

        if (economy != null) {
            economy.depositPlayer(player, amount);
        }

    }

}