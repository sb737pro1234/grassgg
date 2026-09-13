/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package me.sbpro.grassggalliances.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import me.sbpro.grassggalliances.ChatToggleService;
import me.sbpro.grassggalliances.GrassGGAlliances;
import me.sbpro.grassggalliances.InviteService;
import me.sbpro.grassggalliances.MessageService;
import me.sbpro.grassggalliances.data.AllianceStorage;
import me.sbpro.grassggalliances.gui.AllianceLevelGui;
import me.sbpro.grassggalliances.gui.DeleteAllianceGui;
import me.sbpro.grassggalliances.model.Alliance;
import me.sbpro.grassggalliances.service.AllianceBuffService;
import me.sbpro.grassggalliances.service.AllianceXpService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class AllianceCommand
implements CommandExecutor,
TabCompleter {
    private static final int MAX_MEMBERS = 5;
    private static final Pattern HEX_PATTERN = Pattern.compile("(?i)(#[0-9a-f]{6}|&x(&[0-9a-f]){6})");
    private static final Pattern AMPERSAND_COLOR_PATTERN = Pattern.compile("(?i)&[0-9a-fk-or]");
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("[A-Za-z0-9 _-]+");
    private final GrassGGAlliances plugin;
    private final AllianceStorage allianceStorage;
    private final MessageService messageService;
    private final ChatToggleService chatToggleService;
    private final InviteService inviteService;
    private final AllianceBuffService buffService;
    private final AllianceXpService xpService;

    public AllianceCommand(GrassGGAlliances plugin, AllianceStorage allianceStorage, MessageService messageService, ChatToggleService chatToggleService, InviteService inviteService, AllianceBuffService buffService, AllianceXpService xpService) {
        this.plugin = plugin;
        this.allianceStorage = allianceStorage;
        this.messageService = messageService;
        this.chatToggleService = chatToggleService;
        this.inviteService = inviteService;
        this.buffService = buffService;
        this.xpService = xpService;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.messageService.getComponent("player-only"));
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            player.sendMessage(this.messageService.prefixed("usage"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create": {
                this.handleCreate(player, args);
                break;
            }
            case "info": {
                this.handleInfo(player, args);
                break;
            }
            case "delete": {
                this.handleDelete(player);
                break;
            }
            case "chat": {
                this.handleChat(player);
                break;
            }
            case "invite": {
                this.handleInvite(player, args);
                break;
            }
            case "accept": {
                this.handleAccept(player);
                break;
            }
            case "kick": {
                this.handleKick(player, args);
                break;
            }
            case "top": {
                this.handleTop(player);
                break;
            }
            case "level": {
                this.handleLevel(player);
                break;
            }
            default: {
                player.sendMessage(this.messageService.prefixed("usage"));
            }
        }
        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(this.messageService.prefixed("create-usage"));
            return;
        }
        if (this.allianceStorage.getAllianceByPlayer(player.getUniqueId()).isPresent()) {
            player.sendMessage(this.messageService.prefixed("already-in-alliance"));
            return;
        }
        String name = String.join((CharSequence)" ", Arrays.copyOfRange(args, 1, args.length)).trim();
        if (name.length() < 3 || name.length() > 24) {
            player.sendMessage(this.messageService.prefixed("name-length"));
            return;
        }
        if (HEX_PATTERN.matcher(name).find() || AMPERSAND_COLOR_PATTERN.matcher(name).find() || !VALID_NAME_PATTERN.matcher(name).matches()) {
            player.sendMessage(this.messageService.prefixed("invalid-name"));
            return;
        }
        if (this.allianceStorage.getAllianceByName(name).isPresent()) {
            player.sendMessage(this.messageService.prefixed("alliance-exists"));
            return;
        }
        if (this.allianceStorage.createAlliance(name, player.getUniqueId())) {
            this.buffService.refreshPlayer(player);
            player.sendMessage(this.messageService.prefixed("created", text -> text.replace("%alliance%", name)));
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(this.messageService.prefixed("not-found", text -> text.replace("%input%", "")));
            return;
        }
        String input = String.join((CharSequence)" ", Arrays.copyOfRange(args, 1, args.length)).trim();
        Optional<Alliance> allianceOptional = this.findAlliance(input);
        if (allianceOptional.isEmpty()) {
            player.sendMessage(this.messageService.prefixed("not-found", text -> text.replace("%input%", input)));
            return;
        }
        Alliance alliance = allianceOptional.get();
        OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID)alliance.getOwner());
        String ownerName = owner.getName() == null ? alliance.getOwner().toString() : owner.getName();
        String members = alliance.getMembers().stream().map(Bukkit::getOfflinePlayer).map(offline -> offline.getName() == null ? offline.getUniqueId().toString() : offline.getName()).sorted(String.CASE_INSENSITIVE_ORDER).reduce((left, right) -> left + ", " + right).orElse(ownerName);
        for (Component line : this.messageService.getComponentList("info-format", text -> text.replace("%alliance%", alliance.getName()).replace("%owner%", ownerName).replace("%members%", members))) {
            player.sendMessage(line);
        }
    }

    private Optional<Alliance> findAlliance(String input) {
        Optional<Alliance> byName = this.allianceStorage.getAllianceByName(input);
        if (byName.isPresent()) {
            return byName;
        }
        Player online = Bukkit.getPlayerExact((String)input);
        if (online != null) {
            return this.allianceStorage.getAllianceByPlayer(online.getUniqueId());
        }
        OfflinePlayer cached = Bukkit.getOfflinePlayerIfCached((String)input);
        if (cached != null && (cached.getName() != null || cached.hasPlayedBefore())) {
            return this.allianceStorage.getAllianceByPlayer(cached.getUniqueId());
        }
        return Optional.empty();
    }

    private void handleDelete(Player player) {
        Optional<Alliance> allianceOptional = this.allianceStorage.getAllianceByPlayer(player.getUniqueId());
        if (allianceOptional.isEmpty()) {
            player.sendMessage(this.messageService.prefixed("no-alliance"));
            return;
        }
        Alliance alliance = allianceOptional.get();
        if (!alliance.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(this.messageService.prefixed("owner-only-delete"));
            return;
        }
        DeleteAllianceGui.open(this.plugin, player, alliance.getName());
        player.sendMessage(this.messageService.prefixed("delete-opened"));
    }

    private void handleChat(Player player) {
        if (this.allianceStorage.getAllianceByPlayer(player.getUniqueId()).isEmpty()) {
            player.sendMessage(this.messageService.prefixed("no-alliance"));
            return;
        }
        boolean enabled = this.chatToggleService.toggle(player.getUniqueId());
        player.sendMessage(this.messageService.prefixed(enabled ? "chat-enabled" : "chat-disabled"));
    }

    private void handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(this.messageService.prefixed("invite-usage"));
            return;
        }
        Optional<Alliance> allianceOptional = this.allianceStorage.getAllianceByPlayer(player.getUniqueId());
        if (allianceOptional.isEmpty()) {
            player.sendMessage(this.messageService.prefixed("no-alliance"));
            return;
        }
        Alliance alliance = allianceOptional.get();
        if (!alliance.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(this.messageService.prefixed("owner-only-invite"));
            return;
        }
        if (alliance.getMembers().size() >= 5) {
            player.sendMessage(this.messageService.prefixed("alliance-full"));
            return;
        }
        Player target = Bukkit.getPlayerExact((String)args[1]);
        if (target == null) {
            player.sendMessage(this.messageService.prefixed("target-not-online"));
            return;
        }
        if (this.allianceStorage.getAllianceByPlayer(target.getUniqueId()).isPresent()) {
            player.sendMessage(this.messageService.prefixed("target-already-in-alliance"));
            return;
        }
        this.inviteService.setInvite(target.getUniqueId(), alliance.getName());
        player.sendMessage(this.messageService.prefixed("invite-sent", text -> text.replace("%player%", target.getName())));
        target.sendMessage(this.messageService.prefixed("invite-received", text -> text.replace("%alliance%", alliance.getName()).replace("%owner%", player.getName())));
    }

    private void handleAccept(Player player) {
        if (this.allianceStorage.getAllianceByPlayer(player.getUniqueId()).isPresent()) {
            player.sendMessage(this.messageService.prefixed("already-in-alliance"));
            this.inviteService.clearInvite(player.getUniqueId());
            return;
        }
        Optional<String> inviteOptional = this.inviteService.getInvite(player.getUniqueId());
        if (inviteOptional.isEmpty()) {
            player.sendMessage(this.messageService.prefixed("no-pending-invite"));
            return;
        }
        Optional<Alliance> allianceOptional = this.allianceStorage.getAllianceByName(inviteOptional.get());
        if (allianceOptional.isEmpty()) {
            this.inviteService.clearInvite(player.getUniqueId());
            player.sendMessage(this.messageService.prefixed("invite-expired"));
            return;
        }
        Alliance alliance = allianceOptional.get();
        if (alliance.getMembers().size() >= 5) {
            this.inviteService.clearInvite(player.getUniqueId());
            player.sendMessage(this.messageService.prefixed("alliance-full"));
            return;
        }
        if (this.allianceStorage.addMember(alliance.getName(), player.getUniqueId())) {
            this.inviteService.clearInvite(player.getUniqueId());
            this.buffService.refreshPlayer(player);
            player.sendMessage(this.messageService.prefixed("joined", text -> text.replace("%alliance%", alliance.getName())));
            for (UUID memberId : this.allianceStorage.getAllianceByName(alliance.getName()).orElse(alliance).getMembers()) {
                Player member = Bukkit.getPlayer((UUID)memberId);
                if (member == null || member.getUniqueId().equals(player.getUniqueId())) continue;
                member.sendMessage(this.messageService.prefixed("member-joined", text -> text.replace("%player%", player.getName())));
            }
        } else {
            player.sendMessage(this.messageService.prefixed("invite-expired"));
        }
    }

    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(this.messageService.prefixed("kick-usage"));
            return;
        }
        Optional<Alliance> allianceOptional = this.allianceStorage.getAllianceByPlayer(player.getUniqueId());
        if (allianceOptional.isEmpty()) {
            player.sendMessage(this.messageService.prefixed("no-alliance"));
            return;
        }
        Alliance alliance = allianceOptional.get();
        if (!alliance.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(this.messageService.prefixed("owner-only-kick"));
            return;
        }
        Player target = Bukkit.getPlayerExact((String)args[1]);
        if (target == null) {
            player.sendMessage(this.messageService.prefixed("target-not-online"));
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(this.messageService.prefixed("cannot-kick-self"));
            return;
        }
        Optional<Alliance> targetAlliance = this.allianceStorage.getAllianceByPlayer(target.getUniqueId());
        if (targetAlliance.isEmpty() || !targetAlliance.get().getName().equalsIgnoreCase(alliance.getName())) {
            player.sendMessage(this.messageService.prefixed("player-not-in-your-alliance"));
            return;
        }
        if (targetAlliance.get().getOwner().equals(target.getUniqueId())) {
            player.sendMessage(this.messageService.prefixed("cannot-kick-owner"));
            return;
        }
        if (this.allianceStorage.removeMember(alliance.getName(), target.getUniqueId())) {
            this.chatToggleService.disable(target.getUniqueId());
            this.buffService.clearPlayer(target);
            target.sendMessage(this.messageService.prefixed("kicked-target", text -> text.replace("%alliance%", alliance.getName())));
            player.sendMessage(this.messageService.prefixed("kicked-by-owner", text -> text.replace("%player%", target.getName())));
            for (UUID memberId : this.allianceStorage.getAllianceByName(alliance.getName()).orElse(alliance).getMembers()) {
                Player member = Bukkit.getPlayer((UUID)memberId);
                if (member == null || member.getUniqueId().equals(player.getUniqueId())) continue;
                member.sendMessage(this.messageService.prefixed("kicked-broadcast", text -> text.replace("%player%", target.getName())));
            }
        }
    }

    private void handleTop(Player player) {
        List<Alliance> topAlliances = this.xpService.getTopAlliances();
        if (topAlliances.isEmpty()) {
            player.sendMessage(this.messageService.prefixed("top-empty"));
            return;
        }
        for (Component line : this.messageService.getComponentList("top-header", text -> text)) {
            player.sendMessage(line);
        }
        int position = 1;
        for (Alliance alliance : topAlliances.stream().limit(10L).toList()) {
            int currentPosition = position++;
            String xp = String.valueOf(Math.round(alliance.getXp()));
            player.sendMessage(this.messageService.prefixed("top-entry", text -> text.replace("%position%", String.valueOf(currentPosition)).replace("%alliance%", alliance.getName()).replace("%level%", String.valueOf(alliance.getLevel())).replace("%xp%", xp)));
        }
    }

    private void handleLevel(Player player) {
        Optional<Alliance> allianceOptional = this.allianceStorage.getAllianceByPlayer(player.getUniqueId());
        if (allianceOptional.isEmpty()) {
            player.sendMessage(this.messageService.prefixed("no-alliance"));
            return;
        }
        AllianceLevelGui.openMain(this.plugin, player, allianceOptional.get());
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("create", "info", "delete", "chat", "invite", "accept", "kick", "top", "level").stream().filter(option -> option.startsWith(args[0].toLowerCase())).toList();
        }
        return new ArrayList<String>();
    }
}

