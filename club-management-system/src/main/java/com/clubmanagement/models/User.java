package com.clubmanagement.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String passwordSalt;
    private String email;
    private String fullName;
    private String securityQuestion;
    private String securityAnswer;
    private UserRole role;
    private Integer assignedClubId;
    private boolean firstLoginCompleted;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime lastLogin;
    private boolean isActive;

    public enum UserRole {
        CLUB_MANAGER,
        GRADE_11,
        GRADE_9
    }

    public User() {}

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(int id, String username, String password, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String email, String fullName,
                String securityQuestion, String securityAnswer, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.role = role;
        this.firstLoginCompleted = false;
        this.isActive = true;
        this.createdAt = java.time.LocalDateTime.now();
    }

    public User(int id, String username, String password, String email, String fullName,
                String securityQuestion, String securityAnswer, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.role = role;
        this.firstLoginCompleted = false;
        this.isActive = true;
    }

    // Full constructor with all fields
    public User(int id, String username, String password, String passwordSalt, String email, String fullName,
                String securityQuestion, String securityAnswer, UserRole role, Integer assignedClubId,
                boolean firstLoginCompleted, java.time.LocalDateTime createdAt, java.time.LocalDateTime lastLogin, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.passwordSalt = passwordSalt;
        this.email = email;
        this.fullName = fullName;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.role = role;
        this.assignedClubId = assignedClubId;
        this.firstLoginCompleted = firstLoginCompleted;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public Integer getAssignedClubId() {
        return assignedClubId;
    }

    public void setAssignedClubId(Integer assignedClubId) {
        this.assignedClubId = assignedClubId;
    }

    public boolean isFirstLoginCompleted() {
        return firstLoginCompleted;
    }

    public void setFirstLoginCompleted(boolean firstLoginCompleted) {
        this.firstLoginCompleted = firstLoginCompleted;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(java.time.LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", assignedClubId=" + assignedClubId +
                ", firstLoginCompleted=" + firstLoginCompleted +
                ", isActive=" + isActive +
                '}';
    }
}