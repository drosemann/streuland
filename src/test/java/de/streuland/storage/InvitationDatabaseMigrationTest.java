package de.streuland.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvitationDatabaseMigrationTest {
    @Test
    void migratesLegacySchemaAndPreservesData(@TempDir Path tempDir) throws Exception {
        Path dbPath = tempDir.resolve("invite.sqlite");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE invitations (id TEXT PRIMARY KEY, code TEXT UNIQUE, issuer_user_id TEXT, "
                    + "created_at TEXT, expires_at TEXT, max_uses INTEGER, uses INTEGER DEFAULT 0, "
                    + "is_revoked BOOLEAN, allowed_roles TEXT)");
            statement.execute("INSERT INTO invitations (id, code) VALUES ('invite-1', 'ABC123')");
            statement.execute("CREATE TABLE users (id TEXT PRIMARY KEY, username TEXT UNIQUE, email TEXT UNIQUE, "
                    + "password_hash TEXT, password_salt TEXT, created_at TEXT)");
            statement.execute("INSERT INTO users (id, username) VALUES ('user-1', 'alex')");
        }

        assertTrue(InvitationDatabaseMigration.migrate(dbPath));
        assertFalse(InvitationDatabaseMigration.migrate(dbPath));

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement statement = connection.createStatement()) {
            assertColumnExists(statement, "invitations", "server_id");
            assertColumnExists(statement, "invitations", "target_server");
            assertColumnExists(statement, "users", "invite_code_id");
            assertColumnExists(statement, "users", "server_id");

            try (ResultSet result = statement.executeQuery("SELECT version FROM db_version WHERE id = 1")) {
                assertTrue(result.next());
                assertEquals(1, result.getInt("version"));
            }
            try (ResultSet result = statement.executeQuery("SELECT code FROM invitations WHERE id = 'invite-1'")) {
                assertTrue(result.next());
                assertEquals("ABC123", result.getString("code"));
            }
            try (ResultSet result = statement.executeQuery("SELECT username FROM users WHERE id = 'user-1'")) {
                assertTrue(result.next());
                assertEquals("alex", result.getString("username"));
            }
        }
    }

    private void assertColumnExists(Statement statement, String table, String expectedColumn) throws Exception {
        try (ResultSet columns = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (columns.next()) {
                if (expectedColumn.equals(columns.getString("name"))) {
                    return;
                }
            }
        }
        throw new AssertionError("Missing column " + table + "." + expectedColumn);
    }
}
