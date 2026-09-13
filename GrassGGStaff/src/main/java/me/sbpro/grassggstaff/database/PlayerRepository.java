package me.sbpro.grassggstaff.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PlayerRepository {

    private final DatabaseManager database;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(
                    2,
                    runnable -> {
                        Thread thread =
                                new Thread(
                                        runnable,
                                        "GrassGGStaff-PlayerDB"
                                );

                        thread.setDaemon(true);
                        return thread;
                    }
            );

    public PlayerRepository(DatabaseManager database) {
        this.database = database;
    }

    public boolean createTable() {

        String sql = """
                CREATE TABLE IF NOT EXISTS grassgg_players (
                    player_uuid CHAR(36) NOT NULL,
                    player_name VARCHAR(16) NOT NULL,
                    first_seen TIMESTAMP(3) NOT NULL,
                    last_seen TIMESTAMP(3) NOT NULL,
                    PRIMARY KEY (player_uuid),
                    INDEX idx_player_name (player_name)
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

            return false;
        }
    }

    public CompletableFuture<Void> savePlayer(
            UUID uuid,
            String name
    ) {

        return CompletableFuture.runAsync(() -> {

            String sql = """
                    INSERT INTO grassgg_players
                        (player_uuid, player_name, first_seen, last_seen)
                    VALUES
                        (?, ?, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3))
                    ON DUPLICATE KEY UPDATE
                        player_name = VALUES(player_name),
                        last_seen = CURRENT_TIMESTAMP(3)
                    """;

            try (
                    Connection connection = database.connection();
                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setString(
                        1,
                        uuid.toString()
                );

                statement.setString(
                        2,
                        name
                );

                statement.executeUpdate();

            } catch (SQLException exception) {

                throw new RuntimeException(exception);
            }

        }, executor);
    }

    public CompletableFuture<UUID> findUuid(
            String playerName
    ) {

        return CompletableFuture.supplyAsync(() -> {

            String sql = """
                    SELECT player_uuid
                    FROM grassgg_players
                    WHERE LOWER(player_name) = LOWER(?)
                    LIMIT 1
                    """;

            try (
                    Connection connection = database.connection();
                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setString(
                        1,
                        playerName
                );

                try (
                        ResultSet resultSet =
                                statement.executeQuery()
                ) {

                    if (!resultSet.next()) {
                        return null;
                    }

                    return UUID.fromString(
                            resultSet.getString(
                                    "player_uuid"
                            )
                    );
                }

            } catch (SQLException exception) {

                throw new RuntimeException(exception);
            }

        }, executor);
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}