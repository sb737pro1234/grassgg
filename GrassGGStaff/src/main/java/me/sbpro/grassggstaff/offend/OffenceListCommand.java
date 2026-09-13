package me.sbpro.grassggstaff.offend;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.reason.PunishmentReason;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class OffenceListCommand {

    private final GrassGGStaff plugin;

    public OffenceListCommand(GrassGGStaff plugin) {
        this.plugin = plugin;
    }

    public boolean execute(
            CommandSender sender,
            String[] args
    ) {

        sender.sendMessage(
                Messages.OFFENCE_LIST_HEADER
        );

        Map<String, List<PunishmentReason>> byTier =
                new LinkedHashMap<>();

        for (PunishmentReason reason :
                plugin.getReasonManager().all()) {

            byTier.computeIfAbsent(
                    reason.tier(),
                    ignored -> new java.util.ArrayList<>()
            ).add(reason);
        }

        if (byTier.isEmpty()) {

            sender.sendMessage(
                    Messages.OFFENCE_LIST_EMPTY
            );

            return true;
        }

        for (Map.Entry<String, List<PunishmentReason>> entry :
                byTier.entrySet()) {

            String tierId = entry.getKey();

            String tierName =
                    plugin.getPunishmentConfig()
                            .getTier(tierId)
                            .map(tier -> tier.displayName())
                            .orElse(tierId);

            sender.sendMessage(
                    Messages.OFFENCE_LIST_TIER
                            .replace(
                                    "{tier}",
                                    tierName
                            )
            );

            for (PunishmentReason reason :
                    entry.getValue()) {

                sender.sendMessage(
                        Messages.OFFENCE_LIST_REASON
                                .replace(
                                        "{reason}",
                                        reason.displayName()
                                )
                );
            }

            sender.sendMessage("");
        }

        return true;
    }

    public List<String> tabComplete(
            CommandSender sender,
            String[] args
    ) {
        return List.of();
    }
}