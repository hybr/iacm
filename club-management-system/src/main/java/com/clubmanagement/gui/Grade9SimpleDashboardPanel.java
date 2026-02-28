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

/**
 * Enhanced dashboard for Grade 9 students with modern UI matching Grade 11 style
 */
public class Grade9SimpleDashboardPanel extends JPanel {
    private AuthenticationService authService;
    private AttendanceDAO attendanceDAO;
    private ClubDAO clubDAO;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private User currentUser;

    // Feature panels
    private Grade9SelfAttendancePanel attendancePanel;

    // Navigation buttons
    private JButton attendanceButton;
    private JButton logoutButton;

    // Logout callback
    private Runnable logoutCallback;

    public Grade9SimpleDashboardPanel(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceDAO = new AttendanceDAO();
        this.clubDAO = new ClubDAO();
        this.currentUser = authService.getCurrentUser();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        showAttendancePanel(); // Default view
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        contentPanel = ModernTheme.createContentPanel();
        contentPanel.setLayout(cardLayout);

        // Initialize feature panels
        attendancePanel = new Grade9SelfAttendancePanel(authService);

        // Add panels to card layout
        contentPanel.add(attendancePanel, "attendance");

        // Initialize navigation buttons
        attendanceButton = createNavigationButton("Mark Attendance", "Mark your daily attendance");
        logoutButton = createLogoutButton("Logout", "Sign out of the application");
    }

    private JButton createNavigationButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(ModernTheme.PRIMARY_BLUE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setPreferredSize(new Dimension(250, 60));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ModernTheme.SECONDARY_BLUE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ModernTheme.PRIMARY_BLUE);
            }
        });

        return button;
    }

    private JButton createLogoutButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(220, 53, 69)); // Red color for logout
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setPreferredSize(new Dimension(250, 60));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 35, 51)); // Darker red on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 53, 69)); // Original red
            }
        });

        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel with title and user info (SAME AS GRADE 11)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Navigation panel
        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.WEST);

        // Main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(ModernTheme.WHITE);
        mainContentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, ModernTheme.MEDIUM_GRAY),
            BorderFactory.createEmptyBorder(0, 20, 0, 0)
        ));
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = ModernTheme.createTitleLabel("Grade 9 Dashboard");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // User info
        JLabel userInfoLabel = ModernTheme.createBodyLabel("Welcome, " + currentUser.getFullName() + " (" + currentUser.getUsername() + ")");
        userInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(userInfoLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(ModernTheme.WHITE);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        navigationPanel.setPreferredSize(new Dimension(280, 0));

        // Navigation title
        JLabel navTitle = ModernTheme.createHeadingLabel("Features");
        navTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        navigationPanel.add(navTitle);
        navigationPanel.add(Box.createVerticalStrut(20));

        // Add navigation buttons
        attendanceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        navigationPanel.add(attendanceButton);
        navigationPanel.add(Box.createVerticalGlue());

        // Add logout button at the bottom with extra space
        navigationPanel.add(Box.createVerticalStrut(30));
        navigationPanel.add(logoutButton);
        navigationPanel.add(Box.createVerticalStrut(20));

        return navigationPanel;
    }

    private void setupEventHandlers() {
        attendanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAttendancePanel();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
    }

    private void showAttendancePanel() {
        cardLayout.show(contentPanel, "attendance");
        updateActiveButton(attendanceButton);
        // Refresh the attendance panel
        if (attendancePanel != null) {
            attendancePanel.refreshPanel();
        }
    }

    private void updateActiveButton(JButton activeButton) {
        // Reset all button colors
        attendanceButton.setBackground(ModernTheme.PRIMARY_BLUE);

        // Highlight the active button
        activeButton.setBackground(ModernTheme.ACCENT_BLUE);
    }

    /**
     * Refresh all panels to ensure they have the latest data
     */
    public void refreshAllPanels() {
        if (attendancePanel != null) {
            attendancePanel.refreshPanel();
        }
    }

    /**
     * Set callback for logout action
     */
    public void setLogoutCallback(Runnable callback) {
        this.logoutCallback = callback;
    }

    /**
     * Handle logout action (confirmation is handled by the callback)
     */
    private void handleLogout() {
        if (logoutCallback != null) {
            logoutCallback.run();
        } else {
            // Fallback: show message if no callback is set
            JOptionPane.showMessageDialog(this,
                "Logout functionality not properly configured.",
                "Logout Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}