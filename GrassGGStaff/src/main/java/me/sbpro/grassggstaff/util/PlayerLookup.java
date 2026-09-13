package me.sbpro.grassggstaff.util;

import me.sbpro.grassggstaff.GrassGGStaff;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerLookup {

    private PlayerLookup() {
    }

    public static CompletableFuture<Result> find(
            GrassGGStaff plugin,
            String playerName
    ) {

        /*
         * Online players are always preferred.
         */
        Player online =
                Bukkit.getPlayerExact(playerName);

        if (online != null) {

            return CompletableFuture.completedFuture(
                    Result.found(
                            online.getUniqueId(),
                            online.getName()
                    )
            );
        }

        /*
         * Player isn't online.
         *
         * Look them up in our shared MySQL player table.
         */
        return plugin.getPlayerRepository()
                .findUuid(playerName)
                .thenApply(uuid -> {

                    if (uuid == null) {
                        return Result.notFound();
                    }

                    return Result.found(
                            uuid,
                            playerName
                    );
                });
    }

    public record Result(
            boolean found,
            UUID uuid,
            String name
    ) {

        public static Result found(
                UUID uuid,
                String name
        ) {
            return new Result(
                    true,
                    uuid,
                    name
            );
        }

        public static Result notFound() {
            return new Result(
                    false,
                    null,
                    null
            );
        }
    }
}