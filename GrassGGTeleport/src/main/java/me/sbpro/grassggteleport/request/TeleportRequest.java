package me.sbpro.grassggteleport.request;

import java.util.UUID;

public final class TeleportRequest {

    private final UUID requester;
    private final UUID target;
    private final TeleportRequestType type;
    private final long createdAt;

    public TeleportRequest(
            UUID requester,
            UUID target,
            TeleportRequestType type
    ) {
        this.requester = requester;
        this.target = target;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getRequester() {
        return requester;
    }

    public UUID getTarget() {
        return target;
    }

    public TeleportRequestType getType() {
        return type;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}