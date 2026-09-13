package me.sbpro.grassggannouncements.command;

import java.util.concurrent.atomic.AtomicLong;

public class ChatLockManager {

    private final AtomicLong lockedUntil = new AtomicLong(0);

    public void lockChat() {
        lockedUntil.set(System.currentTimeMillis() + 5000);
    }

    public boolean isChatLocked() {
        return System.currentTimeMillis() < lockedUntil.get();
    }
}