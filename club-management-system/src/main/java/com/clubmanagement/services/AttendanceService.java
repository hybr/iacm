package com.clubmanagement.services;

import com.clubmanagement.dao.AttendanceDAO;
import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.models.Attendance;
import com.clubmanagement.models.Attendance.AttendanceStatus;
import com.clubmanagement.models.AttendanceSession;
import com.clubmanagement.models.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttendanceService {
    private AttendanceDAO attendanceDAO;
    private UserDAO userDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
        this.userDAO = new UserDAO();
    }

    // ============ ATTENDANCE MARKING ============

    /**
     * Mark attendance for a single student
     */
    public boolean markStudentAttendance(int studentId, int clubId, int markedById,
                                       LocalDate sessionDate, AttendanceStatus status, String notes) throws SQLException {
        Attendance attendance = new Attendance(studentId, clubId, markedById, sessionDate, status, notes);
        return attendanceDAO.markAttendance(attendance);
    }

    /**
     * Bulk mark attendance for multiple students with the same status
     */
    public boolean markBulkAttendance(List<Integer> studentIds, int clubId, int markedById,
                                    LocalDate sessionDate, AttendanceStatus status) throws SQLException {
        List<Attendance> attendanceList = new ArrayList<>();

        for (int studentId : studentIds) {
            Attendance attendance = new Attendance(studentId, clubId, markedById, sessionDate, status, null);
            attendanceList.add(attendance);
        }

        return attendanceDAO.markBulkAttendance(attendanceList);
    }

    /**
     * Mark all students in a club as present (bulk present marking)
     */
    public boolean markAllPresent(int clubId, int markedById, LocalDate sessionDate) throws SQLException {
        List<User> clubMembers = getClubMembers(clubId);
        List<Integer> studentIds = clubMembers.stream()
                                             .mapToInt(User::getId)
                                             .boxed()
                                             .toList();

        return markBulkAttendance(studentIds, clubId, markedById, sessionDate, AttendanceStatus.PRESENT);
    }

    /**
     * Get all Grade 9 students assigned to a specific club
     */
    public List<User> getClubMembers(int clubId) throws SQLException {
        List<User> allGrade9Students = userDAO.getUsersByRole(User.UserRole.GRADE_9);
        return allGrade9Students.stream()
                               .filter(user -> user.getAssignedClubId() != null && user.getAssignedClubId() == clubId)
                               .toList();
    }

    // ============ ATTENDANCE RETRIEVAL ============

    /**
     * Get attendance for a specific club and date
     */
    public List<Attendance> getClubAttendance(int clubId, LocalDate sessionDate) throws SQLException {
        return attendanceDAO.getAttendanceByClubAndDate(clubId, sessionDate);
    }

    /**
     * Get attendance history for a student
     */
    public List<Attendance> getStudentAttendanceHistory(int studentId) throws SQLException {
        return attendanceDAO.getAttendanceByStudent(studentId);
    }

    /**
     * Get attendance records for a club within a date range
     */
    public List<Attendance> getClubAttendanceRange(int clubId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return attendanceDAO.getAttendanceByClubDateRange(clubId, startDate, endDate);
    }

    /**
     * Get attendance for today's session
     */
    public List<Attendance> getTodayAttendance(int clubId) throws SQLException {
        return getClubAttendance(clubId, LocalDate.now());
    }

    // ============ ATTENDANCE ANALYTICS ============

    /**
     * Get comprehensive attendance statistics for a club
     */
    public Map<String, Object> getClubAttendanceStatistics(int clubId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return attendanceDAO.getClubAttendanceStats(clubId, startDate, endDate);
    }

    /**
     * Get top attendees for a club
     */
    public List<Map<String, Object>> getTopAttendees(int clubId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return attendanceDAO.getTopAttendees(clubId, startDate, endDate, 3); // Top 3
    }

    /**
     * Get students with low attendance (below 75%)
     */
    public List<Map<String, Object>> getLowAttendanceStudents(int clubId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return attendanceDAO.getLowAttendanceStudents(clubId, startDate, endDate, 75.0);
    }

    /**
     * Get attendance statistics for the current month
     */
    public Map<String, Object> getCurrentMonthStats(int clubId) throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        return getClubAttendanceStatistics(clubId, startOfMonth, endOfMonth);
    }

    /**
     * Get formatted attendance summary for display
     */
    public String getAttendanceSummaryText(int clubId) throws SQLException {
        Map<String, Object> stats = getCurrentMonthStats(clubId);

        Integer totalSessions = (Integer) stats.get("totalSessions");
        Double attendancePercentage = (Double) stats.get("attendancePercentage");

        if (totalSessions == null || totalSessions == 0) {
            return "No attendance data available for this month.";
        }

        return String.format(
            "Sessions This Month: %d | Overall Attendance: %.1f%%",
            totalSessions,
            attendancePercentage != null ? attendancePercentage : 0.0
        );
    }

    // ============ ATTENDANCE SESSIONS ============

    /**
     * Create a new attendance session
     */
    public boolean createAttendanceSession(int clubId, LocalDate sessionDate, String sessionTitle,
                                         String sessionDescription, int createdById) throws SQLException {
        AttendanceSession session = new AttendanceSession(clubId, sessionDate, sessionTitle, sessionDescription, createdById);
        return attendanceDAO.createAttendanceSession(session);
    }

    /**
     * Get attendance sessions for a club
     */
    public List<AttendanceSession> getClubSessions(int clubId) throws SQLException {
        return attendanceDAO.getAttendanceSessionsByClub(clubId);
    }

    // ============ PERMISSION CHECKS ============

    /**
     * Check if a user can mark attendance for a specific club
     */
    public boolean canMarkAttendance(User user, int clubId) {
        // Only Grade 11 students can mark attendance
        if (user.getRole() != User.UserRole.GRADE_11) {
            return false;
        }

        // Check if the Grade 11 student is assigned to this club (as a mentor)
        return user.getAssignedClubId() != null && user.getAssignedClubId() == clubId;
    }

    /**
     * Check if a user can view attendance for a specific club
     */
    public boolean canViewAttendance(User user, int clubId) {
        // Club managers can view all clubs
        if (user.getRole() == User.UserRole.CLUB_MANAGER) {
            return true;
        }

        // Grade 11 students can view their assigned club
        if (user.getRole() == User.UserRole.GRADE_11) {
            return user.getAssignedClubId() != null && user.getAssignedClubId() == clubId;
        }

        // Grade 9 students can only view their own attendance (handled separately)
        return false;
    }

    /**
     * Check if a Grade 9 student can view their own attendance
     */
    public boolean canViewOwnAttendance(User user, int studentId) {
        return user.getRole() == User.UserRole.GRADE_9 && user.getId() == studentId;
    }

    // ============ UTILITY METHODS ============

    /**
     * Generate CSV export data for attendance
     */
    public String generateAttendanceCSV(int clubId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Attendance> attendanceList = getClubAttendanceRange(clubId, startDate, endDate);

        StringBuilder csv = new StringBuilder();
        csv.append("Date,Student Name,Status,Notes,Marked By\n");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Attendance attendance : attendanceList) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                attendance.getSessionDate().format(dateFormatter),
                attendance.getStudentName() != null ? attendance.getStudentName() : "",
                attendance.getStatus().getDisplayName(),
                attendance.getNotes() != null ? attendance.getNotes() : "",
                attendance.getMarkerName() != null ? attendance.getMarkerName() : ""
            ));
        }

        return csv.toString();
    }

    /**
     * Get attendance summary for a specific student
     */
    public Map<String, Object> getStudentAttendanceSummary(int studentId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Attendance> attendanceList = attendanceDAO.getAttendanceByStudent(studentId);

        // Filter by date range
        List<Attendance> filteredAttendance = attendanceList.stream()
            .filter(a -> !a.getSessionDate().isBefore(startDate) && !a.getSessionDate().isAfter(endDate))
            .toList();

        int totalSessions = filteredAttendance.size();
        long presentCount = filteredAttendance.stream()
            .mapToLong(a -> (a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE) ? 1 : 0)
            .sum();

        double attendanceRate = totalSessions > 0 ? (double) presentCount / totalSessions * 100 : 0.0;

        return Map.of(
            "totalSessions", totalSessions,
            "attendedSessions", (int) presentCount,
            "attendanceRate", attendanceRate,
            "attendanceList", filteredAttendance
        );
    }

    /**
     * Validate attendance data before saving
     */
    public String validateAttendance(int studentId, int clubId, LocalDate sessionDate) throws SQLException {
        // Check if student exists and is Grade 9
        User student = userDAO.getUserById(studentId);
        if (student == null) {
            return "Student not found.";
        }

        if (student.getRole() != User.UserRole.GRADE_9) {
            return "Attendance can only be marked for Grade 9 students.";
        }

        // Check if student is assigned to the club
        if (student.getAssignedClubId() == null || student.getAssignedClubId() != clubId) {
            return "Student is not assigned to this club.";
        }

        // Check if date is not in the future
        if (sessionDate.isAfter(LocalDate.now())) {
            return "Cannot mark attendance for future dates.";
        }

        return null; // No validation errors
    }
}