package me.sbpro.fastplace.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FastPlaceManager {
    private final Map<UUID, Integer> activePlayers = new ConcurrentHashMap<>();

    public void enable(UUID uuid, int amount) {
        this.activePlayers.put(uuid, amount);
    }

    public void disable(UUID uuid) {
        this.activePlayers.remove(uuid);
    }

    public boolean isEnabled(UUID uuid) {
        return this.activePlayers.containsKey(uuid);
    }

    public int getAmount(UUID uuid) {
        return this.activePlayers.getOrDefault(uuid, 0);
    }
}
