package storage;

import java.sql.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import com.google.gson.Gson;

public class EmotionLogManager extends DatabaseManager {

    public static void saveEmotionLog(int userId, String emotion, double confidence, String corrected) throws SQLException {
        String sql = "INSERT INTO emotion_logs(user_id, emotion, confidence, corrected) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, emotion);
            pstmt.setDouble(3, confidence);
            pstmt.setString(4, corrected);
            pstmt.executeUpdate();
        }
    }

    public static List<Map<String, Object>> loadEmotionLogs(int userId) throws SQLException {
        List<Map<String, Object>> logs = new ArrayList<>();
        String sql = "SELECT * FROM emotion_logs WHERE user_id = ? ORDER BY timestamp DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("timestamp", rs.getString("timestamp"));
                row.put("emotion", rs.getString("emotion"));
                row.put("confidence", rs.getDouble("confidence"));
                row.put("corrected", rs.getString("corrected"));
                logs.add(row);
            }
        }
        return logs;
    }

    public static void exportLogsToJson(List<Map<String, Object>> logs, String filePath) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(logs);
        Files.write(Paths.get(filePath), json.getBytes());
    }
}