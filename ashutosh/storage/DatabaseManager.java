package storage;

import java.sql.*;

public class DatabaseManager {
    protected static final String DB_URL = "jdbc:sqlite:emotisense.db";

    public static void initDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT UNIQUE,"
                    + "model_path TEXT,"
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");";

            String logsTable = "CREATE TABLE IF NOT EXISTS emotion_logs ("
                    + "log_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_id INTEGER,"
                    + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "emotion TEXT,"
                    + "confidence REAL,"
                    + "corrected TEXT,"
                    + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                    + ");";

            String metadataTable = "CREATE TABLE IF NOT EXISTS training_metadata ("
                    + "user_id INTEGER PRIMARY KEY,"
                    + "last_trained DATETIME,"
                    + "samples_used INTEGER,"
                    + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                    + ");";

            stmt.execute(usersTable);
            stmt.execute(logsTable);
            stmt.execute(metadataTable);
        }
    }
}