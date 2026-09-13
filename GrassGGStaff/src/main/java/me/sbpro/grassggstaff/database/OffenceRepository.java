package me.sbpro.grassggstaff.database;

import me.sbpro.grassggstaff.punishment.OffenceRecord;
import me.sbpro.grassggstaff.punishment.Punishment;
import me.sbpro.grassggstaff.punishment.PunishmentType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class OffenceRepository {

    private final DatabaseManager database;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(
                    4,
                    runnable -> {

                        Thread thread =
                                new Thread(
                                        runnable,
                                        "GrassGGStaff-DB"
                                );

                        thread.setDaemon(true);

                        return thread;
                    }
            );

    public OffenceRepository(
            DatabaseManager database
    ) {
        this.database = database;
    }

    public boolean createTables() {

        String sql = """
                CREATE TABLE IF NOT EXISTS grassgg_offences (
                    offence_id BIGINT NOT NULL AUTO_INCREMENT,
                    player_uuid CHAR(36) NOT NULL,
                    player_name VARCHAR(16) NOT NULL,

                    reason_id VARCHAR(100) NOT NULL,
                    reason_name VARCHAR(255) NOT NULL,

                    tier VARCHAR(50) NOT NULL,
                    offence_number INT NOT NULL,

                    punishment_type VARCHAR(20) NOT NULL,
                    duration VARCHAR(50) NOT NULL,
                    duration_seconds BIGINT NULL,

                    started_at TIMESTAMP(3) NOT NULL,
                    expires_at TIMESTAMP(3) NULL,

                    active BOOLEAN NOT NULL DEFAULT TRUE,
                    removed BOOLEAN NOT NULL DEFAULT FALSE,

                    removed_by VARCHAR(36) NULL,
                    removed_at TIMESTAMP(3) NULL,

                    staff_uuid CHAR(36) NOT NULL,
                    staff_name VARCHAR(16) NOT NULL,

                    replaced_at TIMESTAMP(3) NULL,

                    PRIMARY KEY (offence_id),
                    INDEX idx_player (player_uuid),
                    INDEX idx_player_tier (player_uuid, tier),
                    INDEX idx_active (player_uuid, active)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;

        try (
                Connection connection =
                        database.connection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.executeUpdate();

            /*
             * Make sure columns introduced by newer versions
             * exist when using an existing database.
             */
            addColumnIfMissing(
                    connection,
                    "replaced_at",
                    "TIMESTAMP(3) NULL"
            );

            addColumnIfMissing(
                    connection,
                    "removed_by",
                    "VARCHAR(36) NULL"
            );

            addColumnIfMissing(
                    connection,
                    "removed_at",
                    "TIMESTAMP(3) NULL"
            );

            return true;

        } catch (SQLException exception) {

            exception.printStackTrace();

            return false;
        }
    }

    private void addColumnIfMissing(
            Connection connection,
            String name,
            String definition
    ) throws SQLException {

        String sql =
                "ALTER TABLE grassgg_offences ADD COLUMN "
                        + name
                        + " "
                        + definition;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.executeUpdate();

        } catch (SQLException exception) {

            /*
             * MySQL error 1060 = column already exists.
             */
            if (exception.getErrorCode() != 1060) {
                throw exception;
            }
        }
    }

    public CompletableFuture<List<OffenceRecord>> findHistory(
            UUID playerUuid
    ) {

        return CompletableFuture.supplyAsync(
                () -> {

                    List<OffenceRecord> records =
                            new ArrayList<>();

                    String sql = """
                            SELECT *
                            FROM grassgg_offences
                            WHERE player_uuid = ?
                            ORDER BY offence_id ASC
                            """;

                    try (
                            Connection connection =
                                    database.connection();

                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            sql
                                    )
                    ) {

                        statement.setString(
                                1,
                                playerUuid.toString()
                        );

                        try (
                                ResultSet resultSet =
                                        statement.executeQuery()
                        ) {

                            while (resultSet.next()) {

                                records.add(
                                        map(resultSet)
                                );
                            }
                        }

                        return records;

                    } catch (SQLException exception) {

                        throw new CompletionDatabaseException(
                                exception
                        );
                    }
                },
                executor
        );
    }

    public CompletableFuture<OffenceRecord> findActivePunishment(
            UUID playerUuid
    ) {

        return CompletableFuture.supplyAsync(
                () -> {

                    String sql = """
                            SELECT *
                            FROM grassgg_offences
                            WHERE player_uuid = ?
                              AND active = TRUE
                              AND removed = FALSE
                              AND (
                                  expires_at IS NULL
                                  OR expires_at > CURRENT_TIMESTAMP(3)
                              )
                            ORDER BY offence_id DESC
                            LIMIT 1
                            """;

                    try (
                            Connection connection =
                                    database.connection();

                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            sql
                                    )
                    ) {

                        statement.setString(
                                1,
                                playerUuid.toString()
                        );

                        try (
                                ResultSet resultSet =
                                        statement.executeQuery()
                        ) {

                            if (!resultSet.next()) {
                                return null;
                            }

                            return map(resultSet);
                        }

                    } catch (SQLException exception) {

                        throw new CompletionDatabaseException(
                                exception
                        );
                    }
                },
                executor
        );
    }

    /**
     * Compatibility overload.
     */
    public CompletableFuture<Void> replaceActivePunishments(
            UUID playerUuid,
            Instant replacedAt
    ) {

        return replaceActivePunishments(
                playerUuid,
                new UUID(0L, 0L),
                "System",
                replacedAt
        );
    }

    /**
     * Marks all currently active punishments as removed/replaced.
     */
    public CompletableFuture<Void> replaceActivePunishments(
            UUID playerUuid,
            UUID staffUuid,
            String staffName,
            Instant replacedAt
    ) {

        return CompletableFuture.runAsync(
                () -> {

                    String sql = """
                            UPDATE grassgg_offences
                            SET active = FALSE,
                                removed = TRUE,
                                removed_by = ?,
                                removed_at = ?,
                                replaced_at = ?
                            WHERE player_uuid = ?
                              AND active = TRUE
                              AND removed = FALSE
                            """;

                    try (
                            Connection connection =
                                    database.connection();

                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            sql
                                    )
                    ) {

                        statement.setString(
                                1,
                                staffName
                        );

                        statement.setTimestamp(
                                2,
                                Timestamp.from(
                                        replacedAt
                                )
                        );

                        statement.setTimestamp(
                                3,
                                Timestamp.from(
                                        replacedAt
                                )
                        );

                        statement.setString(
                                4,
                                playerUuid.toString()
                        );

                        statement.executeUpdate();

                    } catch (SQLException exception) {

                        throw new CompletionDatabaseException(
                                exception
                        );
                    }
                },
                executor
        );
    }

    public CompletableFuture<Long> insertOffence(
            UUID playerUuid,
            String playerName,
            String reasonId,
            String reasonName,
            String tier,
            int offenceNumber,
            Punishment punishment,
            Instant started,
            Instant expires,
            UUID staffUuid,
            String staffName
    ) {

        return CompletableFuture.supplyAsync(
                () -> {

                    String sql = """
                            INSERT INTO grassgg_offences
                            (
                                player_uuid,
                                player_name,
                                reason_id,
                                reason_name,
                                tier,
                                offence_number,
                                punishment_type,
                                duration,
                                duration_seconds,
                                started_at,
                                expires_at,
                                active,
                                removed,
                                staff_uuid,
                                staff_name
                            )
                            VALUES
                            (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE, FALSE, ?, ?)
                            """;

                    try (
                            Connection connection =
                                    database.connection();

                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            sql,
                                            Statement.RETURN_GENERATED_KEYS
                                    )
                    ) {

                        statement.setString(
                                1,
                                playerUuid.toString()
                        );

                        statement.setString(
                                2,
                                playerName
                        );

                        statement.setString(
                                3,
                                reasonId
                        );

                        statement.setString(
                                4,
                                reasonName
                        );

                        statement.setString(
                                5,
                                tier
                        );

                        statement.setInt(
                                6,
                                offenceNumber
                        );

                        statement.setString(
                                7,
                                punishment.type().name()
                        );

                        statement.setString(
                                8,
                                punishment.duration()
                        );

                        /*
                         * Fixed-duration punishments get seconds.
                         * Calendar durations such as 6mo are represented
                         * by expires_at instead.
                         */
                        Long seconds = null;

                        try {

                            long parsed =
                                    me.sbpro.grassggstaff.util.DurationParser
                                            .parse(
                                                    punishment.duration()
                                            );

                            if (parsed > 0) {
                                seconds = parsed;
                            }

                        } catch (Exception ignored) {
                        }

                        if (seconds == null) {

                            statement.setNull(
                                    9,
                                    Types.BIGINT
                            );

                        } else {

                            statement.setLong(
                                    9,
                                    seconds
                            );
                        }

                        statement.setTimestamp(
                                10,
                                Timestamp.from(
                                        started
                                )
                        );

                        if (expires == null) {

                            statement.setNull(
                                    11,
                                    Types.TIMESTAMP
                            );

                        } else {

                            statement.setTimestamp(
                                    11,
                                    Timestamp.from(
                                            expires
                                    )
                            );
                        }

                        statement.setString(
                                12,
                                staffUuid.toString()
                        );

                        statement.setString(
                                13,
                                staffName
                        );

                        statement.executeUpdate();

                        try (
                                ResultSet keys =
                                        statement.getGeneratedKeys()
                        ) {

                            if (!keys.next()) {

                                throw new SQLException(
                                        "No offence ID was generated."
                                );
                            }

                            return keys.getLong(1);
                        }

                    } catch (SQLException exception) {

                        throw new CompletionDatabaseException(
                                exception
                        );
                    }
                },
                executor
        );
    }

    public CompletableFuture<Boolean> removeActivePunishment(
            UUID playerUuid,
            UUID staffUuid,
            String staffName
    ) {

        return CompletableFuture.supplyAsync(
                () -> {

                    String sql = """
                            UPDATE grassgg_offences
                            SET active = FALSE,
                                removed = TRUE,
                                removed_by = ?,
                                removed_at = CURRENT_TIMESTAMP(3)
                            WHERE player_uuid = ?
                              AND active = TRUE
                              AND removed = FALSE
                            """;

                    try (
                            Connection connection =
                                    database.connection();

                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            sql
                                    )
                    ) {

                        statement.setString(
                                1,
                                staffName
                        );

                        statement.setString(
                                2,
                                playerUuid.toString()
                        );

                        return statement.executeUpdate() > 0;

                    } catch (SQLException exception) {

                        throw new CompletionDatabaseException(
                                exception
                        );
                    }
                },
                executor
        );
    }

    public CompletableFuture<OffenceRecord> updateActivePunishment(
            UUID playerUuid,
            OffenceRecord oldRecord,
            String reasonId,
            String reasonName,
            String tier,
            int offenceNumber,
            Punishment punishment,
            Instant startedAt,
            Instant expiresAt,
            UUID staffUuid,
            String staffName
    ) {

        return CompletableFuture.supplyAsync(
                () -> {

                    String sql = """
                            UPDATE grassgg_offences
                            SET reason_id = ?,
                                reason_name = ?,
                                tier = ?,
                                offence_number = ?,
                                punishment_type = ?,
                                duration = ?,
                                duration_seconds = ?,
                                started_at = ?,
                                expires_at = ?,
                                staff_uuid = ?,
                                staff_name = ?
                            WHERE offence_id = ?
                              AND player_uuid = ?
                              AND active = TRUE
                              AND removed = FALSE
                            """;

                    try (
                            Connection connection =
                                    database.connection();

                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            sql
                                    )
                    ) {

                        statement.setString(
                                1,
                                reasonId
                        );

                        statement.setString(
                                2,
                                reasonName
                        );

                        statement.setString(
                                3,
                                tier
                        );

                        statement.setInt(
                                4,
                                offenceNumber
                        );

                        statement.setString(
                                5,
                                punishment.type().name()
                        );

                        statement.setString(
                                6,
                                punishment.duration()
                        );

                        Long seconds = null;

                        try {

                            long parsed =
                                    me.sbpro.grassggstaff.util.DurationParser
                                            .parse(
                                                    punishment.duration()
                                            );

                            if (parsed > 0) {
                                seconds = parsed;
                            }

                        } catch (Exception ignored) {
                        }

                        if (seconds == null) {

                            statement.setNull(
                                    7,
                                    Types.BIGINT
                            );

                        } else {

                            statement.setLong(
                                    7,
                                    seconds
                            );
                        }

                        statement.setTimestamp(
                                8,
                                Timestamp.from(
                                        startedAt
                                )
                        );

                        if (expiresAt == null) {

                            statement.setNull(
                                    9,
                                    Types.TIMESTAMP
                            );

                        } else {

                            statement.setTimestamp(
                                    9,
                                    Timestamp.from(
                                            expiresAt
                                    )
                            );
                        }

                        statement.setString(
                                10,
                                staffUuid.toString()
                        );

                        statement.setString(
                                11,
                                staffName
                        );

                        statement.setLong(
                                12,
                                oldRecord.offenceId()
                        );

                        statement.setString(
                                13,
                                playerUuid.toString()
                        );

                        if (statement.executeUpdate() == 0) {
                            return null;
                        }

                        return new OffenceRecord(
                                oldRecord.offenceId(),
                                oldRecord.playerUuid(),
                                oldRecord.playerName(),

                                reasonId,
                                reasonName,

                                tier,
                                offenceNumber,

                                punishment.type(),
                                punishment.duration(),

                                startedAt,
                                expiresAt,

                                true,
                                false,

                                oldRecord.removedBy(),
                                oldRecord.removedAt(),

                                staffUuid,
                                staffName,

                                oldRecord.replacedAt()
                        );

                    } catch (SQLException exception) {

                        throw new CompletionDatabaseException(
                                exception
                        );
                    }
                },
                executor
        );
    }

    private OffenceRecord map(
            ResultSet rs
    ) throws SQLException {

        Timestamp expires =
                rs.getTimestamp(
                        "expires_at"
                );

        Timestamp removedAt =
                rs.getTimestamp(
                        "removed_at"
                );

        Timestamp replacedAt =
                rs.getTimestamp(
                        "replaced_at"
                );

        boolean active =
                rs.getBoolean(
                        "active"
                );

        /*
         * Expired punishments aren't enforceable.
         */
        if (expires != null &&
                !expires.toInstant()
                        .isAfter(
                                Instant.now()
                        )) {

            active = false;
        }

        if (rs.getBoolean("removed") ||
                replacedAt != null) {

            active = false;
        }

        return new OffenceRecord(
                rs.getLong(
                        "offence_id"
                ),

                UUID.fromString(
                        rs.getString(
                                "player_uuid"
                        )
                ),

                rs.getString(
                        "player_name"
                ),

                rs.getString(
                        "reason_id"
                ),

                rs.getString(
                        "reason_name"
                ),

                rs.getString(
                        "tier"
                ),

                rs.getInt(
                        "offence_number"
                ),

                PunishmentType.valueOf(
                        rs.getString(
                                "punishment_type"
                        )
                ),

                rs.getString(
                        "duration"
                ),

                rs.getTimestamp(
                        "started_at"
                ).toInstant(),

                expires == null
                        ? null
                        : expires.toInstant(),

                active,

                rs.getBoolean(
                        "removed"
                ),

                rs.getString(
                        "removed_by"
                ),

                removedAt == null
                        ? null
                        : removedAt.toInstant(),

                UUID.fromString(
                        rs.getString(
                                "staff_uuid"
                        )
                ),

                rs.getString(
                        "staff_name"
                ),

                replacedAt == null
                        ? null
                        : replacedAt.toInstant()
        );
    }

    public CompletableFuture<Void> expirePunishments() {

        return CompletableFuture.runAsync(
                () -> {

                    String sql = """
                            UPDATE grassgg_offences
                            SET active = FALSE
                            WHERE active = TRUE
                              AND removed = FALSE
                              AND expires_at IS NOT NULL
                              AND expires_at <= CURRENT_TIMESTAMP(3)
                            """;

                    try (
                            Connection connection =
                                    database.connection();

                            PreparedStatement statement =
                                    connection.prepareStatement(
                                            sql
                                    )
                    ) {

                        statement.executeUpdate();

                    } catch (SQLException exception) {

                        throw new CompletionDatabaseException(
                                exception
                        );
                    }
                },
                executor
        );
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public static final class CompletionDatabaseException
            extends RuntimeException {

        public CompletionDatabaseException(
                Throwable cause
        ) {
            super(cause);
        }
    }

    public CompletableFuture<Boolean> resetPlayerHistory(
            UUID playerUuid
    ) {

        return CompletableFuture.supplyAsync(
                () -> {

                    String deleteAuditSql = """
                        DELETE FROM grassgg_offence_audit
                        WHERE offence_id IN (
                            SELECT offence_id
                            FROM grassgg_offences
                            WHERE player_uuid = ?
                        )
                        """;

                    String deleteOffenceSql = """
                        DELETE FROM grassgg_offences
                        WHERE player_uuid = ?
                        """;

                    try (
                            Connection connection =
                                    database.connection()
                    ) {

                        boolean previousAutoCommit =
                                connection.getAutoCommit();

                        try {

                            connection.setAutoCommit(false);

                            /*
                             * Delete audit entries belonging to this
                             * player's offences first.
                             */
                            try (
                                    PreparedStatement auditStatement =
                                            connection.prepareStatement(
                                                    deleteAuditSql
                                            )
                            ) {

                                auditStatement.setString(
                                        1,
                                        playerUuid.toString()
                                );

                                auditStatement.executeUpdate();
                            }

                            /*
                             * Now delete the player's offence history.
                             */
                            try (
                                    PreparedStatement offenceStatement =
                                            connection.prepareStatement(
                                                    deleteOffenceSql
                                            )
                            ) {

                                offenceStatement.setString(
                                        1,
                                        playerUuid.toString()
                                );

                                offenceStatement.executeUpdate();
                            }

                            connection.commit();

                            connection.setAutoCommit(
                                    previousAutoCommit
                            );

                            return true;

                        } catch (Exception exception) {

                            try {
                                connection.rollback();
                            } catch (SQLException ignored) {
                            }

                            try {
                                connection.setAutoCommit(
                                        previousAutoCommit
                                );
                            } catch (SQLException ignored) {
                            }

                            throw exception;
                        }

                    } catch (Exception exception) {

                        throw new CompletionDatabaseException(
                                exception
                        );
                    }
                },
                executor
        );
    }
}