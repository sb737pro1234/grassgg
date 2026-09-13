package me.sbpro.grassggstaff.punishment;

import java.util.Map;

public record PunishmentTier(String id, String displayName, Map<Integer, Punishment> offences) {

    public Punishment getPunishment(int offence) {
        return offences.get(offence);
    }
}
