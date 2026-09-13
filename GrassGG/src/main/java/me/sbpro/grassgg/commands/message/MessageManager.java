package me.sbpro.grassgg.commands.message;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Central place for /msg and /r logic: sends the formatted message to both
 * players and keeps track of who each player last spoke to (bidirectionally)
 * so that /r works from either side of the conversation. Also tracks which
 * players have disabled incoming private messages via /msgtoggle.
 */
public class MessageManager {

    // playerUuid -> uuid of the player they should reply to
    private final Map<UUID, UUID> lastConversationPartner = new HashMap<>();

    // players who currently have private messages disabled
    private final Set<UUID> messagesDisabled = new HashSet<>();

    public void sendMessage(Player sender, Player target, String message) {
        sender.sendMessage("§8[§2You §8-> §2" + target.getName() + "§8] " + message);
        target.sendMessage("§8[§2" + sender.getName() + "§8-> §2You§8] " + message);

        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        // Both players can now /r to each other, not just the sender.
        lastConversationPartner.put(sender.getUniqueId(), target.getUniqueId());
        lastConversationPartner.put(target.getUniqueId(), sender.getUniqueId());
    }

    public UUID getLastPartner(UUID playerUuid) {
        return lastConversationPartner.get(playerUuid);
    }

    public void clearPartner(UUID playerUuid) {
        lastConversationPartner.remove(playerUuid);
    }

    public boolean isMessagesDisabled(UUID playerUuid) {
        return messagesDisabled.contains(playerUuid);
    }

    public void setMessagesDisabled(UUID playerUuid, boolean disabled) {
        if (disabled) {
            messagesDisabled.add(playerUuid);
        } else {
            messagesDisabled.remove(playerUuid);
        }
    }

    /**
     * Flips the current state and returns the new state (true = now disabled).
     */
    public boolean toggleMessagesDisabled(UUID playerUuid) {
        boolean newState = !isMessagesDisabled(playerUuid);
        setMessagesDisabled(playerUuid, newState);
        return newState;
    }
}