package com.clubmanagement.gui;

import com.clubmanagement.gui.components.NavigationToolbar;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboard extends JFrame {
    private AuthenticationService authService;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private NavigationToolbar toolbar;
    private JLabel statusLabel;

    public MainDashboard(AuthenticationService authService) {
        this.authService = authService;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        contentPanel = ModernTheme.createContentPanel();
        contentPanel.setLayout(cardLayout);

        toolbar = new NavigationToolbar(authService);
        statusLabel = ModernTheme.createBodyLabel("Ready");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Set modern look and feel
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Header with title
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Navigation toolbar for all users
        add(toolbar, BorderLayout.CENTER);

        // Main content area
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.SOUTH);

        loadInitialContent();
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ModernTheme.PRIMARY_BLUE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left side - App title and user info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(ModernTheme.PRIMARY_BLUE);

        JLabel appTitle = new JLabel("Club Management System");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(ModernTheme.WHITE);

        User currentUser = authService.getCurrentUser();
        JLabel userInfo = new JLabel(" • " + currentUser.getUsername() + " (" + formatRole(currentUser.getRole()) + ")");
        userInfo.setFont(ModernTheme.BODY_FONT);
        userInfo.setForeground(ModernTheme.ACCENT_BLUE);

        leftPanel.add(appTitle);
        leftPanel.add(userInfo);

        // Right side - Window controls (minimize, maximize, close)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setBackground(ModernTheme.PRIMARY_BLUE);

        JButton minimizeBtn = createWindowButton("−");
        JButton maximizeBtn = createWindowButton("□");
        JButton closeBtn = createWindowButton("×");

        minimizeBtn.addActionListener(e -> setState(JFrame.ICONIFIED));
        maximizeBtn.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        closeBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        rightPanel.add(minimizeBtn);
        rightPanel.add(maximizeBtn);
        rightPanel.add(closeBtn);

        titlePanel.add(leftPanel, BorderLayout.WEST);
        titlePanel.add(rightPanel, BorderLayout.EAST);

        return titlePanel;
    }

    private JPanel createSimpleTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ModernTheme.PRIMARY_BLUE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left side - App title only for Grade 9
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(ModernTheme.PRIMARY_BLUE);

        JLabel appTitle = new JLabel("Club Management System - My Dashboard");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(ModernTheme.WHITE);

        leftPanel.add(appTitle);

        // Right side - Just close button for Grade 9
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setBackground(ModernTheme.PRIMARY_BLUE);

        JButton closeBtn = createWindowButton("×");
        closeBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        rightPanel.add(closeBtn);

        titlePanel.add(leftPanel, BorderLayout.WEST);
        titlePanel.add(rightPanel, BorderLayout.EAST);

        return titlePanel;
    }

    private JButton createWindowButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(ModernTheme.WHITE);
        button.setBackground(ModernTheme.PRIMARY_BLUE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (text.equals("×")) {
                    button.setBackground(ModernTheme.ERROR_RED);
                } else {
                    button.setBackground(ModernTheme.SECONDARY_BLUE);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ModernTheme.PRIMARY_BLUE);
            }
        });

        return button;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(ModernTheme.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.MEDIUM_GRAY),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        statusPanel.add(statusLabel, BorderLayout.WEST);

        JLabel timeLabel = ModernTheme.createBodyLabel(new java.util.Date().toString());
        statusPanel.add(timeLabel, BorderLayout.EAST);

        return statusPanel;
    }


    private void loadInitialContent() {
        if (authService.isClubManager()) {
            showProposalManagement();
        } else if (authService.isGrade11()) {
            showGrade11SelfAttendance(); // Show self-attendance marking for Grade 11
        } else if (authService.isGrade9()) {
            showAttendanceMarking(); // Grade 9 sees their own attendance history
        }
    }

    private void showProposalManagement() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new ProposalManagementPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "proposalManagement");
        cardLayout.show(contentPanel, "proposalManagement");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showClubAllocation() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new ClubAllocationPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "clubAllocation");
        cardLayout.show(contentPanel, "clubAllocation");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAttendanceReport() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new AttendanceReportPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "attendanceReport");
        cardLayout.show(contentPanel, "attendanceReport");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProposalSubmission() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new ProposalSubmissionPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "proposalSubmission");
        cardLayout.show(contentPanel, "proposalSubmission");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showClubMembers() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new ClubMembersPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "clubMembers");
        cardLayout.show(contentPanel, "clubMembers");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProposalStatus() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new ProposalStatusPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "proposalStatus");
        cardLayout.show(contentPanel, "proposalStatus");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAttendanceDashboard() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new AttendanceDashboardPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "attendanceDashboard");
        cardLayout.show(contentPanel, "attendanceDashboard");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showGrade11SelfAttendance() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new Grade11SelfAttendancePanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade11SelfAttendance");
        cardLayout.show(contentPanel, "grade11SelfAttendance");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showGrade9ClubAssignments() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new Grade9ClubAssignmentsPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade9ClubAssignments");
        cardLayout.show(contentPanel, "grade9ClubAssignments");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAttendanceMarking() {
        if (authService.isGrade11()) {
            // Grade 11 students see their self-attendance marking panel
            showGrade11SelfAttendance();
        } else {
            // Other roles use the basic attendance marking panel
            contentPanel.removeAll();
            JPanel cardPanel = ModernTheme.createCardPanel();
            cardPanel.setLayout(new BorderLayout());

            if (authService.isGrade9()) {
                // Grade 9 students see their simplified dashboard with only attendance and logout
                cardPanel.add(new Grade9SimpleDashboardPanel(authService), BorderLayout.CENTER);
            } else {
                // Fallback for other roles
                JLabel messageLabel = ModernTheme.createBodyLabel("Attendance feature is not available for your role.");
                messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cardPanel.add(messageLabel, BorderLayout.CENTER);
            }

            contentPanel.add(cardPanel, "attendanceMarking");
            cardLayout.show(contentPanel, "attendanceMarking");
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    private void showClubInfo() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new ClubInfoPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "clubInfo");
        cardLayout.show(contentPanel, "clubInfo");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            authService.logout();
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }

    private void openChangePasswordFrame() {
        SwingUtilities.invokeLater(() -> {
            new ChangePasswordFrame(authService).setVisible(true);
        });
    }

    private String formatRole(User.UserRole role) {
        switch (role) {
            case CLUB_MANAGER: return "Club Manager";
            case GRADE_11: return "11th Grader";
            case GRADE_9: return "9th Grader";
            default: return role.toString();
        }
    }

    private void setupEventHandlers() {
        toolbar.addActionListener(e -> handleToolbarAction(e.getActionCommand()));
    }

    private void handleToolbarAction(String command) {
        statusLabel.setText("Processing " + command + "...");

        SwingUtilities.invokeLater(() -> {
            switch (command.toLowerCase()) {
                case "dashboard":
                    loadInitialContent();
                    statusLabel.setText("Dashboard loaded");
                    break;
                case "proposals":
                    showProposalManagement();
                    statusLabel.setText("Proposal Management loaded");
                    break;
                case "allocation":
                    showClubAllocation();
                    statusLabel.setText("Club Allocation loaded");
                    break;
                case "grade9clubs":
                    showGrade9ClubAssignments();
                    statusLabel.setText("Grade 9 Club Assignments loaded");
                    break;
                case "reports":
                    showAttendanceReport();
                    statusLabel.setText("Attendance Report loaded");
                    break;
                case "submit":
                    showProposalSubmission();
                    statusLabel.setText("Proposal Submission loaded");
                    break;
                case "status":
                    showProposalStatus();
                    statusLabel.setText("Proposal Status loaded");
                    break;
                case "members":
                    showClubMembers();
                    statusLabel.setText("Club Members loaded");
                    break;
                case "attendance":
                    showAttendanceMarking();
                    statusLabel.setText("Attendance Marking loaded");
                    break;
                case "selfattendance":
                    if (authService.isGrade11()) {
                        showGrade11SelfAttendance();
                        statusLabel.setText("Self-Attendance Marking loaded");
                    } else {
                        showAttendanceMarking();
                        statusLabel.setText("Attendance loaded");
                    }
                    break;
                case "attendancehistory":
                    showAttendanceReport();
                    statusLabel.setText("Attendance History loaded");
                    break;
                case "clubassignment":
                    showGrade9ClubAssignments();
                    statusLabel.setText("Club Assignment loaded");
                    break;
                case "clubinfo":
                    showClubInfo();
                    statusLabel.setText("Club Info loaded");
                    break;
                case "changepassword":
                    openChangePasswordFrame();
                    statusLabel.setText("Change Password opened");
                    break;
                case "logout":
                    logout();
                    break;
                case "refresh":
                    refreshCurrentView();
                    statusLabel.setText("View refreshed");
                    break;
                case "settings":
                    showSettings();
                    statusLabel.setText("Settings opened");
                    break;
                case "profile":
                    showProfile();
                    statusLabel.setText("Profile information displayed");
                    break;
                case "userguide":
                    showUserGuide();
                    statusLabel.setText("User guide opened");
                    break;
                case "about":
                    showAbout();
                    statusLabel.setText("About dialog opened");
                    break;
                default:
                    statusLabel.setText("Ready");
                    break;
            }
        });
    }

    private void refreshCurrentView() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSettings() {
        JOptionPane.showMessageDialog(this, "Settings functionality coming soon!", "Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showProfile() {
        SwingUtilities.invokeLater(() -> {
            new MyProfileFrame(authService).setVisible(true);
        });
    }

    private void showUserGuide() {
        String guide = "Club Management System User Guide\n\n" +
                      "1. Use the ribbon tabs to navigate between different functions\n" +
                      "2. Home tab contains your main dashboard functions\n" +
                      "3. Account tab has profile and session management\n" +
                      "4. Help tab provides support and information\n\n" +
                      "For more help, contact your system administrator.";
        JOptionPane.showMessageDialog(this, guide, "User Guide", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        String about = "Club Management System\n\n" +
                      "Version: 2.0\n" +
                      "A modern application for managing school clubs\n\n" +
                      "Features:\n" +
                      "• User Management with Registration\n" +
                      "• Proposal Management\n" +
                      "• Club Allocation\n" +
                      "• Attendance Tracking\n" +
                      "• Modern Ribbon Interface\n\n" +
                      "© 2024 School Administration";
        JOptionPane.showMessageDialog(this, about, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupFrame() {
        setTitle("Club Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Remove default title bar for custom one
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        // Set modern background
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);
    }
}