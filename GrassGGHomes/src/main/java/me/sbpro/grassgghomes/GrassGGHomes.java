package me.sbpro.grassgghomes;

import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGHomes extends JavaPlugin {

    private HomesManager homesManager;

    public HomesManager getHomesManager() {
        return homesManager;
    }

    @Override
    public void onEnable() {

        /*
         * Config
         */
        getConfig().options().copyDefaults();
        saveDefaultConfig();


        /*
         * Startup message
         */
        getLogger().info("GrassGG Homes » Enabled.");


        /*
         * Homes Manager
         */
        homesManager = new HomesManager(this);
        homesManager.loadHomes();


        /*
         * /homes
         *
         * /home is registered as an alias
         * in plugin.yml.
         */
        if (getCommand("homes") != null) {

            getCommand("homes").setExecutor(
                    new HomesCommand(this)
            );

            getCommand("homes").setTabCompleter(
                    new HomeTabCompleter(this)
            );
        }


        /*
         * /sethome
         */
        if (getCommand("sethome") != null) {

            getCommand("sethome").setExecutor(
                    new SetHomeCommand(this)
            );

            getCommand("sethome").setTabCompleter(
                    new SetHomeTabCompleter()
            );
        }


        /*
         * /delhome
         */
        if (getCommand("delhome") != null) {

            getCommand("delhome").setExecutor(
                    new DelHomeCommand(this)
            );

            getCommand("delhome").setTabCompleter(
                    new DelHomeTabCompleter(this)
            );
        }


        /*
         * Event listeners
         */
        getServer()
                .getPluginManager()
                .registerEvents(
                        new HomesListener(this),
                        this
                );


        getLogger().info("GrassGG Homes » Loaded successfully.");
    }


    @Override
    public void onDisable() {

        if (homesManager != null) {

            homesManager.saveHomes();
        }

        getLogger().info("GrassGG Homes » Disabled.");
    }
}