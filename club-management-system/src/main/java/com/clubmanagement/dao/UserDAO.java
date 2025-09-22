package com.clubmanagement.dao;

import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.models.User;
import com.clubmanagement.models.User.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    try {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            getStringOrNull(rs, "password_salt"),
                            getStringOrNull(rs, "email"),
                            getStringOrNull(rs, "full_name"),
                            getStringOrNull(rs, "security_question"),
                            getStringOrNull(rs, "security_answer"),
                            UserRole.valueOf(rs.getString("role")),
                            getIntegerOrNull(rs, "assigned_club_id"),
                            getBooleanOrDefault(rs, "first_login_completed", false),
                            getTimestampOrNull(rs, "created_at"),
                            getTimestampOrNull(rs, "last_login"),
                            getBooleanOrDefault(rs, "is_active", true)
                        );
                    } catch (SQLException e) {
                        // Fallback for older database schema
                        System.err.println("Warning: Using fallback user constructor due to: " + e.getMessage());
                        return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            UserRole.valueOf(rs.getString("role"))
                        );
                    }
                }
            }
        }
        return null;
    }

    public List<User> getUsersByRole(UserRole role) throws SQLException {
        String query = "SELECT * FROM users WHERE role = ?";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, role.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("security_question"),
                        rs.getString("security_answer"),
                        UserRole.valueOf(rs.getString("role"))
                    ));
                }
            }
        }
        return users;
    }

    public User getUserById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("security_question"),
                        rs.getString("security_answer"),
                        UserRole.valueOf(rs.getString("role"))
                    );
                }
            }
        }
        return null;
    }

    public boolean insertUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password, email, full_name, security_question, security_answer, role) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getSecurityQuestion());
            pstmt.setString(6, user.getSecurityAnswer());
            pstmt.setString(7, user.getRole().name());

            return pstmt.executeUpdate() > 0;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("full_name"),
                    rs.getString("security_question"),
                    rs.getString("security_answer"),
                    UserRole.valueOf(rs.getString("role"))
                ));
            }
        }
        return users;
    }

    public User getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("security_question"),
                        rs.getString("security_answer"),
                        UserRole.valueOf(rs.getString("role"))
                    );
                }
            }
        }
        return null;
    }

    public User getUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("security_question"),
                        rs.getString("security_answer"),
                        UserRole.valueOf(rs.getString("role"))
                    );
                }
            }
        }
        return null;
    }

    public boolean updatePassword(String username, String newPassword) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean validateSecurityAnswer(String username, String securityAnswer) throws SQLException {
        String query = "SELECT security_answer FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedAnswer = rs.getString("security_answer");
                    return storedAnswer != null && storedAnswer.equalsIgnoreCase(securityAnswer.trim());
                }
            }
        }
        return false;
    }

    public boolean usernameExists(String username) throws SQLException {
        String query = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT 1 FROM users WHERE email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean updateUserClubAssignment(int userId, int clubId) throws SQLException {
        String query = "UPDATE users SET assigned_club_id = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, clubId);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateFirstLoginCompleted(int userId, boolean completed) throws SQLException {
        String query = "UPDATE users SET first_login_completed = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, completed);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateLastLogin(int userId) throws SQLException {
        String query = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateUserProfile(User user) throws SQLException {
        String query = "UPDATE users SET email = ?, full_name = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFullName());
            pstmt.setInt(3, user.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean storePasswordResetToken(String token, int userId, String email, java.time.LocalDateTime expiresAt) throws SQLException {
        String query = "INSERT INTO password_reset_tokens (token, user_id, email, expires_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, token);
            pstmt.setInt(2, userId);
            pstmt.setString(3, email);
            pstmt.setString(4, expiresAt.toString());

            return pstmt.executeUpdate() > 0;
        }
    }

    public User getUserByResetToken(String token) throws SQLException {
        String query = """
            SELECT u.* FROM users u
            JOIN password_reset_tokens t ON u.id = t.user_id
            WHERE t.token = ? AND t.used = 0 AND t.expires_at > ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, token);
            pstmt.setString(2, java.time.LocalDateTime.now().toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("password_salt"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("security_question"),
                        rs.getString("security_answer"),
                        User.UserRole.valueOf(rs.getString("role")),
                        rs.getObject("assigned_club_id", Integer.class),
                        rs.getBoolean("first_login_completed"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toLocalDateTime() : null,
                        rs.getBoolean("is_active")
                    );
                }
            }
        }
        return null;
    }

    public boolean markTokenAsUsed(String token) throws SQLException {
        String query = "UPDATE password_reset_tokens SET used = 1 WHERE token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, token);

            return pstmt.executeUpdate() > 0;
        }
    }

    public void cleanupExpiredTokens() throws SQLException {
        String query = "DELETE FROM password_reset_tokens WHERE expires_at < ? OR used = 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, java.time.LocalDateTime.now().toString());

            pstmt.executeUpdate();
        }
    }

    public boolean updatePasswordWithSalt(int userId, String hashedPassword, String salt) throws SQLException {
        String query = "UPDATE users SET password = ?, password_salt = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, salt);
            pstmt.setInt(3, userId);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Get all Grade 9 students with their club assignments
     */
    public List<User> getGrade9StudentsWithClubAssignments() throws SQLException {
        String query = """
            SELECT u.*, c.name as club_name FROM users u
            LEFT JOIN clubs c ON u.assigned_club_id = c.id
            WHERE u.role = 'GRADE_9'
            ORDER BY u.full_name, c.name
        """;
        List<User> students = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("password_salt"),
                    rs.getString("email"),
                    rs.getString("full_name"),
                    rs.getString("security_question"),
                    rs.getString("security_answer"),
                    UserRole.valueOf(rs.getString("role")),
                    getIntegerOrNull(rs, "assigned_club_id"),
                    getBooleanOrDefault(rs, "first_login_completed", false),
                    rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                    rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toLocalDateTime() : null,
                    rs.getBoolean("is_active")
                );
                students.add(user);
            }
        }
        return students;
    }

    /**
     * Helper methods for null-safe database access
     */
    private String getStringOrNull(ResultSet rs, String columnName) {
        try {
            return rs.getString(columnName);
        } catch (SQLException e) {
            return null;
        }
    }

    private boolean getBooleanOrDefault(ResultSet rs, String columnName, boolean defaultValue) {
        try {
            return rs.getBoolean(columnName);
        } catch (SQLException e) {
            return defaultValue;
        }
    }

    private Integer getIntegerOrNull(ResultSet rs, String columnName) {
        try {
            int value = rs.getInt(columnName);
            return rs.wasNull() ? null : value;
        } catch (SQLException e) {
            return null;
        }
    }

    private java.time.LocalDateTime getTimestampOrNull(ResultSet rs, String columnName) {
        try {
            Timestamp timestamp = rs.getTimestamp(columnName);
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Mark first login as completed for a user
     */
    public boolean markFirstLoginCompleted(int userId) throws SQLException {
        String query = "UPDATE users SET first_login_completed = 1 WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }
}