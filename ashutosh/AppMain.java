import storage.*;
import java.util.*;

public class AppMain {
    public static void main(String[] args) throws Exception {
        DatabaseManager.initDatabase();
        int userId = UserManager.registerUser("alice", "models/alice.model");
        EmotionLogManager.saveEmotionLog(userId, "happy", 0.92, null);
        EmotionLogManager.saveEmotionLog(userId, "sad", 0.85, "neutral");
        List<Map<String, Object>> logs = EmotionLogManager.loadEmotionLogs(userId);
        EmotionLogManager.exportLogsToJson(logs, "user_data/alice_logs.json");
        // ModelManager.saveModel(...);
        // ModelManager.loadModel(...);
    }
}