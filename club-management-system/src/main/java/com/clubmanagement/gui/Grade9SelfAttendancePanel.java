package com.clubmanagement.gui;

import com.clubmanagement.dao.AttendanceDAO;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Attendance;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Grade9SelfAttendancePanel extends JPanel {
    private AuthenticationService authService;
    private AttendanceDAO attendanceDAO;
    private ClubDAO clubDAO;
    private JButton markPresentButton;
    private JButton markAbsentButton;
    private JLabel statusLabel;
    private JLabel clubInfoLabel;
    private JLabel attendanceCountLabel;
    private JTable attendanceHistoryTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public Grade9SelfAttendancePanel(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceDAO = new AttendanceDAO();
        this.clubDAO = new ClubDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    private void initializeComponents() {
        // Main action buttons
        markPresentButton = ModernTheme.createPrimaryButton("Mark Present ✅");
        markAbsentButton = ModernTheme.createSecondaryButton("Mark Absent ❌");
        refreshButton = ModernTheme.createSecondaryButton("Refresh");

        // Status and info labels
        statusLabel = ModernTheme.createBodyLabel("Ready to mark attendance for today");
        clubInfoLabel = ModernTheme.createHeadingLabel("Loading club information...");
        attendanceCountLabel = ModernTheme.createBodyLabel("Loading attendance statistics...");

        // Attendance history table
        String[] columnNames = {"Date", "Status", "Time Marked"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceHistoryTable = new JTable(tableModel);
        attendanceHistoryTable.setFont(ModernTheme.BODY_FONT);
        attendanceHistoryTable.setRowHeight(25);
        attendanceHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = ModernTheme.createTitleLabel("My Attendance");
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel clubInfoPanel = new JPanel(new BorderLayout());
        clubInfoPanel.setBackground(ModernTheme.WHITE);
        clubInfoPanel.add(clubInfoLabel, BorderLayout.NORTH);
        clubInfoPanel.add(attendanceCountLabel, BorderLayout.SOUTH);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(clubInfoPanel, BorderLayout.EAST);

        // Today's attendance panel
        JPanel todayPanel = ModernTheme.createCardPanel();
        todayPanel.setLayout(new BorderLayout());
        todayPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel todayTitle = ModernTheme.createHeadingLabel("Today's Attendance - " +
                                                          LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        todayTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.add(markPresentButton);
        buttonPanel.add(markAbsentButton);
        buttonPanel.add(refreshButton);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(ModernTheme.WHITE);
        statusPanel.add(statusLabel);

        JPanel todayContent = new JPanel(new BorderLayout());
        todayContent.setBackground(ModernTheme.WHITE);
        todayContent.add(todayTitle, BorderLayout.NORTH);
        todayContent.add(buttonPanel, BorderLayout.CENTER);
        todayContent.add(statusPanel, BorderLayout.SOUTH);

        todayPanel.add(todayContent, BorderLayout.CENTER);

        // History panel
        JPanel historyPanel = ModernTheme.createCardPanel();
        historyPanel.setLayout(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel historyTitle = ModernTheme.createHeadingLabel("Attendance History");
        historyTitle.setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane tableScrollPane = new JScrollPane(attendanceHistoryTable);
        tableScrollPane.setBorder(ModernTheme.createRoundedBorder(ModernTheme.MEDIUM_GRAY, 1));

        historyPanel.add(historyTitle, BorderLayout.NORTH);
        historyPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Main layout
        JPanel mainContent = new JPanel(new GridLayout(2, 1, 0, 15));
        mainContent.setBackground(ModernTheme.WHITE);
        mainContent.add(todayPanel);
        mainContent.add(historyPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        markPresentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markAttendance(true);
            }
        });

        markAbsentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markAttendance(false);
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
    }

    private void loadData() {
        try {
            User currentUser = authService.getCurrentUser();

            // Load club information
            if (currentUser.getAssignedClubId() != null) {
                Club club = clubDAO.getClubById(currentUser.getAssignedClubId());
                if (club != null) {
                    clubInfoLabel.setText("Club: " + club.getName());
                } else {
                    clubInfoLabel.setText("Club: Not found");
                }
            } else {
                clubInfoLabel.setText("Club: Not assigned");
            }

            // Load attendance statistics
            loadAttendanceStatistics(currentUser);

            // Load attendance history
            loadAttendanceHistory(currentUser);

            // Check if attendance already marked for today
            checkTodayAttendance(currentUser);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading attendance data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAttendanceStatistics(User user) throws SQLException {
        List<Attendance> allAttendance = attendanceDAO.getAttendanceByStudentId(user.getId());
        int totalDays = allAttendance.size();
        long presentDays = allAttendance.stream().filter(a -> "PRESENT".equals(a.getStatus())).count();

        attendanceCountLabel.setText(String.format("Attendance: %d/%d days present", presentDays, totalDays));
    }

    private void loadAttendanceHistory(User user) throws SQLException {
        tableModel.setRowCount(0);

        List<Attendance> attendanceList = attendanceDAO.getAttendanceByStudentId(user.getId());

        for (Attendance attendance : attendanceList) {
            String status = "PRESENT".equals(attendance.getStatus()) ? "Present ✅" : "Absent ❌";
            String timeMarked = attendance.getCreatedAt() != null ?
                attendance.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A";

            Object[] rowData = {
                attendance.getSessionDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                status,
                timeMarked
            };
            tableModel.addRow(rowData);
        }
    }

    private void checkTodayAttendance(User user) throws SQLException {
        LocalDate today = LocalDate.now();
        boolean hasAttendanceToday = attendanceDAO.hasAttendanceForDate(user.getId(), today);

        if (hasAttendanceToday) {
            markPresentButton.setEnabled(false);
            markAbsentButton.setEnabled(false);
            statusLabel.setText("✅ Attendance already marked for today");
            statusLabel.setForeground(ModernTheme.PRIMARY_BLUE);
        } else {
            markPresentButton.setEnabled(true);
            markAbsentButton.setEnabled(true);
            statusLabel.setText("Ready to mark attendance for today");
            statusLabel.setForeground(ModernTheme.TEXT_DARK);
        }
    }

    private void markAttendance(boolean isPresent) {
        try {
            User currentUser = authService.getCurrentUser();

            if (currentUser.getAssignedClubId() == null) {
                JOptionPane.showMessageDialog(this,
                    "You are not assigned to a club. Please contact the administrator.",
                    "No Club Assignment",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate today = LocalDate.now();

            // Check if already marked
            if (attendanceDAO.hasAttendanceForDate(currentUser.getId(), today)) {
                JOptionPane.showMessageDialog(this,
                    "You have already marked attendance for today.",
                    "Attendance Already Marked",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create attendance record
            Attendance attendance = new Attendance();
            attendance.setStudentId(currentUser.getId());
            attendance.setClubId(currentUser.getAssignedClubId());
            attendance.setSessionDate(today);
            attendance.setStatus(isPresent ? "PRESENT" : "ABSENT");
            attendance.setMarkedById(currentUser.getId()); // Self-marked
            attendance.setCreatedAt(LocalDateTime.now());

            // Save to database
            if (attendanceDAO.insertAttendance(attendance)) {
                String statusText = isPresent ? "Present ✅" : "Absent ❌";
                JOptionPane.showMessageDialog(this,
                    "Attendance marked as: " + statusText,
                    "Attendance Recorded",
                    JOptionPane.INFORMATION_MESSAGE);

                // Refresh the data
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to record attendance. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}