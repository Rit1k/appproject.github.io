package com.studyhaven;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudyHavenDAO {

    public User createUser(String username, String password, String email) throws SQLException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Hash the password in real-world applications
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    User user = new User();
                    user.setId(generatedKeys.getInt(1));
                    user.setUsername(username);
                    user.setEmail(email);
                    return user;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    public List<Task> getTasksForWorkspace(int workspaceId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE workspace_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, workspaceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setCompleted(rs.getBoolean("completed"));
                    task.setWorkspaceId(rs.getInt("workspace_id"));
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public void createTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (title, completed, workspace_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setBoolean(2, task.isCompleted());
            pstmt.setInt(3, task.getWorkspaceId());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating task failed, no ID obtained.");
                }
            }
        }
    }
}
