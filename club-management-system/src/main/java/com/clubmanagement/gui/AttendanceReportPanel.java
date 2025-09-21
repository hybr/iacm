package com.clubmanagement.gui;

import com.clubmanagement.models.User;
import com.clubmanagement.services.AttendanceService;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AttendanceReportPanel extends JPanel {
    private AuthenticationService authService;
    private AttendanceService attendanceService;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public AttendanceReportPanel(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceService = new AttendanceService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAttendanceReport();
    }

    private void initializeComponents() {
        String[] columnNames = {"Student ID", "Student Name", "Sessions Attended", "Meets Requirement", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(tableModel);
        refreshButton = new JButton("Refresh Report");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Attendance Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadAttendanceReport());
    }

    private void loadAttendanceReport() {
        try {
            AttendanceService.AttendanceReport report = attendanceService.generateAttendanceReport();
            tableModel.setRowCount(0);

            for (User student : report.getAllStudents()) {
                int attendanceCount = attendanceService.getStudentAttendanceCount(student.getId());
                boolean meetsRequirement = attendanceService.hasMinimumAttendance(student.getId());
                boolean isPoorAttendance = report.getPoorAttendanceStudents().contains(student);

                String status = isPoorAttendance ? "POOR ATTENDANCE" : "GOOD";

                Object[] row = {
                    student.getId(),
                    student.getUsername(),
                    attendanceCount + "/" + AttendanceService.getTotalSessions(),
                    meetsRequirement ? "Yes" : "No",
                    status
                };
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading attendance report: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}