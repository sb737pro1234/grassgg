package me.sbpro.grassggstaff.punishment;

public enum PunishmentType {
    WARNING("Warning"),
    MUTE("Mute"),
    BAN("Ban");

    private final String displayName;

    PunishmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
