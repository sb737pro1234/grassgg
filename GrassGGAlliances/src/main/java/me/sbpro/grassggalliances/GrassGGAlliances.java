/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.sbpro.grassggalliances;

import me.sbpro.grassggalliances.ChatToggleService;
import me.sbpro.grassggalliances.InviteService;
import me.sbpro.grassggalliances.MessageService;
import me.sbpro.grassggalliances.command.AllianceCommand;
import me.sbpro.grassggalliances.data.AllianceStorage;
import me.sbpro.grassggalliances.gui.AllianceLevelGuiListener;
import me.sbpro.grassggalliances.gui.DeleteAllianceListener;
import me.sbpro.grassggalliances.hook.AlliancePlaceholderExpansion;
import me.sbpro.grassggalliances.listener.AllianceChatListener;
import me.sbpro.grassggalliances.listener.AlliancePlayerListener;
import me.sbpro.grassggalliances.listener.AllianceXpListener;
import me.sbpro.grassggalliances.service.AllianceBuffService;
import me.sbpro.grassggalliances.service.AllianceXpService;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrassGGAlliances
extends JavaPlugin {
    private AllianceStorage allianceStorage;
    private MessageService messageService;
    private ChatToggleService chatToggleService;
    private InviteService inviteService;
    private AllianceBuffService buffService;
    private AllianceXpService xpService;

    public void onEnable() {
        this.messageService = new MessageService(this);
        this.messageService.saveDefaultMessages();
        this.allianceStorage = new AllianceStorage(this);
        this.chatToggleService = new ChatToggleService();
        this.inviteService = new InviteService();
        this.buffService = new AllianceBuffService(this, this.allianceStorage);
        this.xpService = new AllianceXpService(this, this.allianceStorage, this.messageService, this.buffService);
        AllianceCommand allianceCommand = new AllianceCommand(this, this.allianceStorage, this.messageService, this.chatToggleService, this.inviteService, this.buffService, this.xpService);
        PluginCommand command = this.getCommand("alliance");
        if (command != null) {
            command.setExecutor((CommandExecutor)allianceCommand);
            command.setTabCompleter((TabCompleter)allianceCommand);
        }
        this.getServer().getPluginManager().registerEvents((Listener)new DeleteAllianceListener(this.allianceStorage, this.messageService, this.chatToggleService, this.buffService), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new AllianceChatListener(this.allianceStorage, this.messageService, this.chatToggleService), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new AllianceXpListener(this.xpService), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new AlliancePlayerListener(this, this.buffService), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new AllianceLevelGuiListener(this, this.allianceStorage, this.messageService, this.xpService), (Plugin)this);
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new AlliancePlaceholderExpansion(this, this.allianceStorage).register();
        }
        this.buffService.refreshAllOnlinePlayers();
    }

    public MessageService getMessageService() {
        return this.messageService;
    }

    public AllianceStorage getAllianceStorage() {
        return this.allianceStorage;
    }

    public ChatToggleService getChatToggleService() {
        return this.chatToggleService;
    }

    public InviteService getInviteService() {
        return this.inviteService;
    }

    public AllianceBuffService getBuffService() {
        return this.buffService;
    }

    public AllianceXpService getXpService() {
        return this.xpService;
    }
}

