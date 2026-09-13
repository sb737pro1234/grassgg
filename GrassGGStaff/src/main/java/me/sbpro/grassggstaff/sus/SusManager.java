package me.sbpro.grassggstaff.sus;

import me.sbpro.grassggstaff.GrassGGStaff;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class SusManager {

    private final GrassGGStaff plugin;
    private final SusRepository repository;

    private final Map<UUID, SusPlayer> cache =
            new ConcurrentHashMap<>();

    private BukkitTask refreshTask;

    public SusManager(
            GrassGGStaff plugin,
            SusRepository repository
    ) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public void start() {

        refreshAll();

        refreshTask =
                Bukkit.getScheduler().runTaskTimerAsynchronously(
                        plugin,
                        () -> refreshAll(),
                        20L * 10,
                        20L * 10
                );
    }

    public CompletableFuture<Void> refreshAll() {

        return repository.findAll()
                .thenAccept(notes -> {

                    Map<UUID, List<SusNote>> grouped =
                            new LinkedHashMap<>();

                    for (SusNote note : notes) {

                        grouped
                                .computeIfAbsent(
                                        note.playerUuid(),
                                        ignored ->
                                                new ArrayList<>()
                                )
                                .add(note);
                    }

                    Map<UUID, SusPlayer> replacement =
                            new LinkedHashMap<>();

                    for (Map.Entry<UUID, List<SusNote>> entry :
                            grouped.entrySet()) {

                        List<SusNote> playerNotes =
                                entry.getValue();

                        if (playerNotes.isEmpty()) {
                            continue;
                        }

                        String latestName =
                                playerNotes.get(
                                        playerNotes.size() - 1
                                ).playerName();

                        replacement.put(
                                entry.getKey(),
                                new SusPlayer(
                                        entry.getKey(),
                                        latestName,
                                        List.copyOf(playerNotes)
                                )
                        );
                    }

                    cache.clear();
                    cache.putAll(replacement);
                });
    }

    public List<SusPlayer> getPlayers() {

        List<SusPlayer> players =
                new ArrayList<>(cache.values());

        players.sort(
                Comparator.comparing(
                        SusPlayer::name,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return Collections.unmodifiableList(players);
    }

    public List<String> getReasons(
            UUID playerUuid
    ) {

        SusPlayer player =
                cache.get(playerUuid);

        if (player == null) {
            return List.of();
        }

        return player.notes()
                .stream()
                .map(SusNote::reason)
                .toList();
    }

    public CompletableFuture<AddResult> addNote(
            UUID playerUuid,
            String playerName,
            String reason,
            UUID staffUuid,
            String staffName
    ) {

        return repository.findByPlayer(playerUuid)
                .thenCompose(existing -> {

                    String normalised =
                            normalise(reason);

                    boolean duplicate =
                            existing.stream()
                                    .anyMatch(note ->
                                            normalise(
                                                    note.reason()
                                            ).equals(normalised)
                                    );

                    if (duplicate) {
                        return CompletableFuture.completedFuture(
                                AddResult.alreadyExists()
                        );
                    }

                    return repository.addNote(
                                    playerUuid,
                                    playerName,
                                    reason,
                                    staffUuid,
                                    staffName
                            )
                            .thenCompose(note ->
                                    refreshPlayer(playerUuid)
                                            .thenApply(
                                                    ignored ->
                                                            AddResult.added(
                                                                    note
                                                            )
                                            )
                            );
                });
    }

    public CompletableFuture<RemoveResult> removeNote(
            UUID playerUuid,
            String reason
    ) {

        return repository.findByPlayer(playerUuid)
                .thenCompose(notes -> {

                    String normalised =
                            normalise(reason);

                    SusNote match =
                            notes.stream()
                                    .filter(note ->
                                            normalise(
                                                    note.reason()
                                            ).equals(normalised)
                                    )
                                    .findFirst()
                                    .orElse(null);

                    if (match == null) {
                        return CompletableFuture.completedFuture(
                                RemoveResult.notFound()
                        );
                    }

                    return repository.removeNote(
                                    match.susId()
                            )
                            .thenCompose(success -> {

                                if (!success) {
                                    return CompletableFuture.completedFuture(
                                            RemoveResult.notFound()
                                    );
                                }

                                return refreshPlayer(
                                        playerUuid
                                )
                                        .thenApply(
                                                ignored ->
                                                        RemoveResult.removed(
                                                                match
                                                        )
                                        );
                            });
                });
    }

    private CompletableFuture<Void> refreshPlayer(
            UUID playerUuid
    ) {

        return repository.findByPlayer(playerUuid)
                .thenAccept(notes -> {

                    if (notes.isEmpty()) {
                        cache.remove(playerUuid);
                        return;
                    }

                    String latestName =
                            notes.get(
                                    notes.size() - 1
                            ).playerName();

                    cache.put(
                            playerUuid,
                            new SusPlayer(
                                    playerUuid,
                                    latestName,
                                    List.copyOf(notes)
                            )
                    );
                });
    }

    private String normalise(
            String input
    ) {

        return input
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }

    public void shutdown() {

        if (refreshTask != null) {
            refreshTask.cancel();
        }
    }

    public record AddResult(
            Status status,
            SusNote note
    ) {

        public static AddResult added(
                SusNote note
        ) {
            return new AddResult(
                    Status.ADDED,
                    note
            );
        }

        public static AddResult alreadyExists() {
            return new AddResult(
                    Status.ALREADY_EXISTS,
                    null
            );
        }

        public enum Status {
            ADDED,
            ALREADY_EXISTS
        }
    }

    public record RemoveResult(
            Status status,
            SusNote note
    ) {

        public static RemoveResult removed(
                SusNote note
        ) {
            return new RemoveResult(
                    Status.REMOVED,
                    note
            );
        }

        public static RemoveResult notFound() {
            return new RemoveResult(
                    Status.NOT_FOUND,
                    null
            );
        }

        public enum Status {
            REMOVED,
            NOT_FOUND
        }
    }
}