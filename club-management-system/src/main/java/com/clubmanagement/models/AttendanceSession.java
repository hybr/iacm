package com.clubmanagement.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttendanceSession {
    private int id;
    private int clubId;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private String sessionTitle;
    private String sessionDescription;
    private int createdById;
    private LocalDateTime createdAt;
    private boolean isActive;

    // For display purposes
    private String clubName;
    private String creatorName;

    // Default constructor
    public AttendanceSession() {
        this.sessionTime = LocalTime.of(14, 30); // Default 2:30 PM
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Full constructor
    public AttendanceSession(int id, int clubId, LocalDate sessionDate, LocalTime sessionTime,
                           String sessionTitle, String sessionDescription, int createdById,
                           LocalDateTime createdAt, boolean isActive) {
        this.id = id;
        this.clubId = clubId;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.sessionTitle = sessionTitle;
        this.sessionDescription = sessionDescription;
        this.createdById = createdById;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    // Constructor for new session
    public AttendanceSession(int clubId, LocalDate sessionDate, String sessionTitle,
                           String sessionDescription, int createdById) {
        this();
        this.clubId = clubId;
        this.sessionDate = sessionDate;
        this.sessionTitle = sessionTitle;
        this.sessionDescription = sessionDescription;
        this.createdById = createdById;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getSessionTime() { return sessionTime; }
    public void setSessionTime(LocalTime sessionTime) { this.sessionTime = sessionTime; }

    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }

    public String getSessionDescription() { return sessionDescription; }
    public void setSessionDescription(String sessionDescription) { this.sessionDescription = sessionDescription; }

    public int getCreatedById() { return createdById; }
    public void setCreatedById(int createdById) { this.createdById = createdById; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)",
            sessionTitle != null ? sessionTitle : "Club Session",
            sessionDate,
            clubName != null ? clubName : "Club " + clubId);
    }
}