package storage;

import java.sql.*;

public class UserManager extends DatabaseManager {

    public static int registerUser(String username, String modelPath) throws SQLException {
        String sql = "INSERT INTO users(username, model_path) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, modelPath);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }
}