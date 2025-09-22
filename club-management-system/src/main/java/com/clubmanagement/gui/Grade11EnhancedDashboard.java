package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Enhanced dashboard for Grade 11 students with attendance marking and proposal features
 */
public class Grade11EnhancedDashboard extends JPanel {
    private AuthenticationService authService;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private User currentUser;

    // Feature panels
    private Grade11SelfAttendancePanel attendancePanel;
    private ProposalSubmissionPanel proposalSubmissionPanel;
    private ProposalStatusPanel proposalStatusPanel;
    private Grade9StudentsViewPanel grade9StudentsViewPanel;

    // Navigation buttons
    private JButton attendanceButton;
    private JButton uploadProposalButton;
    private JButton checkProposalStatusButton;
    private JButton viewGrade9StudentsButton;
    private JButton logoutButton;

    // Logout callback
    private Runnable logoutCallback;

    public Grade11EnhancedDashboard(AuthenticationService authService) {
        this.authService = authService;
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
        attendancePanel = new Grade11SelfAttendancePanel(authService);
        proposalSubmissionPanel = new ProposalSubmissionPanel(authService);
        proposalStatusPanel = new ProposalStatusPanel(authService);
        grade9StudentsViewPanel = new Grade9StudentsViewPanel(authService);

        // Add panels to card layout
        contentPanel.add(attendancePanel, "attendance");
        contentPanel.add(proposalSubmissionPanel, "uploadProposal");
        contentPanel.add(proposalStatusPanel, "checkStatus");
        contentPanel.add(grade9StudentsViewPanel, "viewGrade9");

        // Initialize navigation buttons
        attendanceButton = createNavigationButton("📝 Mark Attendance", "Mark your daily attendance");
        uploadProposalButton = createNavigationButton("📤 Upload Proposal", "Submit a new proposal document");
        checkProposalStatusButton = createNavigationButton("📊 Check Proposal Status", "View status of your submitted proposals");
        viewGrade9StudentsButton = createNavigationButton("👥 View Grade 9", "View Grade 9 students and their clubs");
        logoutButton = createLogoutButton("🚪 Logout", "Sign out of the application");
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

        // Header panel with title and user info
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
        JLabel titleLabel = ModernTheme.createTitleLabel("Grade 11 Dashboard");
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
        uploadProposalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkProposalStatusButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewGrade9StudentsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        navigationPanel.add(attendanceButton);
        navigationPanel.add(Box.createVerticalStrut(15));
        navigationPanel.add(uploadProposalButton);
        navigationPanel.add(Box.createVerticalStrut(15));
        navigationPanel.add(checkProposalStatusButton);
        navigationPanel.add(Box.createVerticalStrut(15));
        navigationPanel.add(viewGrade9StudentsButton);
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

        uploadProposalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUploadProposalPanel();
            }
        });

        checkProposalStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCheckProposalStatusPanel();
            }
        });

        viewGrade9StudentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGrade9StudentsViewPanel();
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

    private void showUploadProposalPanel() {
        cardLayout.show(contentPanel, "uploadProposal");
        updateActiveButton(uploadProposalButton);
    }

    private void showCheckProposalStatusPanel() {
        cardLayout.show(contentPanel, "checkStatus");
        updateActiveButton(checkProposalStatusButton);
    }

    private void showGrade9StudentsViewPanel() {
        cardLayout.show(contentPanel, "viewGrade9");
        updateActiveButton(viewGrade9StudentsButton);
        // Refresh the Grade 9 students view panel
        if (grade9StudentsViewPanel != null) {
            grade9StudentsViewPanel.refreshPanel();
        }
    }

    private void updateActiveButton(JButton activeButton) {
        // Reset all button colors
        attendanceButton.setBackground(ModernTheme.PRIMARY_BLUE);
        uploadProposalButton.setBackground(ModernTheme.PRIMARY_BLUE);
        checkProposalStatusButton.setBackground(ModernTheme.PRIMARY_BLUE);
        viewGrade9StudentsButton.setBackground(ModernTheme.PRIMARY_BLUE);

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
        // Proposal panels will refresh automatically when they become visible
    }

    /**
     * Set callback for logout action
     */
    public void setLogoutCallback(Runnable callback) {
        this.logoutCallback = callback;
    }

    /**
     * Handle logout action with confirmation
     */
    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
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
}