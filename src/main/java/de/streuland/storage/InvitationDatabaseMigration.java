package de.streuland.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class InvitationDatabaseMigration {
    private static final int CURRENT_VERSION = 1;

    private InvitationDatabaseMigration() {
    }

    public static boolean migrate(Path dbPath) {
        try {
            Path parent = dbPath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create directory for invitation DB", e);
        }

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            return migrate(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to migrate invitation database", e);
        }
    }

    public static boolean migrate(Connection connection) throws SQLException {
        boolean originalAutoCommit = connection.getAutoCommit();
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            connection.setAutoCommit(false);

            boolean hadSchema = tableExists(connection, "db_version")
                    || tableExists(connection, "invitations")
                    || tableExists(connection, "users");
            statement.execute("CREATE TABLE IF NOT EXISTS db_version (" +
                    "id INTEGER PRIMARY KEY CHECK (id = 1), " +
                    "version INTEGER NOT NULL" +
                    ")");
            statement.execute("INSERT OR IGNORE INTO db_version (id, version) VALUES (1, 0)");

            int version;
            try (ResultSet result = statement.executeQuery("SELECT version FROM db_version WHERE id = 1")) {
                if (!result.next()) {
                    throw new SQLException("Database version row is missing");
                }
                version = result.getInt("version");
            }
            if (version > CURRENT_VERSION) {
                throw new IllegalStateException("Invitation database version " + version
                        + " is newer than supported version " + CURRENT_VERSION);
            }

            statement.execute("CREATE TABLE IF NOT EXISTS invitations (" +
                    "id TEXT PRIMARY KEY, " +
                    "code TEXT UNIQUE, " +
                    "issuer_user_id TEXT, " +
                    "created_at TEXT, " +
                    "expires_at TEXT, " +
                    "max_uses INTEGER, " +
                    "uses INTEGER DEFAULT 0, " +
                    "is_revoked BOOLEAN, " +
                    "allowed_roles TEXT" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id TEXT PRIMARY KEY, " +
                    "username TEXT UNIQUE, " +
                    "email TEXT UNIQUE, " +
                    "password_hash TEXT, " +
                    "password_salt TEXT, " +
                    "created_at TEXT" +
                    ")");

            ensureColumn(connection, "invitations", "server_id", "TEXT");
            ensureColumn(connection, "invitations", "target_server", "TEXT");
            ensureColumn(connection, "users", "invite_code_id", "TEXT");
            ensureColumn(connection, "users", "server_id", "TEXT");

            boolean schemaChanged = version < CURRENT_VERSION;
            if (schemaChanged) {
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE db_version SET version = ? WHERE id = 1")) {
                    update.setInt(1, CURRENT_VERSION);
                    update.executeUpdate();
                }
            }
            connection.commit();
            return hadSchema && schemaChanged;
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private static boolean tableExists(Connection connection, String table) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?")) {
            statement.setString(1, table);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private static void ensureColumn(Connection connection, String table, String column, String type)
            throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet columns = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (columns.next()) {
                if (column.equalsIgnoreCase(columns.getString("name"))) {
                    return;
                }
            }
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
        }
    }
}
