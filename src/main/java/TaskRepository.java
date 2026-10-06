import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    public enum Category {
        URGENT_IMPORTANT("Do First"),
        NOT_URGENT_IMPORTANT("Schedule"),
        URGENT_NOT_IMPORTANT("Delegate"),
        NOT_URGENT_NOT_IMPORTANT("Eliminate");

        private final String label;
        Category(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public static class Task {
        public String id; // Add the ID field
        public String name;
        public Category category;

        // Constructor for NEW tasks (generates an ID)
        public Task(String name, Category category) {
            this.id = java.util.UUID.randomUUID().toString();
            this.name = name;
            this.category = category;
        }

        // Constructor for LOADED tasks from Supabase
        public Task(String id, String name, Category category) {
            this.id = id;
            this.name = name;
            this.category = category;
        }

        // The JList uses this to know what text to display
        @Override
        public String toString() { return name; }
    }

    // 1. UPDATE saveTask to include the ID
    public static void saveTask(String userId, Task task) {
        String sql = "INSERT INTO pomodoro.tasks (id, user_identifier, task_name, category) VALUES (?::uuid, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.id);
            pstmt.setString(2, userId);
            pstmt.setString(3, task.name);
            pstmt.setString(4, task.category.name());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 2. UPDATE loadTasks to fetch the ID
    public static List<Task> loadTasks(String userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, task_name, category FROM pomodoro.tasks WHERE user_identifier = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(new Task(
                        rs.getString("id"), // Fetch the UUID
                        rs.getString("task_name"),
                        Category.valueOf(rs.getString("category"))
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tasks;
    }

    // 3. ADD the deleteTask method
    public static void deleteTask(String taskId) {
        String sql = "DELETE FROM pomodoro.tasks WHERE id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}