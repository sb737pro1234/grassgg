package me.sbpro.grassggstaff.sus;

import me.sbpro.grassggstaff.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SusRepository {

    private final DatabaseManager database;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(
                    2,
                    runnable -> {
                        Thread thread = new Thread(
                                runnable,
                                "GrassGGStaff-SusDB"
                        );

                        thread.setDaemon(true);
                        return thread;
                    }
            );

    public SusRepository(DatabaseManager database) {
        this.database = database;
    }

    public boolean createTable() {

        String sql = """
                CREATE TABLE IF NOT EXISTS grassgg_sus (
                    sus_id BIGINT NOT NULL AUTO_INCREMENT,

                    player_uuid CHAR(36) NOT NULL,
                    player_name VARCHAR(16) NOT NULL,

                    reason VARCHAR(255) NOT NULL,

                    staff_uuid CHAR(36) NOT NULL,
                    staff_name VARCHAR(16) NOT NULL,

                    created_at TIMESTAMP(3) NOT NULL,

                    PRIMARY KEY (sus_id),
                    INDEX idx_sus_player (player_uuid),
                    INDEX idx_sus_player_name (player_name)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;

        try (
                Connection connection = database.connection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.executeUpdate();
            return true;

        } catch (SQLException exception) {

            exception.printStackTrace();
            return false;
        }
    }

    public CompletableFuture<List<SusNote>> findAll() {

        return CompletableFuture.supplyAsync(
                () -> {

                    List<SusNote> notes = new ArrayList<>();

                    String sql = """
                            SELECT
                                sus_id,
                                player_uuid,
                                player_name,
                                reason,
                                staff_uuid,
                                staff_name,
                                created_at
                            FROM grassgg_sus
                            ORDER BY player_name ASC, sus_id ASC
                            """;

                    try (
                            Connection connection = database.connection();
                            PreparedStatement statement =
                                    connection.prepareStatement(sql);
                            ResultSet resultSet =
                                    statement.executeQuery()
                    ) {

                        while (resultSet.next()) {
                            notes.add(map(resultSet));
                        }

                        return notes;

                    } catch (SQLException exception) {
                        throw new DatabaseException(exception);
                    }
                },
                executor
        );
    }

    public CompletableFuture<List<SusNote>> findByPlayer(
            UUID playerUuid
    ) {

        return CompletableFuture.supplyAsync(
                () -> {

                    List<SusNote> notes = new ArrayList<>();

                    String sql = """
                            SELECT
                                sus_id,
                                player_uuid,
                                player_name,
                                reason,
                                staff_uuid,
                                staff_name,
                                created_at
                            FROM grassgg_sus
                            WHERE player_uuid = ?
                            ORDER BY sus_id ASC
                            """;

                    try (
                            Connection connection = database.connection();
                            PreparedStatement statement =
                                    connection.prepareStatement(sql)
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
                                notes.add(map(resultSet));
                            }
                        }

                        return notes;

                    } catch (SQLException exception) {
                        throw new DatabaseException(exception);
                    }
                },
                executor
        );
    }

    public CompletableFuture<SusNote> addNote(
            UUID playerUuid,
            String playerName,
            String reason,
            UUID staffUuid,
            String staffName
    ) {

        return CompletableFuture.supplyAsync(
                () -> {

                    String sql = """
                            INSERT INTO grassgg_sus (
                                player_uuid,
                                player_name,
                                reason,
                                staff_uuid,
                                staff_name,
                                created_at
                            )
                            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP(3))
                            """;

                    try (
                            Connection connection = database.connection();
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
                                reason
                        );

                        statement.setString(
                                4,
                                staffUuid.toString()
                        );

                        statement.setString(
                                5,
                                staffName
                        );

                        statement.executeUpdate();

                        try (
                                ResultSet generatedKeys =
                                        statement.getGeneratedKeys()
                        ) {

                            if (!generatedKeys.next()) {
                                throw new SQLException(
                                        "Could not retrieve SUS ID."
                                );
                            }

                            long susId =
                                    generatedKeys.getLong(1);

                            return new SusNote(
                                    susId,
                                    playerUuid,
                                    playerName,
                                    reason,
                                    staffUuid,
                                    staffName,
                                    java.time.Instant.now()
                            );
                        }

                    } catch (SQLException exception) {
                        throw new DatabaseException(exception);
                    }
                },
                executor
        );
    }

    public CompletableFuture<Boolean> removeNote(
            long susId
    ) {

        return CompletableFuture.supplyAsync(
                () -> {

                    String sql = """
                            DELETE FROM grassgg_sus
                            WHERE sus_id = ?
                            """;

                    try (
                            Connection connection = database.connection();
                            PreparedStatement statement =
                                    connection.prepareStatement(sql)
                    ) {

                        statement.setLong(
                                1,
                                susId
                        );

                        return statement.executeUpdate() > 0;

                    } catch (SQLException exception) {
                        throw new DatabaseException(exception);
                    }
                },
                executor
        );
    }

    private SusNote map(
            ResultSet resultSet
    ) throws SQLException {

        UUID playerUuid =
                UUID.fromString(
                        resultSet.getString(
                                "player_uuid"
                        )
                );

        UUID staffUuid =
                UUID.fromString(
                        resultSet.getString(
                                "staff_uuid"
                        )
                );

        Timestamp timestamp =
                resultSet.getTimestamp(
                        "created_at"
                );

        return new SusNote(
                resultSet.getLong("sus_id"),
                playerUuid,
                resultSet.getString("player_name"),
                resultSet.getString("reason"),
                staffUuid,
                resultSet.getString("staff_name"),
                timestamp.toInstant()
        );
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public static final class DatabaseException
            extends RuntimeException {

        public DatabaseException(Throwable cause) {
            super(cause);
        }
    }
}