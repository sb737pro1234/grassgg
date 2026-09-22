package me.sbpro.grassgg;

import me.sbpro.grassgg.commands.clear.ClearCommand;
import me.sbpro.grassgg.commands.clear.ClearConfirmListener;
import me.sbpro.grassgg.commands.feed.FeedCommand;
import me.sbpro.grassgg.commands.feed.FeedListener;
import me.sbpro.grassgg.commands.gamemode.*;
import me.sbpro.grassgg.commands.gui.*;
import me.sbpro.grassgg.commands.heal.HealCommand;
import me.sbpro.grassgg.commands.heal.HealListener;
import me.sbpro.grassgg.commands.kill.KillCommand;
import me.sbpro.grassgg.commands.kill.KillListener;
import me.sbpro.grassgg.commands.message.MessageCommand;
import me.sbpro.grassgg.commands.message.MessageManager;
import me.sbpro.grassgg.commands.message.MsgToggleCommand;
import me.sbpro.grassgg.commands.message.ReplyCommand;
import me.sbpro.grassgg.commands.misc.*;
import me.sbpro.grassgg.commands.rules.RulesCommand;
import me.sbpro.grassgg.commands.rules.RulesListener;
import me.sbpro.grassgg.commands.say.SayCommand;
import me.sbpro.grassgg.commands.trash.TrashCommand;
import me.sbpro.grassgg.commands.trash.TrashListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class GrassGGGlobal extends JavaPlugin implements Listener {

    // /message <player> <message>
    // /reply <message>
    private final MessageManager messageManager = new MessageManager();

    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public void onEnable() {
        getCommand("kill").setExecutor(new KillCommand());
        getServer().getPluginManager().registerEvents(new KillListener(), this);

        getCommand("heal").setExecutor(new HealCommand());
        getServer().getPluginManager().registerEvents(new HealListener(), this);

        getCommand("feed").setExecutor(new FeedCommand());
        getServer().getPluginManager().registerEvents(new FeedListener(), this);

        getCommand("clear").setExecutor(new ClearCommand());
        getServer().getPluginManager().registerEvents(new ClearConfirmListener(),this);

        getCommand("youtube").setExecutor(new YouTubeCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        getCommand("store").setExecutor(new StoreCommand());
        getCommand("staff").setExecutor(new StaffCommand());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("nightvision").setExecutor(new NightVisionCommand());

        getCommand("msg").setExecutor(new MessageCommand(this));
        getCommand("r").setExecutor(new ReplyCommand(this));
        getCommand("msgtoggle").setExecutor(new MsgToggleCommand(this));
        Bukkit.getPluginManager().registerEvents(this, this);


        GameModeCommand gamemode = new GameModeCommand();
        GmcCommand gmc = new GmcCommand();
        GmsCommand gms = new GmsCommand();
        GmaCommand gma = new GmaCommand();
        GmspCommand gmsp = new GmspCommand();
        getCommand("gamemode").setExecutor(gamemode);
        getCommand("gamemode").setTabCompleter(gamemode);
        getCommand("gmc").setExecutor(gmc);
        getCommand("gmc").setTabCompleter(gmc);
        getCommand("gms").setExecutor(gms);
        getCommand("gms").setTabCompleter(gms);
        getCommand("gma").setExecutor(gma);
        getCommand("gma").setTabCompleter(gma);
        getCommand("gmsp").setExecutor(gmsp);
        getCommand("gmsp").setTabCompleter(gmsp);
        getServer().getPluginManager().registerEvents(new GamemodeListener(), this);


        getCommand("craft").setExecutor(new CraftCommand());
        getCommand("smithing").setExecutor(new SmithingCommand());
        getCommand("stonecutter").setExecutor(new StonecutterCommand());
        getCommand("loom").setExecutor(new LoomCommand());
        getCommand("cartography").setExecutor(new CartographyCommand());
        getCommand("grindstone").setExecutor(new GrindstoneCommand());
        getCommand("anvil").setExecutor(new AnvilCommand());



        getCommand("rules").setExecutor(new RulesCommand());
        getServer().getPluginManager().registerEvents(new RulesListener(),this);

        getCommand("trash").setExecutor(new TrashCommand());
        getServer().getPluginManager().registerEvents(new TrashListener(this), this);


        getCommand("say").setExecutor(new SayCommand());

    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPlayedBefore()) {

            if (player.hasPermission("grassgg.announcejoinleave")) {
                event.setJoinMessage("§8[§a§l+§8] §f" + event.getPlayer().getDisplayName());
            } else {
                event.setJoinMessage(null);
                return;
            }

        } else {
            event.setJoinMessage("§2§lGRASS.GG §8»§f§l " + player.getDisplayName() + " §2has joined the server for the first time! Welcome!");
        }

    }

    // Death
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            event.setDeathMessage(
                    "§8[§4§l⚔§8] §4" + victim.getName() + " §fwas killed by §4" + killer.getName() + "§f."
            );
        } else {
            event.setDeathMessage(null);
        }
    }

    // Player Quit
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission("grassgg.announcejoinleave")){
            event.setQuitMessage("§8[§c§l-§8] §f" + event.getPlayer().getDisplayName());
        } else {
            event.setQuitMessage(null);
            return;
        }
    }

}
