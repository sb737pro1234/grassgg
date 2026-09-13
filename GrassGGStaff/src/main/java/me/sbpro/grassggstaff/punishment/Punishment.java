package me.sbpro.grassggstaff.punishment;

public record Punishment(PunishmentType type, String duration) {
    public String display() {
        if (type == PunishmentType.WARNING) return "Warning";
        return duration + " " + type.getDisplayName();
    }
}
