/*
 * Decompiled with CFR 0.152.
 */
package me.sbpro.grassggalliances;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ChatToggleService {
    private final Set<UUID> allianceChat = new HashSet<UUID>();

    public boolean toggle(UUID uuid) {
        if (this.allianceChat.remove(uuid)) {
            return false;
        }
        this.allianceChat.add(uuid);
        return true;
    }

    public boolean isAllianceChatEnabled(UUID uuid) {
        return this.allianceChat.contains(uuid);
    }

    public void disable(UUID uuid) {
        this.allianceChat.remove(uuid);
    }

    public void disableAll(Iterable<UUID> uuids) {
        for (UUID uuid : uuids) {
            this.allianceChat.remove(uuid);
        }
    }
}

