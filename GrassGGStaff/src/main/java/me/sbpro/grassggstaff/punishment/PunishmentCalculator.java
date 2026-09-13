package me.sbpro.grassggstaff.punishment;

import java.util.Collection;

public final class PunishmentCalculator {

    public int nextOffenceNumber(Collection<OffenceRecord> history, String tier) {
        return history.stream()
                .filter(record -> record.tier().equalsIgnoreCase(tier))
                .mapToInt(OffenceRecord::offenceNumber)
                .max()
                .orElse(0) + 1;
    }

    public Punishment calculate(PunishmentTier tier, int offenceNumber) {
        return tier.getPunishment(offenceNumber);
    }
}
