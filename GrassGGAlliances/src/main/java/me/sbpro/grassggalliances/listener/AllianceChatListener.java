/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.event.player.AsyncChatEvent
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package me.sbpro.grassggalliances.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Optional;
import java.util.UUID;
import me.sbpro.grassggalliances.ChatToggleService;
import me.sbpro.grassggalliances.MessageService;
import me.sbpro.grassggalliances.data.AllianceStorage;
import me.sbpro.grassggalliances.model.Alliance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class AllianceChatListener
implements Listener {
    private final AllianceStorage allianceStorage;
    private final MessageService messageService;
    private final ChatToggleService chatToggleService;

    public AllianceChatListener(AllianceStorage allianceStorage, MessageService messageService, ChatToggleService chatToggleService) {
        this.allianceStorage = allianceStorage;
        this.messageService = messageService;
        this.chatToggleService = chatToggleService;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (!this.chatToggleService.isAllianceChatEnabled(player.getUniqueId())) {
            return;
        }
        Optional<Alliance> allianceOptional = this.allianceStorage.getAllianceByPlayer(player.getUniqueId());
        if (allianceOptional.isEmpty()) {
            this.chatToggleService.disable(player.getUniqueId());
            return;
        }
        event.setCancelled(true);
        Alliance alliance = allianceOptional.get();
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        Component formatted = this.messageService.prefixed("chat-format", text -> text.replace("%alliance%", alliance.getName()).replace("%player%", player.getName()).replace("%message%", plainMessage));
        for (UUID member : alliance.getMembers()) {
            Player target = Bukkit.getPlayer((UUID)member);
            if (target == null) continue;
            target.sendMessage(formatted);
        }
    }
}

