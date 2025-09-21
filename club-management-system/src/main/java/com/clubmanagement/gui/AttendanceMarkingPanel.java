package com.clubmanagement.gui;

import com.clubmanagement.services.AttendanceService;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class AttendanceMarkingPanel extends JPanel {
    private AuthenticationService authService;
    private AttendanceService attendanceService;
    private JComboBox<Integer> sessionComboBox;
    private JRadioButton presentRadioButton;
    private JRadioButton absentRadioButton;
    private JButton markButton;
    private JTextArea attendanceStatusArea;

    public AttendanceMarkingPanel(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceService = new AttendanceService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAttendanceStatus();
    }

    private void initializeComponents() {
        Integer[] sessions = new Integer[AttendanceService.getTotalSessions()];
        for (int i = 0; i < sessions.length; i++) {
            sessions[i] = i + 1;
        }
        sessionComboBox = new JComboBox<>(sessions);

        presentRadioButton = new JRadioButton("Present", true);
        absentRadioButton = new JRadioButton("Absent");
        ButtonGroup attendanceGroup = new ButtonGroup();
        attendanceGroup.add(presentRadioButton);
        attendanceGroup.add(absentRadioButton);

        markButton = new JButton("Mark Attendance");
        attendanceStatusArea = new JTextArea(10, 40);
        attendanceStatusArea.setEditable(false);
        attendanceStatusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Mark Attendance", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 5, 5);
        centerPanel.add(new JLabel("Session Number:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(sessionComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 10, 5, 5);
        centerPanel.add(new JLabel("Attendance Status:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.add(presentRadioButton);
        radioPanel.add(Box.createHorizontalStrut(10));
        radioPanel.add(absentRadioButton);
        centerPanel.add(radioPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 20, 10);
        centerPanel.add(markButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        JScrollPane scrollPane = new JScrollPane(attendanceStatusArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Attendance Record"));
        centerPanel.add(scrollPane, gbc);

        add(centerPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Important Information"));
        JLabel infoLabel = new JLabel("<html><b>Attendance Requirement:</b> You must attend at least " +
                AttendanceService.getMinimumRequiredAttendance() + " out of " +
                AttendanceService.getTotalSessions() + " sessions to meet the minimum requirement.</html>");
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        markButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markAttendance();
            }
        });

        sessionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSessionAttendance();
            }
        });
    }

    private void markAttendance() {
        int sessionNumber = (Integer) sessionComboBox.getSelectedItem();
        boolean present = presentRadioButton.isSelected();
        int studentId = authService.getCurrentUser().getId();

        try {
            boolean success = attendanceService.markAttendance(studentId, sessionNumber, present);

            if (success) {
                String status = present ? "present" : "absent";
                JOptionPane.showMessageDialog(this,
                                            "Attendance marked as " + status + " for session " + sessionNumber,
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendanceStatus();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to mark attendance.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void checkSessionAttendance() {
        int sessionNumber = (Integer) sessionComboBox.getSelectedItem();
        int studentId = authService.getCurrentUser().getId();

        try {
            boolean hasAttendance = attendanceService.hasAttendanceForSession(studentId, sessionNumber);
            if (hasAttendance) {
                var attendanceList = attendanceService.getStudentAttendance(studentId);
                for (var attendance : attendanceList) {
                    if (attendance.getSessionNumber() == sessionNumber) {
                        presentRadioButton.setSelected(attendance.isPresent());
                        absentRadioButton.setSelected(!attendance.isPresent());
                        break;
                    }
                }
            } else {
                presentRadioButton.setSelected(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadAttendanceStatus() {
        try {
            int studentId = authService.getCurrentUser().getId();
            var attendanceList = attendanceService.getStudentAttendance(studentId);
            int attendanceCount = attendanceService.getStudentAttendanceCount(studentId);
            boolean meetsRequirement = attendanceService.hasMinimumAttendance(studentId);

            attendanceStatusArea.setText("");
            attendanceStatusArea.append("ATTENDANCE SUMMARY\n");
            attendanceStatusArea.append("==================\n\n");
            attendanceStatusArea.append("Total Sessions Attended: " + attendanceCount + "/" +
                    AttendanceService.getTotalSessions() + "\n");
            attendanceStatusArea.append("Minimum Requirement: " +
                    AttendanceService.getMinimumRequiredAttendance() + " sessions\n");
            attendanceStatusArea.append("Status: " + (meetsRequirement ? "MEETS REQUIREMENT" : "DOES NOT MEET REQUIREMENT") + "\n\n");

            attendanceStatusArea.append("SESSION DETAILS:\n");
            attendanceStatusArea.append("================\n");

            boolean[] sessionAttended = new boolean[AttendanceService.getTotalSessions()];
            for (var attendance : attendanceList) {
                if (attendance.getSessionNumber() >= 1 && attendance.getSessionNumber() <= AttendanceService.getTotalSessions()) {
                    sessionAttended[attendance.getSessionNumber() - 1] = attendance.isPresent();
                }
            }

            for (int i = 0; i < AttendanceService.getTotalSessions(); i++) {
                attendanceStatusArea.append("Session " + (i + 1) + ": ");
                if (sessionAttended[i]) {
                    attendanceStatusArea.append("PRESENT\n");
                } else {
                    try {
                        boolean hasRecord = attendanceService.hasAttendanceForSession(studentId, i + 1);
                        if (hasRecord) {
                            attendanceStatusArea.append("ABSENT\n");
                        } else {
                            attendanceStatusArea.append("NOT MARKED\n");
                        }
                    } catch (SQLException ex) {
                        attendanceStatusArea.append("ERROR\n");
                    }
                }
            }

        } catch (SQLException ex) {
            attendanceStatusArea.setText("Error loading attendance: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}