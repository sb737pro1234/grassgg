package me.sbpro.grassggsurvival;

// Java Util Imports
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// Bukkit Imports
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


// Vault
import net.milkbowl.vault.economy.Economy;

// My Imports
import me.sbpro.grassggsurvival.enchants.AutoSmeltEnchant;
import me.sbpro.grassggsurvival.settings.*;
import me.sbpro.grassggsurvival.commands.*;
import me.sbpro.grassggsurvival.listeners.HidePlayerListener;


public final class GrassGGSurvival extends JavaPlugin implements Listener {

    private static GrassGGSurvival instance;

    private Economy economy;

    private final Set<UUID> hiddenPlayerUsers = new HashSet<>();



    // Economy
    private Economy setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return null;
        }

        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return null;
        }

        return rsp.getProvider();
    }



    // Settings Managers
    private ChatToggleManager chatToggleManager;

    public ChatToggleManager getChatToggleManager() {
        return chatToggleManager;
    }


    private NightVisionManager nightVisionManager;

    public NightVisionManager getNightVisionManager() {
        return nightVisionManager;
    }


    private ScoreboardManager scoreboardManager;

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }


    private GlowManager glowManager;

    public GlowManager getGlowManager() {
        return glowManager;
    }


    // Enable
    @Override
    public void onEnable() {

        instance = this;

        getLogger().info("Grass.GG Network » Server Starting.");

        getConfig().options().copyDefaults();
        saveDefaultConfig();


        // Commands
        HidePlayerCommand hidePlayerCommand = new HidePlayerCommand(this);
        getCommand("hideplayer").setExecutor(hidePlayerCommand);
        getCommand("hideplayer").setTabCompleter(hidePlayerCommand);

        getCommand("glow").setExecutor(new GlowCommand(this));
        getCommand("settings").setExecutor(new SettingsCommand(this));
        getCommand("chattoggle").setExecutor(new ChatToggleCommand(this));


        AutoSmeltEnchant autoSmelt = new AutoSmeltEnchant(this);
        getCommand("customenchant").setExecutor(new CustomEnchantCommand(autoSmelt));


        // Listeners / Events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new HidePlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new SettingsListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerSettingsListener(this), this);


        // Economy
        economy = setupEconomy();

        if (economy == null) {
            getLogger().severe("Vault economy not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        // Managers
        chatToggleManager = new ChatToggleManager(this);
        scoreboardManager = new ScoreboardManager(this);
        nightVisionManager = new NightVisionManager(this);
        glowManager = new GlowManager(this);
    }


    // Disable
    @Override
    public void onDisable() {



        getLogger().info("Grass.GG Network » Server Stopping.");
    }


    // Player Join
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()){


            // Grassy Helmet
            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();

            if (helmetMeta != null) {
                helmetMeta.setColor(Color.GREEN);
                helmetMeta.setDisplayName("§2Grassy Helmet");
                helmet.setItemMeta(helmetMeta);
            }


            // Grassy Chestplate
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta chestMeta = (LeatherArmorMeta) chestplate.getItemMeta();

            if (chestMeta != null) {
                chestMeta.setColor(Color.GREEN);
                chestMeta.setDisplayName("§2Grassy Chestplate");
                chestplate.setItemMeta(chestMeta);
            }


            // Grassy Leggings
            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();

            if (leggingsMeta != null) {
                leggingsMeta.setColor(Color.GREEN);
                leggingsMeta.setDisplayName("§2Grassy Leggings");
                leggings.setItemMeta(leggingsMeta);
            }


            // Grassy Boots
            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();

            if (bootsMeta != null) {
                bootsMeta.setColor(Color.GREEN);
                bootsMeta.setDisplayName("§2Grassy Boots");
                boots.setItemMeta(bootsMeta);
            }


            // Give armour
            player.getInventory().setHelmet(helmet);
            player.getInventory().setChestplate(chestplate);
            player.getInventory().setLeggings(leggings);
            player.getInventory().setBoots(boots);


            // Give starter items
            player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
            player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
            player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
            player.getInventory().addItem(new ItemStack(Material.STONE_SHOVEL));
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
// Teleport first-time players to SurvivalSpawn.
// Delayed so other plugins can finish their join teleport first.
            Bukkit.getScheduler().runTaskLater(this, () -> {

                World world = Bukkit.getWorld("SurvivalSpawn");

                if (world != null && player.isOnline()) {
                    player.teleport(new Location(world, 0.5, 1, 0.5));
                }

            }, 10L);
        }
    }




    // Player Respawn
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();

        // If the player has no bed / respawn anchor set,
        // send them to SurvivalSpawn.
        if (player.getRespawnLocation() == null) {

            World world = Bukkit.getWorld("SurvivalSpawn");

            if (world != null) {
                event.setRespawnLocation(
                        new Location(world, 0.5, 1, 0.5)
                );
            }
        }
    }





    // Advancement
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        event.message(null);
    }


    // Instance
    public static GrassGGSurvival getInstance() {
        return instance;
    }


    // Hidden Players
    public Set<UUID> getHiddenPlayerUsers() {
        return hiddenPlayerUsers;
    }


    public boolean isHidingPlayers(UUID uuid) {
        return hiddenPlayerUsers.contains(uuid);
    }


    public void setHidingPlayers(UUID uuid, boolean hiding) {

        if (hiding) {
            hiddenPlayerUsers.add(uuid);
        } else {
            hiddenPlayerUsers.remove(uuid);
        }
    }
}