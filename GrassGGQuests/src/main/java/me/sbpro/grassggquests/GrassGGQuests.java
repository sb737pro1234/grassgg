package me.sbpro.grassggquests;

import me.sbpro.grassggquests.commands.QuestsCommand;
import me.sbpro.grassggquests.data.DataManager;
import me.sbpro.grassggquests.menus.MenuListener;
import me.sbpro.grassggquests.menus.MenuManager;
import me.sbpro.grassggquests.placeholder.QuestExpansion;
import me.sbpro.grassggquests.quests.QuestListener;
import me.sbpro.grassggquests.quests.QuestManager;
import me.sbpro.grassggquests.shop.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GrassGGQuests extends JavaPlugin {
    private DataManager dataManager;
    private QuestManager questManager;
    private ShopManager shopManager;
    private MenuManager menus;
    private QuestExpansion expansion;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        dataManager = new DataManager(this);
        questManager = new QuestManager(this);
        shopManager = new ShopManager(this);
        menus = new MenuManager(this);

        PluginCommand quests = getCommand("quests");
        if (quests == null) throw new IllegalStateException("quests command missing from plugin.yml");
        QuestsCommand command = new QuestsCommand(this);
        quests.setExecutor(command);
        quests.setTabCompleter(command);

        PluginCommand shop = getCommand("questshop");
        if (shop == null) throw new IllegalStateException("questshop command missing from plugin.yml");
        shop.setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player player)) { sender.sendMessage(Messages.PREFIX + Messages.PLAYER_ONLY); return true; }
            menus.openShop(player);
            return true;
        });

        PluginCommand level = getCommand("questlevel");
        if (level == null) throw new IllegalStateException("questlevel command missing from plugin.yml");
        level.setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player player)) { sender.sendMessage(Messages.PREFIX + Messages.PLAYER_ONLY); return true; }
            menus.openLevel(player);
            return true;
        });

        Bukkit.getPluginManager().registerEvents(new QuestListener(questManager), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);

        for (Player player : Bukkit.getOnlinePlayers()) questManager.loadPlayer(player);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            expansion = new QuestExpansion(this);
            expansion.register();
            getLogger().info("PlaceholderAPI support enabled.");
        } else {
            getLogger().info("PlaceholderAPI not found. Placeholder support will be unavailable until it is installed.");
        }

        getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) { questManager.loadPlayer(event.getPlayer()); }
            @org.bukkit.event.EventHandler
            public void onQuit(org.bukkit.event.player.PlayerQuitEvent event) { questManager.unloadPlayer(event.getPlayer()); }
        }, this);

        getLogger().info("GrassGGQuests enabled.");
    }

    @Override
    public void onDisable() {
        if (questManager != null) questManager.saveAll();
        if (expansion != null) expansion.unregister();
    }

    public void reloadPluginConfig() {
        reloadConfig();
        questManager.refreshAfterConfigChange();
    }

    public DataManager getDataManager() { return dataManager; }
    public QuestManager getQuestManager() { return questManager; }
    public ShopManager getShopManager() { return shopManager; }
    public MenuManager getMenus() { return menus; }
}
