package com.clubmanagement.gui;

import com.clubmanagement.dao.AttendanceDAO;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Attendance;
import com.clubmanagement.models.Attendance.AttendanceStatus;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Self-attendance marking panel for Grade 11 students
 */
public class Grade11SelfAttendancePanel extends JPanel {
    private AuthenticationService authService;
    private AttendanceDAO attendanceDAO;
    private ClubDAO clubDAO;

    // UI Components
    private JLabel titleLabel;
    private JLabel dateLabel;
    private JLabel clubInfoLabel;
    private JButton presentButton;
    private JButton absentButton;
    private JLabel statusMessageLabel;
    private JPanel buttonPanel;

    private User currentUser;
    private LocalDate currentDate;
    private boolean hasMarkedToday;
    private AttendanceStatus todayStatus;

    public Grade11SelfAttendancePanel(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceDAO = new AttendanceDAO();
        this.clubDAO = new ClubDAO();
        this.currentUser = authService.getCurrentUser();
        this.currentDate = LocalDate.now();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAttendanceStatus();
    }

    private void initializeComponents() {
        // Title
        titleLabel = ModernTheme.createTitleLabel("Mark Attendance");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Date display in DD-MM-YYYY format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateLabel = ModernTheme.createHeadingLabel("Today: " + currentDate.format(dateFormatter));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Club information
        clubInfoLabel = ModernTheme.createBodyLabel("Loading club information...");
        clubInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Present button with check mark
        presentButton = new JButton("✅ Present");
        presentButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        presentButton.setPreferredSize(new Dimension(200, 80));
        presentButton.setBackground(new Color(34, 139, 34)); // Forest Green
        presentButton.setForeground(Color.WHITE);
        presentButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        presentButton.setFocusPainted(false);
        presentButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Absent button with X mark
        absentButton = new JButton("❌ Absent");
        absentButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        absentButton.setPreferredSize(new Dimension(200, 80));
        absentButton.setBackground(new Color(220, 20, 60)); // Crimson
        absentButton.setForeground(Color.WHITE);
        absentButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        absentButton.setFocusPainted(false);
        absentButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Status message
        statusMessageLabel = ModernTheme.createBodyLabel("");
        statusMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusMessageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.add(presentButton);
        buttonPanel.add(absentButton);

        // Add hover effects
        setupButtonHoverEffects();
    }

    private void setupButtonHoverEffects() {
        // Present button hover effect
        presentButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (presentButton.isEnabled()) {
                    presentButton.setBackground(new Color(50, 205, 50)); // Lime Green
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (presentButton.isEnabled()) {
                    presentButton.setBackground(new Color(34, 139, 34)); // Forest Green
                }
            }
        });

        // Absent button hover effect
        absentButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (absentButton.isEnabled()) {
                    absentButton.setBackground(new Color(255, 69, 0)); // Red Orange
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (absentButton.isEnabled()) {
                    absentButton.setBackground(new Color(220, 20, 60)); // Crimson
                }
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ModernTheme.WHITE);

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        clubInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(dateLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(clubInfoLabel);

        // Center content panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ModernTheme.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        // Instructions panel
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setBackground(ModernTheme.WHITE);
        JLabel instructionsLabel = ModernTheme.createBodyLabel("Please mark your attendance for today:");
        instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionsPanel.add(instructionsLabel);

        centerPanel.add(instructionsPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(ModernTheme.WHITE);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        statusMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(statusMessageLabel);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        presentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markAttendance(AttendanceStatus.PRESENT);
            }
        });

        absentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markAttendance(AttendanceStatus.ABSENT);
            }
        });
    }

    private void loadAttendanceStatus() {
        try {
            // Load club information
            if (currentUser.getAssignedClubId() != null) {
                Club club = clubDAO.getClubById(currentUser.getAssignedClubId());
                if (club != null) {
                    clubInfoLabel.setText("Club: " + club.getName());
                } else {
                    clubInfoLabel.setText("No club assignment found");
                }
            } else {
                clubInfoLabel.setText("You are not assigned to any club");
                disableButtons("You are not assigned to any club. Please contact the administrator.");
                return;
            }

            // Check if attendance is already marked for today
            checkTodayAttendance();

        } catch (SQLException e) {
            statusMessageLabel.setText("Error loading data: " + e.getMessage());
            statusMessageLabel.setForeground(Color.RED);
        }
    }

    private void checkTodayAttendance() throws SQLException {
        // Check if attendance is already marked for today
        Attendance todayAttendance = attendanceDAO.getStudentAttendanceForDate(
            currentUser.getId(),
            currentUser.getAssignedClubId() != null ? currentUser.getAssignedClubId() : 0,
            currentDate
        );

        if (todayAttendance != null) {
            hasMarkedToday = true;
            todayStatus = todayAttendance.getStatus();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String statusText = "Attendance marked as " + todayStatus + " for " + currentDate.format(dateFormatter);

            statusMessageLabel.setText(statusText);
            statusMessageLabel.setForeground(todayStatus == AttendanceStatus.PRESENT ?
                new Color(34, 139, 34) : new Color(220, 20, 60));

            disableButtons("You have already marked attendance for today.");
        } else {
            hasMarkedToday = false;
            statusMessageLabel.setText("Please select your attendance status for today.");
            statusMessageLabel.setForeground(ModernTheme.DARK_GRAY);
        }
    }

    private void markAttendance(AttendanceStatus status) {
        if (hasMarkedToday) {
            JOptionPane.showMessageDialog(this,
                "You have already marked attendance for today.",
                "Already Marked", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Create attendance record
            Attendance attendance = new Attendance(
                currentUser.getId(),
                currentUser.getAssignedClubId(),
                currentUser.getId(), // Self-marked
                currentDate,
                status,
                "Self-marked attendance"
            );

            // Save to database
            boolean success = attendanceDAO.markAttendance(attendance);

            if (success) {
                hasMarkedToday = true;
                todayStatus = status;

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String confirmationMessage = "Attendance marked as " + status + " for " + currentDate.format(dateFormatter);

                statusMessageLabel.setText(confirmationMessage);
                statusMessageLabel.setForeground(status == AttendanceStatus.PRESENT ?
                    new Color(34, 139, 34) : new Color(220, 20, 60));

                disableButtons("You have already marked attendance for today.");

                // Show confirmation dialog
                JOptionPane.showMessageDialog(this,
                    confirmationMessage,
                    "Attendance Recorded", JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to mark attendance. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disableButtons(String message) {
        presentButton.setEnabled(false);
        absentButton.setEnabled(false);

        // Change button appearance when disabled
        presentButton.setBackground(Color.LIGHT_GRAY);
        absentButton.setBackground(Color.LIGHT_GRAY);
        presentButton.setForeground(Color.DARK_GRAY);
        absentButton.setForeground(Color.DARK_GRAY);

        if (message != null && !message.isEmpty()) {
            statusMessageLabel.setText(message);
            statusMessageLabel.setForeground(ModernTheme.DARK_GRAY);
        }
    }

    /**
     * Refresh the panel to check for updated attendance status
     */
    public void refreshPanel() {
        try {
            checkTodayAttendance();
        } catch (SQLException e) {
            statusMessageLabel.setText("Error refreshing data: " + e.getMessage());
            statusMessageLabel.setForeground(Color.RED);
        }
    }
}