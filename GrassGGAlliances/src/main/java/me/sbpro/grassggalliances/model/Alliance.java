/*
 * Decompiled with CFR 0.152.
 */
package me.sbpro.grassggalliances.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Alliance {
    private final String name;
    private final UUID owner;
    private final Set<UUID> members;
    private double xp;
    private int level;

    public Alliance(String name, UUID owner, Set<UUID> members, double xp, int level) {
        this.name = name;
        this.owner = owner;
        this.members = new HashSet<UUID>(members);
        this.xp = xp;
        this.level = level;
    }

    public String getName() {
        return this.name;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(this.members);
    }

    public boolean addMember(UUID uuid) {
        return this.members.add(uuid);
    }

    public boolean removeMember(UUID uuid) {
        return this.members.remove(uuid);
    }

    public double getXp() {
        return this.xp;
    }

    public void addXp(double amount) {
        this.xp += amount;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

