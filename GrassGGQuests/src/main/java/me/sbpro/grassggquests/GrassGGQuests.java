package me.sbpro.grassggquests;

import me.sbpro.grassggcoins.data.CoinManager;
import me.sbpro.grassggquests.commands.QuestsCommand;
import me.sbpro.grassggquests.data.DataManager;
import me.sbpro.grassggquests.menus.MenuListener;
import me.sbpro.grassggquests.menus.MenuManager;
import me.sbpro.grassggquests.quests.QuestBossBarManager;
import me.sbpro.grassggquests.quests.QuestListener;
import me.sbpro.grassggquests.quests.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class GrassGGQuests extends JavaPlugin {
    private DataManager dataManager;
    private QuestManager questManager;
    private MenuManager menus;
    private QuestBossBarManager bossBars;
    private me.sbpro.grassggcoins.GrassGGCoins coinsPlugin;

    @Override
    public void onEnable() {
        coinsPlugin = (me.sbpro.grassggcoins.GrassGGCoins) Bukkit.getPluginManager().getPlugin("GrassGGCoins");
        if (coinsPlugin == null) {
            getLogger().severe("GrassGGCoins was not found. Disabling GrassGGQuests.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        dataManager = new DataManager(this);
        questManager = new QuestManager(this);
        menus = new MenuManager(this);
        bossBars = new QuestBossBarManager(this);

        PluginCommand quests = getCommand("quests");
        if (quests == null) throw new IllegalStateException("quests command missing from plugin.yml");
        QuestsCommand command = new QuestsCommand(this);
        quests.setExecutor(command);
        quests.setTabCompleter(command);

        Bukkit.getPluginManager().registerEvents(new QuestListener(questManager), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @EventHandler
            public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) { questManager.loadPlayer(event.getPlayer()); }
            @EventHandler
            public void onQuit(org.bukkit.event.player.PlayerQuitEvent event) { questManager.unloadPlayer(event.getPlayer()); }
        }, this);

        for (Player player : Bukkit.getOnlinePlayers()) questManager.loadPlayer(player);
        getLogger().info("GrassGGQuests enabled.");
    }

    @Override
    public void onDisable() {
        if (questManager != null) questManager.saveAll();
        if (bossBars != null) bossBars.removeAll();
    }

    public void reloadPluginConfig() { reloadConfig(); questManager.refreshAfterConfigChange(); }
    public me.sbpro.grassggcoins.GrassGGCoins getCoinsPlugin() { return coinsPlugin; }
    public CoinManager getCoinManager() { return coinsPlugin.getCoinManager(); }
    public DataManager getDataManager() { return dataManager; }
    public QuestManager getQuestManager() { return questManager; }
    public MenuManager getMenus() { return menus; }
    public QuestBossBarManager getBossBars() { return bossBars; }
}
