package com.clubmanagement.dao;

import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.models.Attendance;
import com.clubmanagement.models.Attendance.AttendanceStatus;
import com.clubmanagement.models.AttendanceSession;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAO {

    // ============ ATTENDANCE RECORDS ============

    /**
     * Mark attendance for a student
     */
    public boolean markAttendance(Attendance attendance) throws SQLException {
        String query = """
            INSERT OR REPLACE INTO attendance
            (student_id, club_id, marked_by_id, session_date, session_time, status, notes, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getClubId());
            pstmt.setInt(3, attendance.getMarkedById());
            pstmt.setString(4, attendance.getSessionDate().toString());
            pstmt.setString(5, attendance.getSessionTime().toString());
            pstmt.setString(6, attendance.getStatus().name());
            pstmt.setString(7, attendance.getNotes());
            pstmt.setString(8, LocalDateTime.now().toString());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Bulk mark attendance for multiple students
     */
    public boolean markBulkAttendance(List<Attendance> attendanceList) throws SQLException {
        String query = """
            INSERT OR REPLACE INTO attendance
            (student_id, club_id, marked_by_id, session_date, session_time, status, notes, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false);

            for (Attendance attendance : attendanceList) {
                pstmt.setInt(1, attendance.getStudentId());
                pstmt.setInt(2, attendance.getClubId());
                pstmt.setInt(3, attendance.getMarkedById());
                pstmt.setString(4, attendance.getSessionDate().toString());
                pstmt.setString(5, attendance.getSessionTime().toString());
                pstmt.setString(6, attendance.getStatus().name());
                pstmt.setString(7, attendance.getNotes());
                pstmt.setString(8, LocalDateTime.now().toString());
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

            // Check if all records were processed
            for (int result : results) {
                if (result <= 0) return false;
            }
            return true;
        }
    }

    /**
     * Get attendance records for a specific club and date
     */
    public List<Attendance> getAttendanceByClubAndDate(int clubId, LocalDate sessionDate) throws SQLException {
        String query = """
            SELECT a.*, u.full_name as student_name, u2.full_name as marker_name, c.name as club_name
            FROM attendance a
            JOIN users u ON a.student_id = u.id
            JOIN users u2 ON a.marked_by_id = u2.id
            JOIN clubs c ON a.club_id = c.id
            WHERE a.club_id = ? AND a.session_date = ?
            ORDER BY u.full_name
        """;

        List<Attendance> attendanceList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, clubId);
            pstmt.setString(2, sessionDate.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(mapResultSetToAttendance(rs));
                }
            }
        }

        return attendanceList;
    }

    /**
     * Get attendance history for a specific student
     */
    public List<Attendance> getAttendanceByStudent(int studentId) throws SQLException {
        String query = """
            SELECT a.*, u.full_name as student_name, u2.full_name as marker_name, c.name as club_name
            FROM attendance a
            JOIN users u ON a.student_id = u.id
            JOIN users u2 ON a.marked_by_id = u2.id
            JOIN clubs c ON a.club_id = c.id
            WHERE a.student_id = ?
            ORDER BY a.session_date DESC
        """;

        List<Attendance> attendanceList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(mapResultSetToAttendance(rs));
                }
            }
        }

        return attendanceList;
    }

    /**
     * Get attendance records for a specific club with date range
     */
    public List<Attendance> getAttendanceByClubDateRange(int clubId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String query = """
            SELECT a.*, u.full_name as student_name, u2.full_name as marker_name, c.name as club_name
            FROM attendance a
            JOIN users u ON a.student_id = u.id
            JOIN users u2 ON a.marked_by_id = u2.id
            JOIN clubs c ON a.club_id = c.id
            WHERE a.club_id = ? AND a.session_date BETWEEN ? AND ?
            ORDER BY a.session_date DESC, u.full_name
        """;

        List<Attendance> attendanceList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, clubId);
            pstmt.setString(2, startDate.toString());
            pstmt.setString(3, endDate.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(mapResultSetToAttendance(rs));
                }
            }
        }

        return attendanceList;
    }

    // ============ ATTENDANCE ANALYTICS ============

    /**
     * Get attendance statistics for a club
     */
    public Map<String, Object> getClubAttendanceStats(int clubId, LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<String, Object> stats = new HashMap<>();

        // Total sessions
        String sessionCountQuery = """
            SELECT COUNT(DISTINCT session_date) as total_sessions
            FROM attendance
            WHERE club_id = ? AND session_date BETWEEN ? AND ?
        """;

        // Attendance percentage
        String attendanceQuery = """
            SELECT
                COUNT(*) as total_records,
                SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) as present_count,
                SUM(CASE WHEN status = 'LATE' THEN 1 ELSE 0 END) as late_count,
                SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END) as absent_count,
                SUM(CASE WHEN status = 'EXCUSED' THEN 1 ELSE 0 END) as excused_count
            FROM attendance
            WHERE club_id = ? AND session_date BETWEEN ? AND ?
        """;

        try (Connection conn = DatabaseManager.getConnection()) {
            // Get session count
            try (PreparedStatement pstmt = conn.prepareStatement(sessionCountQuery)) {
                pstmt.setInt(1, clubId);
                pstmt.setString(2, startDate.toString());
                pstmt.setString(3, endDate.toString());

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        stats.put("totalSessions", rs.getInt("total_sessions"));
                    }
                }
            }

            // Get attendance stats
            try (PreparedStatement pstmt = conn.prepareStatement(attendanceQuery)) {
                pstmt.setInt(1, clubId);
                pstmt.setString(2, startDate.toString());
                pstmt.setString(3, endDate.toString());

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int totalRecords = rs.getInt("total_records");
                        int presentCount = rs.getInt("present_count");
                        int lateCount = rs.getInt("late_count");
                        int absentCount = rs.getInt("absent_count");
                        int excusedCount = rs.getInt("excused_count");

                        stats.put("totalRecords", totalRecords);
                        stats.put("presentCount", presentCount);
                        stats.put("lateCount", lateCount);
                        stats.put("absentCount", absentCount);
                        stats.put("excusedCount", excusedCount);

                        // Calculate percentages
                        if (totalRecords > 0) {
                            stats.put("attendancePercentage", (double)(presentCount + lateCount) / totalRecords * 100);
                            stats.put("presentPercentage", (double)presentCount / totalRecords * 100);
                            stats.put("absentPercentage", (double)absentCount / totalRecords * 100);
                        } else {
                            stats.put("attendancePercentage", 0.0);
                            stats.put("presentPercentage", 0.0);
                            stats.put("absentPercentage", 0.0);
                        }
                    }
                }
            }
        }

        return stats;
    }

    /**
     * Get top regular attendees for a club
     */
    public List<Map<String, Object>> getTopAttendees(int clubId, LocalDate startDate, LocalDate endDate, int limit) throws SQLException {
        String query = """
            SELECT
                u.id,
                u.full_name,
                COUNT(*) as total_sessions,
                SUM(CASE WHEN a.status IN ('PRESENT', 'LATE') THEN 1 ELSE 0 END) as attended_sessions,
                ROUND(SUM(CASE WHEN a.status IN ('PRESENT', 'LATE') THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as attendance_rate
            FROM attendance a
            JOIN users u ON a.student_id = u.id
            WHERE a.club_id = ? AND a.session_date BETWEEN ? AND ?
            GROUP BY u.id, u.full_name
            ORDER BY attendance_rate DESC, attended_sessions DESC
            LIMIT ?
        """;

        List<Map<String, Object>> topAttendees = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, clubId);
            pstmt.setString(2, startDate.toString());
            pstmt.setString(3, endDate.toString());
            pstmt.setInt(4, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> attendee = new HashMap<>();
                    attendee.put("studentId", rs.getInt("id"));
                    attendee.put("studentName", rs.getString("full_name"));
                    attendee.put("totalSessions", rs.getInt("total_sessions"));
                    attendee.put("attendedSessions", rs.getInt("attended_sessions"));
                    attendee.put("attendanceRate", rs.getDouble("attendance_rate"));
                    topAttendees.add(attendee);
                }
            }
        }

        return topAttendees;
    }

    /**
     * Get students with low attendance (below threshold)
     */
    public List<Map<String, Object>> getLowAttendanceStudents(int clubId, LocalDate startDate, LocalDate endDate, double threshold) throws SQLException {
        String query = """
            SELECT
                u.id,
                u.full_name,
                u.email,
                COUNT(*) as total_sessions,
                SUM(CASE WHEN a.status IN ('PRESENT', 'LATE') THEN 1 ELSE 0 END) as attended_sessions,
                ROUND(SUM(CASE WHEN a.status IN ('PRESENT', 'LATE') THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as attendance_rate
            FROM attendance a
            JOIN users u ON a.student_id = u.id
            WHERE a.club_id = ? AND a.session_date BETWEEN ? AND ?
            GROUP BY u.id, u.full_name, u.email
            HAVING attendance_rate < ?
            ORDER BY attendance_rate ASC
        """;

        List<Map<String, Object>> lowAttendanceStudents = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, clubId);
            pstmt.setString(2, startDate.toString());
            pstmt.setString(3, endDate.toString());
            pstmt.setDouble(4, threshold);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> student = new HashMap<>();
                    student.put("studentId", rs.getInt("id"));
                    student.put("studentName", rs.getString("full_name"));
                    student.put("email", rs.getString("email"));
                    student.put("totalSessions", rs.getInt("total_sessions"));
                    student.put("attendedSessions", rs.getInt("attended_sessions"));
                    student.put("attendanceRate", rs.getDouble("attendance_rate"));
                    lowAttendanceStudents.add(student);
                }
            }
        }

        return lowAttendanceStudents;
    }

    // ============ ATTENDANCE SESSIONS ============

    /**
     * Create an attendance session
     */
    public boolean createAttendanceSession(AttendanceSession session) throws SQLException {
        String query = """
            INSERT INTO attendance_sessions
            (club_id, session_date, session_time, session_title, session_description, created_by_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, session.getClubId());
            pstmt.setString(2, session.getSessionDate().toString());
            pstmt.setString(3, session.getSessionTime().toString());
            pstmt.setString(4, session.getSessionTitle());
            pstmt.setString(5, session.getSessionDescription());
            pstmt.setInt(6, session.getCreatedById());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Get attendance sessions for a club
     */
    public List<AttendanceSession> getAttendanceSessionsByClub(int clubId) throws SQLException {
        String query = """
            SELECT s.*, c.name as club_name, u.full_name as creator_name
            FROM attendance_sessions s
            JOIN clubs c ON s.club_id = c.id
            JOIN users u ON s.created_by_id = u.id
            WHERE s.club_id = ? AND s.is_active = 1
            ORDER BY s.session_date DESC
        """;

        List<AttendanceSession> sessions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, clubId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToAttendanceSession(rs));
                }
            }
        }

        return sessions;
    }

    // ============ HELPER METHODS ============

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setId(rs.getInt("id"));
        attendance.setStudentId(rs.getInt("student_id"));
        attendance.setClubId(rs.getInt("club_id"));
        attendance.setMarkedById(rs.getInt("marked_by_id"));
        attendance.setSessionDate(LocalDate.parse(rs.getString("session_date")));
        attendance.setSessionTime(LocalTime.parse(rs.getString("session_time")));
        attendance.setStatus(AttendanceStatus.valueOf(rs.getString("status")));
        attendance.setNotes(rs.getString("notes"));

        String createdAtStr = rs.getString("created_at");
        if (createdAtStr != null) {
            attendance.setCreatedAt(LocalDateTime.parse(createdAtStr));
        }

        String updatedAtStr = rs.getString("updated_at");
        if (updatedAtStr != null) {
            attendance.setUpdatedAt(LocalDateTime.parse(updatedAtStr));
        }

        // Set display names if available
        attendance.setStudentName(rs.getString("student_name"));
        attendance.setMarkerName(rs.getString("marker_name"));
        attendance.setClubName(rs.getString("club_name"));

        return attendance;
    }

    private AttendanceSession mapResultSetToAttendanceSession(ResultSet rs) throws SQLException {
        AttendanceSession session = new AttendanceSession();
        session.setId(rs.getInt("id"));
        session.setClubId(rs.getInt("club_id"));
        session.setSessionDate(LocalDate.parse(rs.getString("session_date")));
        session.setSessionTime(LocalTime.parse(rs.getString("session_time")));
        session.setSessionTitle(rs.getString("session_title"));
        session.setSessionDescription(rs.getString("session_description"));
        session.setCreatedById(rs.getInt("created_by_id"));

        String createdAtStr = rs.getString("created_at");
        if (createdAtStr != null) {
            session.setCreatedAt(LocalDateTime.parse(createdAtStr));
        }

        session.setActive(rs.getBoolean("is_active"));
        session.setClubName(rs.getString("club_name"));
        session.setCreatorName(rs.getString("creator_name"));

        return session;
    }

    // ============ GRADE 9 SELF-ATTENDANCE METHODS ============

    /**
     * Insert attendance record for Grade 9 self-attendance
     */
    public boolean insertAttendance(Attendance attendance) throws SQLException {
        String query = """
            INSERT INTO attendance
            (student_id, club_id, marked_by_id, session_date, session_time, status, notes, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getClubId());
            pstmt.setInt(3, attendance.getMarkedById());
            pstmt.setString(4, attendance.getSessionDate().toString());
            pstmt.setString(5, attendance.getSessionTime() != null ? attendance.getSessionTime().toString() : "14:30:00");
            pstmt.setString(6, attendance.getStatus() != null ? attendance.getStatus().name() : "ABSENT");
            pstmt.setString(7, attendance.getNotes());
            pstmt.setString(8, attendance.getCreatedAt() != null ? attendance.getCreatedAt().toString() : LocalDateTime.now().toString());
            pstmt.setString(9, LocalDateTime.now().toString());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Get all attendance records for a specific student
     */
    public List<Attendance> getAttendanceByStudentId(int studentId) throws SQLException {
        String query = """
            SELECT a.*, c.name as club_name, u1.full_name as student_name, u2.full_name as marker_name
            FROM attendance a
            LEFT JOIN clubs c ON a.club_id = c.id
            LEFT JOIN users u1 ON a.student_id = u1.id
            LEFT JOIN users u2 ON a.marked_by_id = u2.id
            WHERE a.student_id = ?
            ORDER BY a.session_date DESC
        """;

        List<Attendance> attendanceList = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(mapResultSetToSimpleAttendance(rs));
                }
            }
        }

        return attendanceList;
    }

    /**
     * Check if student has attendance record for a specific date
     */
    public boolean hasAttendanceForDate(int studentId, LocalDate date) throws SQLException {
        String query = "SELECT COUNT(*) FROM attendance WHERE student_id = ? AND session_date = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.setString(2, date.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Simplified attendance mapping for Grade 9 interface
     */
    private Attendance mapResultSetToSimpleAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setId(rs.getInt("id"));
        attendance.setStudentId(rs.getInt("student_id"));
        attendance.setClubId(rs.getInt("club_id"));
        attendance.setMarkedById(rs.getInt("marked_by_id"));
        attendance.setSessionDate(LocalDate.parse(rs.getString("session_date")));

        String timeStr = rs.getString("session_time");
        if (timeStr != null) {
            attendance.setSessionTime(LocalTime.parse(timeStr));
        }

        attendance.setStatus(rs.getString("status"));
        attendance.setNotes(rs.getString("notes"));

        String createdAtStr = rs.getString("created_at");
        if (createdAtStr != null) {
            attendance.setCreatedAt(LocalDateTime.parse(createdAtStr));
        }

        String updatedAtStr = rs.getString("updated_at");
        if (updatedAtStr != null) {
            attendance.setUpdatedAt(LocalDateTime.parse(updatedAtStr));
        }

        // Set display names if available
        attendance.setStudentName(rs.getString("student_name"));
        attendance.setMarkerName(rs.getString("marker_name"));
        attendance.setClubName(rs.getString("club_name"));

        return attendance;
    }

    /**
     * Get student attendance for a specific date
     */
    public Attendance getStudentAttendanceForDate(int studentId, int clubId, LocalDate sessionDate) throws SQLException {
        String query = """
            SELECT a.*, u.full_name as student_name, m.full_name as marker_name, c.name as club_name
            FROM attendance a
            LEFT JOIN users u ON a.student_id = u.id
            LEFT JOIN users m ON a.marked_by_id = m.id
            LEFT JOIN clubs c ON a.club_id = c.id
            WHERE a.student_id = ? AND a.club_id = ? AND a.session_date = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, clubId);
            pstmt.setString(3, sessionDate.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAttendance(rs);
                }
            }
        }
        return null;
    }
}