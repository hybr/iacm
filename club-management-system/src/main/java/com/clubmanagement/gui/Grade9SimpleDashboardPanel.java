package com.clubmanagement.gui;

import com.clubmanagement.dao.AttendanceDAO;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Attendance;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Grade9SimpleDashboardPanel extends JPanel {
    private AuthenticationService authService;
    private AttendanceDAO attendanceDAO;
    private ClubDAO clubDAO;
    private JButton markPresentButton;
    private JButton markAbsentButton;
    private JButton logoutButton;
    private JLabel statusLabel;
    private JLabel welcomeLabel;

    public Grade9SimpleDashboardPanel(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceDAO = new AttendanceDAO();
        this.clubDAO = new ClubDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    private void initializeComponents() {
        User currentUser = authService.getCurrentUser();

        // Welcome message
        welcomeLabel = ModernTheme.createTitleLabel("Welcome, " + currentUser.getFullName());

        // Main action buttons - large and prominent
        markPresentButton = ModernTheme.createPrimaryButton("✅ Mark Present");
        markPresentButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        markPresentButton.setPreferredSize(new Dimension(200, 60));

        markAbsentButton = ModernTheme.createSecondaryButton("❌ Mark Absent");
        markAbsentButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        markAbsentButton.setPreferredSize(new Dimension(200, 60));

        // Logout button
        logoutButton = ModernTheme.createSecondaryButton("🚪 Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setPreferredSize(new Dimension(120, 40));

        // Status label
        statusLabel = ModernTheme.createBodyLabel("Ready to mark attendance for today");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Header panel with welcome and logout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        welcomeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Main content panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ModernTheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        // Today's date label
        JLabel dateLabel = ModernTheme.createHeadingLabel("Today: " +
                                                         LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(dateLabel, gbc);

        // Attendance buttons
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 10, 20);

        gbc.gridx = 0;
        mainPanel.add(markPresentButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(markAbsentButton, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(statusLabel, gbc);

        // Add to main layout
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
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

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }

    private void loadData() {
        try {
            User currentUser = authService.getCurrentUser();
            LocalDate today = LocalDate.now();

            // Check if attendance already marked for today
            boolean hasAttendanceToday = attendanceDAO.hasAttendanceForDate(currentUser.getId(), today);

            if (hasAttendanceToday) {
                markPresentButton.setEnabled(false);
                markAbsentButton.setEnabled(false);
                statusLabel.setText("✅ Attendance already marked for today");
                statusLabel.setForeground(ModernTheme.PRIMARY_BLUE);
            } else {
                markPresentButton.setEnabled(true);
                markAbsentButton.setEnabled(true);
                statusLabel.setText("Please mark your attendance for today");
                statusLabel.setForeground(ModernTheme.TEXT_DARK);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading attendance data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
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
                    "Already Marked",
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
                    "Attendance marked as: " + statusText + "\n\nThank you!",
                    "Attendance Recorded",
                    JOptionPane.INFORMATION_MESSAGE);

                // Refresh the UI
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

    private void logout() {
        int response = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            authService.logout();

            // Close current window and open login frame
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                window.dispose();
            }

            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }
}