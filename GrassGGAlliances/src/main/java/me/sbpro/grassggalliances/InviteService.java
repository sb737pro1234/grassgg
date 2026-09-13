/*
 * Decompiled with CFR 0.152.
 */
package me.sbpro.grassggalliances;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InviteService {
    private final Map<UUID, String> pendingInvites = new HashMap<UUID, String>();

    public void setInvite(UUID playerId, String allianceName) {
        this.pendingInvites.put(playerId, allianceName);
    }

    public Optional<String> getInvite(UUID playerId) {
        return Optional.ofNullable(this.pendingInvites.get(playerId));
    }

    public void clearInvite(UUID playerId) {
        this.pendingInvites.remove(playerId);
    }
}

