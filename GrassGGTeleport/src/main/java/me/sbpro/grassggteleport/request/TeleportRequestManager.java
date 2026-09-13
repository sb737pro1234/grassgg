package me.sbpro.grassggteleport.request;

import me.sbpro.grassggteleport.message.Messages;
import me.sbpro.grassggteleport.settings.TeleportPreferencesManager;
import me.sbpro.grassggteleport.teleport.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TeleportRequestManager {

    private static final long REQUEST_EXPIRATION_MILLIS = 180_000L;

    private final JavaPlugin plugin;
    private final TeleportManager teleportManager;
    private final TeleportPreferencesManager preferencesManager;

    private final Map<UUID, Map<UUID, TeleportRequest>> incomingRequests =
            new HashMap<>();

    private final Map<UUID, Map<UUID, TeleportRequest>> outgoingRequests =
            new HashMap<>();

    private BukkitTask expirationTask;

    public TeleportRequestManager(
            JavaPlugin plugin,
            TeleportManager teleportManager,
            TeleportPreferencesManager preferencesManager
    ) {
        this.plugin = plugin;
        this.teleportManager = teleportManager;
        this.preferencesManager = preferencesManager;

        startExpirationTask();
    }

    public boolean sendRequest(
            Player requester,
            Player target,
            TeleportRequestType type
    ) {
        if (requester.getUniqueId().equals(target.getUniqueId())) {
            Messages.sendError(
                    requester,
                    "You cannot send a teleport request to yourself."
            );
            return false;
        }

        if (preferencesManager.isTeleportRequestsDisabled(target)) {
            requester.sendMessage(
                    Messages.teleportsDisabled(target)
            );
            return false;
        }

        if (hasOutgoingRequest(requester, target)) {
            requester.sendMessage(
                    Messages.requestAlreadySent(target)
            );
            return false;
        }

        TeleportRequest request = new TeleportRequest(
                requester.getUniqueId(),
                target.getUniqueId(),
                type
        );

        incomingRequests
                .computeIfAbsent(
                        target.getUniqueId(),
                        ignored -> new HashMap<>()
                )
                .put(
                        requester.getUniqueId(),
                        request
                );

        outgoingRequests
                .computeIfAbsent(
                        requester.getUniqueId(),
                        ignored -> new HashMap<>()
                )
                .put(
                        target.getUniqueId(),
                        request
                );

        /*
         * /tpauto only applies to /tpa.
         * /tpahere is never automatically accepted.
         */
        if (type == TeleportRequestType.TPA
                && preferencesManager.shouldAutoAcceptTpa(target)) {

            requester.sendMessage(
                    Messages.requestSent(target)
            );

            requester.sendMessage(
                    Messages.requestAutoAccepted(target)
            );

            target.sendMessage(
                    Messages.requestAccepted(requester)
            );

            removeRequest(request);

            startAcceptedTeleport(
                    requester,
                    target,
                    request
            );

            return true;
        }

        if (type == TeleportRequestType.TPA) {
            requester.sendMessage(
                    Messages.requestSent(target)
            );

            target.sendMessage(
                    Messages.requestReceived(requester)
            );
        } else {
            requester.sendMessage(
                    Messages.requestSentToTeleportToYou(target)
            );

            target.sendMessage(
                    Messages.requestReceivedToYou(requester)
            );
        }

        return true;
    }

    public boolean acceptMostRecent(Player target) {
        TeleportRequest request =
                getMostRecentIncoming(target);

        if (request == null) {
            Messages.sendError(
                    target,
                    "You do not have a pending teleport request."
            );
            return false;
        }

        return acceptRequest(target, request);
    }

    public boolean acceptFrom(
            Player target,
            Player requester
    ) {
        TeleportRequest request =
                getIncomingRequest(
                        target,
                        requester
                );

        if (request == null) {
            Messages.sendError(
                    target,
                    "You do not have a pending teleport request from "
                            + requester.getName()
                            + "."
            );
            return false;
        }

        return acceptRequest(target, request);
    }

    public boolean denyMostRecent(Player target) {
        TeleportRequest request =
                getMostRecentIncoming(target);

        if (request == null) {
            Messages.sendError(
                    target,
                    "You do not have a pending teleport request."
            );
            return false;
        }

        denyRequest(target, request);
        return true;
    }

    public boolean cancelOutgoing(Player requester) {
        Map<UUID, TeleportRequest> requests =
                outgoingRequests.get(
                        requester.getUniqueId()
                );

        if (requests == null || requests.isEmpty()) {
            Messages.sendError(
                    requester,
                    "You do not have any outgoing teleport requests."
            );
            return false;
        }

        List<TeleportRequest> requestsToCancel =
                new ArrayList<>(
                        requests.values()
                );

        for (TeleportRequest request : requestsToCancel) {
            removeRequest(request);

            Player target = Bukkit.getPlayer(
                    request.getTarget()
            );

            if (target != null && target.isOnline()) {
                target.sendMessage(
                        Messages.requestCancelledByRequester(
                                requester
                        )
                );
            }
        }

        requester.sendMessage(
                Messages.requestCancelled()
        );

        return true;
    }

    public void handleDisconnect(Player player) {
        UUID uuid = player.getUniqueId();

        /*
         * Requests where this player was the target.
         */
        Map<UUID, TeleportRequest> incoming =
                incomingRequests.remove(uuid);

        if (incoming != null) {
            for (TeleportRequest request :
                    new ArrayList<>(incoming.values())) {

                outgoingRequests.computeIfPresent(
                        request.getRequester(),
                        (ignored, requests) -> {
                            requests.remove(uuid);

                            if (requests.isEmpty()) {
                                return null;
                            }

                            return requests;
                        }
                );

                Player requester = Bukkit.getPlayer(
                        request.getRequester()
                );

                if (requester != null && requester.isOnline()) {
                    requester.sendMessage(
                            Messages.playerOffline(player)
                    );
                }
            }
        }

        /*
         * Requests where this player was the requester.
         */
        Map<UUID, TeleportRequest> outgoing =
                outgoingRequests.remove(uuid);

        if (outgoing != null) {
            for (TeleportRequest request :
                    new ArrayList<>(outgoing.values())) {

                incomingRequests.computeIfPresent(
                        request.getTarget(),
                        (ignored, requests) -> {
                            requests.remove(uuid);

                            if (requests.isEmpty()) {
                                return null;
                            }

                            return requests;
                        }
                );

                Player target = Bukkit.getPlayer(
                        request.getTarget()
                );

                if (target != null && target.isOnline()) {
                    target.sendMessage(
                            Messages.playerOffline(player)
                    );
                }
            }
        }
    }

    public void shutdown() {
        if (expirationTask != null) {
            expirationTask.cancel();
            expirationTask = null;
        }

        incomingRequests.clear();
        outgoingRequests.clear();
    }

    private boolean acceptRequest(
            Player target,
            TeleportRequest request
    ) {
        Player requester = Bukkit.getPlayer(
                request.getRequester()
        );

        if (requester == null || !requester.isOnline()) {
            removeRequest(request);

            Messages.sendError(
                    target,
                    "That player is no longer online."
            );

            return false;
        }

        removeRequest(request);

        target.sendMessage(
                Messages.requestAccepted(requester)
        );

        startAcceptedTeleport(
                requester,
                target,
                request
        );

        return true;
    }

    private void startAcceptedTeleport(
            Player requester,
            Player target,
            TeleportRequest request
    ) {
        if (request.getType() == TeleportRequestType.TPA) {
            /*
             * The destination is deliberately resolved when
             * the countdown finishes, not when the request
             * is accepted.
             */
            teleportManager.startTeleport(
                    requester,
                    () -> target.isOnline()
                            ? target.getLocation().clone()
                            : null
            );
        } else {
            /*
             * /tpahere teleports the target to the requester.
             * Again, the requester's CURRENT location is used
             * when the countdown completes.
             */
            teleportManager.startTeleport(
                    target,
                    () -> requester.isOnline()
                            ? requester.getLocation().clone()
                            : null
            );
        }
    }

    private void denyRequest(
            Player target,
            TeleportRequest request
    ) {
        Player requester = Bukkit.getPlayer(
                request.getRequester()
        );

        removeRequest(request);

        if (requester != null && requester.isOnline()) {
            requester.sendMessage(
                    Messages.requestDenied(target)
            );

            target.sendMessage(
                    Messages.requestDenied(requester)
            );
        } else {
            target.sendMessage(
                    Messages.requestDenied(target)
            );
        }
    }

    private TeleportRequest getMostRecentIncoming(
            Player target
    ) {
        Map<UUID, TeleportRequest> requests =
                incomingRequests.get(
                        target.getUniqueId()
                );

        if (requests == null || requests.isEmpty()) {
            return null;
        }

        TeleportRequest mostRecent = null;

        for (TeleportRequest request : requests.values()) {
            if (mostRecent == null
                    || request.getCreatedAt()
                    > mostRecent.getCreatedAt()) {
                mostRecent = request;
            }
        }

        return mostRecent;
    }

    private TeleportRequest getIncomingRequest(
            Player target,
            Player requester
    ) {
        Map<UUID, TeleportRequest> requests =
                incomingRequests.get(
                        target.getUniqueId()
                );

        if (requests == null) {
            return null;
        }

        return requests.get(
                requester.getUniqueId()
        );
    }

    private boolean hasOutgoingRequest(
            Player requester,
            Player target
    ) {
        Map<UUID, TeleportRequest> requests =
                outgoingRequests.get(
                        requester.getUniqueId()
                );

        return requests != null
                && requests.containsKey(
                target.getUniqueId()
        );
    }

    private void removeRequest(
            TeleportRequest request
    ) {
        UUID requester = request.getRequester();
        UUID target = request.getTarget();

        incomingRequests.computeIfPresent(
                target,
                (ignored, requests) -> {
                    requests.remove(requester);

                    if (requests.isEmpty()) {
                        return null;
                    }

                    return requests;
                }
        );

        outgoingRequests.computeIfPresent(
                requester,
                (ignored, requests) -> {
                    requests.remove(target);

                    if (requests.isEmpty()) {
                        return null;
                    }

                    return requests;
                }
        );
    }

    private void startExpirationTask() {
        expirationTask = plugin.getServer()
                .getScheduler()
                .runTaskTimer(
                        plugin,
                        this::removeExpiredRequests,
                        20L,
                        20L
                );
    }

    private void removeExpiredRequests() {
        long now = System.currentTimeMillis();

        List<TeleportRequest> expiredRequests =
                new ArrayList<>();

        for (Map<UUID, TeleportRequest> requests
                : incomingRequests.values()) {

            for (TeleportRequest request :
                    requests.values()) {

                if (now - request.getCreatedAt()
                        >= REQUEST_EXPIRATION_MILLIS) {

                    expiredRequests.add(request);
                }
            }
        }

        for (TeleportRequest request :
                expiredRequests) {

            removeRequest(request);

            Player requester = Bukkit.getPlayer(
                    request.getRequester()
            );

            Player target = Bukkit.getPlayer(
                    request.getTarget()
            );

            if (requester != null
                    && requester.isOnline()
                    && target != null) {

                requester.sendMessage(
                        Messages.requestExpiredTo(target)
                );
            }

            if (target != null
                    && target.isOnline()
                    && requester != null) {

                target.sendMessage(
                        Messages.requestExpiredFrom(requester)
                );
            }
        }
    }
}