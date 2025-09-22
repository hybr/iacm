package com.clubmanagement.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Attendance {
    public enum AttendanceStatus {
        PRESENT("Present", "✅"),
        ABSENT("Absent", "❌"),
        LATE("Late", "⏱️"),
        EXCUSED("Excused", "📝");

        private final String displayName;
        private final String emoji;

        AttendanceStatus(String displayName, String emoji) {
            this.displayName = displayName;
            this.emoji = emoji;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getEmoji() {
            return emoji;
        }

        @Override
        public String toString() {
            return emoji + " " + displayName;
        }
    }

    private int id;
    private int studentId;
    private int clubId;
    private int markedById;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private AttendanceStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Student and marker names (for display purposes)
    private String studentName;
    private String markerName;
    private String clubName;

    // Default constructor
    public Attendance() {
        this.sessionTime = LocalTime.of(14, 30); // Default 2:30 PM
        this.status = AttendanceStatus.PRESENT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Full constructor
    public Attendance(int id, int studentId, int clubId, int markedById, LocalDate sessionDate,
                     LocalTime sessionTime, AttendanceStatus status, String notes,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.clubId = clubId;
        this.markedById = markedById;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for new attendance record
    public Attendance(int studentId, int clubId, int markedById, LocalDate sessionDate,
                     AttendanceStatus status, String notes) {
        this();
        this.studentId = studentId;
        this.clubId = clubId;
        this.markedById = markedById;
        this.sessionDate = sessionDate;
        this.status = status;
        this.notes = notes;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public int getMarkedById() { return markedById; }
    public void setMarkedById(int markedById) { this.markedById = markedById; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getSessionTime() { return sessionTime; }
    public void setSessionTime(LocalTime sessionTime) { this.sessionTime = sessionTime; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    // Convenience method for setting status from string
    public void setStatus(String statusStr) {
        if (statusStr != null) {
            try {
                this.status = AttendanceStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                // Fallback for simple string values
                switch (statusStr.toUpperCase()) {
                    case "PRESENT":
                        this.status = AttendanceStatus.PRESENT;
                        break;
                    case "ABSENT":
                        this.status = AttendanceStatus.ABSENT;
                        break;
                    case "LATE":
                        this.status = AttendanceStatus.LATE;
                        break;
                    case "EXCUSED":
                        this.status = AttendanceStatus.EXCUSED;
                        break;
                    default:
                        this.status = AttendanceStatus.ABSENT;
                }
            }
        }
        this.updatedAt = LocalDateTime.now();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getMarkerName() { return markerName; }
    public void setMarkerName(String markerName) { this.markerName = markerName; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s) - %s",
            studentName != null ? studentName : "Student " + studentId,
            sessionDate,
            status.toString(),
            notes != null ? notes : "");
    }
}