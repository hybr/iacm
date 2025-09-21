package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Attendance;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AttendanceService;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AttendanceHistoryPanel extends JPanel {
    private AuthenticationService authService;
    private AttendanceService attendanceService;
    private JLabel titleLabel;
    private JLabel summaryLabel;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public AttendanceHistoryPanel(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceService = new AttendanceService();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAttendanceData();
    }

    private void initializeComponents() {
        User currentUser = authService.getCurrentUser();
        titleLabel = ModernTheme.createTitleLabel("My Attendance History");

        summaryLabel = ModernTheme.createHeadingLabel("Loading attendance summary...");

        // Table setup
        String[] columnNames = {"Date", "Club", "Status", "Notes", "Marked By"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only for Grade 9 students
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(35);
        attendanceTable.setFont(ModernTheme.BODY_FONT);
        attendanceTable.getTableHeader().setFont(ModernTheme.HEADING_FONT);
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer for status column
        attendanceTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        // Set column widths
        attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        attendanceTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Club
        attendanceTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Status
        attendanceTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Notes
        attendanceTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Marked By

        refreshButton = ModernTheme.createSecondaryButton("Refresh");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ModernTheme.WHITE);

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(summaryLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ModernTheme.WHITE);

        JLabel tableTitle = ModernTheme.createHeadingLabel("Attendance Records");
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        JScrollPane tableScrollPane = new JScrollPane(attendanceTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        tableScrollPane.setPreferredSize(new Dimension(700, 300));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.add(refreshButton);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panels
        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadAttendanceData());
    }

    private void loadAttendanceData() {
        try {
            User currentUser = authService.getCurrentUser();
            List<Attendance> attendanceHistory = attendanceService.getStudentAttendanceHistory(currentUser.getId());

            // Update summary
            updateSummary(currentUser.getId());

            // Update table
            updateTable(attendanceHistory);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading attendance data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSummary(int studentId) throws SQLException {
        // Get summary for current month
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        Map<String, Object> summary = attendanceService.getStudentAttendanceSummary(
            studentId, startOfMonth, endOfMonth);

        int totalSessions = (Integer) summary.get("totalSessions");
        int attendedSessions = (Integer) summary.get("attendedSessions");
        double attendanceRate = (Double) summary.get("attendanceRate");

        String summaryText = String.format(
            "This Month: %d/%d sessions attended (%.1f%% attendance rate)",
            attendedSessions, totalSessions, attendanceRate
        );

        summaryLabel.setText(summaryText);

        // Color coding based on attendance rate
        if (attendanceRate >= 90) {
            summaryLabel.setForeground(new Color(0, 150, 0)); // Green
        } else if (attendanceRate >= 75) {
            summaryLabel.setForeground(new Color(200, 100, 0)); // Orange
        } else {
            summaryLabel.setForeground(new Color(200, 0, 0)); // Red
        }
    }

    private void updateTable(List<Attendance> attendanceHistory) {
        tableModel.setRowCount(0);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        for (Attendance attendance : attendanceHistory) {
            Object[] rowData = {
                attendance.getSessionDate().format(dateFormatter),
                attendance.getClubName() != null ? attendance.getClubName() : "Unknown",
                attendance.getStatus(),
                attendance.getNotes() != null ? attendance.getNotes() : "",
                attendance.getMarkerName() != null ? attendance.getMarkerName() : "Unknown"
            };
            tableModel.addRow(rowData);
        }

        if (attendanceHistory.isEmpty()) {
            Object[] emptyMessage = {"No attendance records found", "", "", "", ""};
            tableModel.addRow(emptyMessage);
        }
    }

    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof Attendance.AttendanceStatus) {
                Attendance.AttendanceStatus status = (Attendance.AttendanceStatus) value;
                setText(status.toString());

                // Set background color based on status
                if (!isSelected) {
                    switch (status) {
                        case PRESENT -> setBackground(new Color(220, 255, 220)); // Light green
                        case LATE -> setBackground(new Color(255, 255, 180)); // Light yellow
                        case ABSENT -> setBackground(new Color(255, 220, 220)); // Light red
                        case EXCUSED -> setBackground(new Color(220, 230, 255)); // Light blue
                    }
                }
            }

            return c;
        }
    }
}