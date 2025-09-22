package com.clubmanagement.services;

import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.models.User;
import com.clubmanagement.security.PasswordHasher;
import com.clubmanagement.security.TokenManager;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class AuthenticationService {
    private UserDAO userDAO;
    private User currentUser;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public boolean login(String username, String password) throws SQLException {
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            // Update last login time
            userDAO.updateLastLogin(user.getId());

            this.currentUser = user;
            return true;
        }
        return false;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(User.UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    public boolean isClubManager() {
        return hasRole(User.UserRole.CLUB_MANAGER);
    }

    public boolean isGrade11() {
        return hasRole(User.UserRole.GRADE_11);
    }

    public boolean isGrade9() {
        return hasRole(User.UserRole.GRADE_9);
    }

    public boolean registerUser(String username, String password, String email, String fullName,
                               String securityQuestion, String securityAnswer, User.UserRole role) throws SQLException {
        if (userDAO.usernameExists(username)) {
            throw new SQLException("Username already exists");
        }

        if (userDAO.emailExists(email)) {
            throw new SQLException("Email already exists");
        }

        User newUser = new User(username, password, email, fullName, securityQuestion, securityAnswer, role);
        return userDAO.insertUser(newUser);
    }

    public boolean changePassword(String currentPassword, String newPassword) throws SQLException {
        if (currentUser == null) {
            return false;
        }

        if (!currentUser.getPassword().equals(currentPassword)) {
            return false;
        }

        boolean updated = userDAO.updatePassword(currentUser.getUsername(), newPassword);
        if (updated) {
            currentUser.setPassword(newPassword);
        }
        return updated;
    }

    public boolean resetPassword(String username, String securityAnswer, String newPassword) throws SQLException {
        if (!userDAO.validateSecurityAnswer(username, securityAnswer)) {
            return false;
        }

        return userDAO.updatePassword(username, newPassword);
    }

    public User getUserByUsername(String username) throws SQLException {
        return userDAO.getUserByUsername(username);
    }

    public User getUserByEmail(String email) throws SQLException {
        return userDAO.getUserByEmail(email);
    }

    public boolean validateCredentials(String username, String password) throws SQLException {
        User user = userDAO.authenticate(username, password);
        return user != null;
    }

    public String validateRegistrationData(String username, String password, String confirmPassword,
                                         String email, String fullName, String securityQuestion, String securityAnswer) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required";
        }

        if (username.trim().length() < 3) {
            return "Username must be at least 3 characters long";
        }

        if (password == null || password.isEmpty()) {
            return "Password is required";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }

        if (!isValidEmail(email)) {
            return "Invalid email format";
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name is required";
        }

        if (securityQuestion == null || securityQuestion.trim().isEmpty()) {
            return "Security question is required";
        }

        if (securityAnswer == null || securityAnswer.trim().isEmpty()) {
            return "Security answer is required";
        }

        return null;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    /**
     * Request password reset - generates token and sends email
     */
    public boolean requestPasswordReset(String usernameOrEmail) throws SQLException {
        User user = null;

        // Try to find user by username first, then by email
        if (usernameOrEmail.contains("@")) {
            user = userDAO.getUserByEmail(usernameOrEmail);
        } else {
            user = userDAO.getUserByUsername(usernameOrEmail);
        }

        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            return false; // User not found or no email on file
        }

        // Generate secure token
        String token = TokenManager.generatePasswordResetToken(
            String.valueOf(user.getId()),
            user.getEmail()
        );

        // Store token in database
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(TokenManager.getTokenExpiryMinutes());
        userDAO.storePasswordResetToken(token, user.getId(), user.getEmail(), expiresAt);

        // Send reset email
        return EmailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), token);
    }

    /**
     * Reset password using valid token
     */
    public boolean resetPasswordWithToken(String token, String newPassword) throws SQLException {
        // Validate token
        TokenManager.TokenInfo tokenInfo = TokenManager.validatePasswordResetToken(token);
        if (tokenInfo == null) {
            return false; // Invalid or expired token
        }

        // Get user by token
        User user = userDAO.getUserByResetToken(token);
        if (user == null) {
            return false;
        }

        // Hash new password
        PasswordHasher.HashedPassword hashedPassword = PasswordHasher.hashPassword(newPassword);

        // Update password in database
        boolean updated = userDAO.updatePasswordWithSalt(user.getId(), hashedPassword.getHash(), hashedPassword.getSalt());

        if (updated) {
            // Mark token as used
            userDAO.markTokenAsUsed(token);
            TokenManager.consumeToken(token);
            return true;
        }

        return false;
    }

    /**
     * Check if user needs to complete first login (9th graders)
     */
    public boolean needsFirstLoginCompletion() {
        return currentUser != null &&
               currentUser.getRole() == User.UserRole.GRADE_9 &&
               !currentUser.isFirstLoginCompleted();
    }

    /**
     * Check if user has an assigned club
     */
    public boolean hasAssignedClub() {
        return currentUser != null && currentUser.getAssignedClubId() != null;
    }

    /**
     * Clean up expired tokens periodically
     */
    public void cleanupExpiredTokens() {
        try {
            userDAO.cleanupExpiredTokens();
            TokenManager.cleanupExpiredTokens();
        } catch (SQLException e) {
            System.err.println("Error cleaning up expired tokens: " + e.getMessage());
        }
    }

    /**
     * Check if Grade 11 student needs club selection (first login with no club assignments)
     */
    public boolean needsClubSelection() {
        if (!isGrade11()) {
            return false;
        }

        try {
            com.clubmanagement.dao.Grade11ClubAssignmentDAO assignmentDAO =
                new com.clubmanagement.dao.Grade11ClubAssignmentDAO();
            return !assignmentDAO.hasClubAssignments(currentUser.getId());
        } catch (SQLException e) {
            System.err.println("Error checking club assignments: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mark first login as completed for the current user
     */
    public void markFirstLoginCompleted(int userId) {
        try {
            userDAO.markFirstLoginCompleted(userId);
            // Update current user object if it's the same user
            if (currentUser != null && currentUser.getId() == userId) {
                currentUser.setFirstLoginCompleted(true);
            }
        } catch (SQLException e) {
            System.err.println("Error marking first login completed: " + e.getMessage());
        }
    }

    /**
     * Check if current user has completed first login setup
     */
    public boolean hasCompletedFirstLogin() {
        return currentUser != null && currentUser.isFirstLoginCompleted();
    }
}