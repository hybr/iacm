package com.clubmanagement.dao;

import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.models.Club;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing Grade 11 student club assignments (many-to-many)
 */
public class Grade11ClubAssignmentDAO {

    /**
     * Assign multiple clubs to a Grade 11 student
     */
    public boolean assignClubsToStudent(int studentId, List<Club> clubs) throws SQLException {
        if (clubs.isEmpty()) {
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                // First, remove any existing assignments for this student
                clearStudentClubAssignments(conn, studentId);

                // Insert new assignments
                String insertQuery = "INSERT INTO grade11_student_clubs (student_id, club_id) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    for (Club club : clubs) {
                        pstmt.setInt(1, studentId);
                        pstmt.setInt(2, club.getId());
                        pstmt.executeUpdate();
                    }
                }

                conn.commit(); // Commit transaction
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restore auto-commit
            }
        }
    }

    /**
     * Get all clubs assigned to a Grade 11 student
     */
    public List<Club> getStudentClubs(int studentId) throws SQLException {
        String query = """
            SELECT c.id, c.name
            FROM clubs c
            JOIN grade11_student_clubs gsc ON c.id = gsc.club_id
            WHERE gsc.student_id = ?
            ORDER BY c.name
        """;

        List<Club> clubs = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Club club = new Club();
                    club.setId(rs.getInt("id"));
                    club.setName(rs.getString("name"));
                    clubs.add(club);
                }
            }
        }

        return clubs;
    }

    /**
     * Check if a Grade 11 student has any club assignments
     */
    public boolean hasClubAssignments(int studentId) throws SQLException {
        String query = "SELECT COUNT(*) FROM grade11_student_clubs WHERE student_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Get all Grade 11 students and their club assignments
     */
    public List<StudentClubAssignment> getAllGrade11Assignments() throws SQLException {
        String query = """
            SELECT u.id as student_id, u.username, u.full_name,
                   c.id as club_id, c.name as club_name,
                   gsc.assigned_at
            FROM users u
            LEFT JOIN grade11_student_clubs gsc ON u.id = gsc.student_id
            LEFT JOIN clubs c ON gsc.club_id = c.id
            WHERE u.role = 'GRADE_11'
            ORDER BY u.full_name, c.name
        """;

        List<StudentClubAssignment> assignments = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                StudentClubAssignment assignment = new StudentClubAssignment();
                assignment.setStudentId(rs.getInt("student_id"));
                assignment.setStudentUsername(rs.getString("username"));
                assignment.setStudentFullName(rs.getString("full_name"));
                assignment.setClubId(rs.getInt("club_id"));
                assignment.setClubName(rs.getString("club_name"));

                String assignedAtStr = rs.getString("assigned_at");
                if (assignedAtStr != null) {
                    assignment.setAssignedAt(java.time.LocalDateTime.parse(assignedAtStr));
                }

                assignments.add(assignment);
            }
        }

        return assignments;
    }

    /**
     * Remove a specific club assignment for a student
     */
    public boolean removeClubAssignment(int studentId, int clubId) throws SQLException {
        String query = "DELETE FROM grade11_student_clubs WHERE student_id = ? AND club_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, clubId);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Clear all club assignments for a student (used during reassignment)
     */
    private void clearStudentClubAssignments(Connection conn, int studentId) throws SQLException {
        String query = "DELETE FROM grade11_student_clubs WHERE student_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Get students assigned to a specific club
     */
    public List<Integer> getStudentsInClub(int clubId) throws SQLException {
        String query = "SELECT student_id FROM grade11_student_clubs WHERE club_id = ?";
        List<Integer> studentIds = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, clubId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    studentIds.add(rs.getInt("student_id"));
                }
            }
        }

        return studentIds;
    }

    /**
     * Get club assignment statistics
     */
    public ClubAssignmentStats getAssignmentStats() throws SQLException {
        ClubAssignmentStats stats = new ClubAssignmentStats();

        // Total Grade 11 students
        String totalStudentsQuery = "SELECT COUNT(*) FROM users WHERE role = 'GRADE_11'";

        // Students with club assignments
        String assignedStudentsQuery = """
            SELECT COUNT(DISTINCT student_id)
            FROM grade11_student_clubs gsc
            JOIN users u ON gsc.student_id = u.id
            WHERE u.role = 'GRADE_11'
        """;

        // Average clubs per student
        String avgClubsQuery = """
            SELECT AVG(club_count) as avg_clubs
            FROM (
                SELECT COUNT(*) as club_count
                FROM grade11_student_clubs gsc
                JOIN users u ON gsc.student_id = u.id
                WHERE u.role = 'GRADE_11'
                GROUP BY student_id
            )
        """;

        try (Connection conn = DatabaseManager.getConnection()) {
            // Get total students
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(totalStudentsQuery)) {
                if (rs.next()) {
                    stats.setTotalStudents(rs.getInt(1));
                }
            }

            // Get assigned students
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(assignedStudentsQuery)) {
                if (rs.next()) {
                    stats.setAssignedStudents(rs.getInt(1));
                }
            }

            // Get average clubs
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(avgClubsQuery)) {
                if (rs.next()) {
                    stats.setAverageClubsPerStudent(rs.getDouble("avg_clubs"));
                }
            }
        }

        return stats;
    }

    /**
     * Inner class for student club assignment data
     */
    public static class StudentClubAssignment {
        private int studentId;
        private String studentUsername;
        private String studentFullName;
        private int clubId;
        private String clubName;
        private java.time.LocalDateTime assignedAt;

        // Getters and setters
        public int getStudentId() { return studentId; }
        public void setStudentId(int studentId) { this.studentId = studentId; }

        public String getStudentUsername() { return studentUsername; }
        public void setStudentUsername(String studentUsername) { this.studentUsername = studentUsername; }

        public String getStudentFullName() { return studentFullName; }
        public void setStudentFullName(String studentFullName) { this.studentFullName = studentFullName; }

        public int getClubId() { return clubId; }
        public void setClubId(int clubId) { this.clubId = clubId; }

        public String getClubName() { return clubName; }
        public void setClubName(String clubName) { this.clubName = clubName; }

        public java.time.LocalDateTime getAssignedAt() { return assignedAt; }
        public void setAssignedAt(java.time.LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    }

    /**
     * Inner class for club assignment statistics
     */
    public static class ClubAssignmentStats {
        private int totalStudents;
        private int assignedStudents;
        private double averageClubsPerStudent;

        // Getters and setters
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

        public int getAssignedStudents() { return assignedStudents; }
        public void setAssignedStudents(int assignedStudents) { this.assignedStudents = assignedStudents; }

        public double getAverageClubsPerStudent() { return averageClubsPerStudent; }
        public void setAverageClubsPerStudent(double averageClubsPerStudent) { this.averageClubsPerStudent = averageClubsPerStudent; }

        public int getUnassignedStudents() {
            return totalStudents - assignedStudents;
        }
    }
}