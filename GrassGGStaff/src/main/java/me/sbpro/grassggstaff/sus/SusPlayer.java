package me.sbpro.grassggstaff.sus;

import java.util.List;
import java.util.UUID;

public record SusPlayer(
        UUID uuid,
        String name,
        List<SusNote> notes
) {
}