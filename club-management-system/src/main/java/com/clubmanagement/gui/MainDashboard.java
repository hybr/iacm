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

        // Set macOS specific settings for better compatibility
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Club Management");

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
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // North: title bar only — toolbar is not used in the current UI
        add(createTitlePanel(), BorderLayout.NORTH);

        // Center: content fills all remaining space
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // South: status bar
        add(createStatusPanel(), BorderLayout.SOUTH);

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

        JButton minimizeBtn = createWindowButton("-");
        JButton maximizeBtn = createWindowButton("+");
        JButton closeBtn = createWindowButton("X");

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

        JButton closeBtn = createWindowButton("X");
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
                if (text.equals("X")) {
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


    private void setToolbarVisible(boolean visible) {
        toolbar.setVisible(visible);
        revalidate();
        repaint();
    }

    private void showManagerDashboard() {
        setToolbarVisible(false);
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());

        ManagerDashboardPanel dashboardPanel = new ManagerDashboardPanel(authService);
        dashboardPanel.setOnLogout(this::logout);
        dashboardPanel.setOnProposals(() -> {
            showProposalManagement();
            statusLabel.setText("Proposal Management loaded");
        });
        dashboardPanel.setOnAttendanceReports(() -> {
            showManagerAttendanceReports();
            statusLabel.setText("Attendance Reports loaded");
        });
        dashboardPanel.setOnClubAssignments(() -> {
            showManagerClubAssignments();
            statusLabel.setText("Club Assignments loaded");
        });

        cardPanel.add(dashboardPanel, BorderLayout.CENTER);
        contentPanel.add(cardPanel, "managerDashboard");
        cardLayout.show(contentPanel, "managerDashboard");
        contentPanel.revalidate();
        contentPanel.repaint();
        statusLabel.setText("Dashboard loaded");
    }

    private void loadInitialContent() {
        if (authService.isClubManager()) {
            showManagerDashboard();
        } else if (authService.isGrade11()) {
            showGrade11Dashboard();
        } else if (authService.isGrade9()) {
            showGrade9Dashboard();
        }
    }

    // ── Grade 11 dashboard and feature pages ─────────────────────────────────

    private void showGrade11Dashboard() {
        setToolbarVisible(false);
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());

        Grade11LandingDashboard dashboard = new Grade11LandingDashboard(authService);
        dashboard.setOnLogout(this::logout);
        dashboard.setOnAttendance(() -> {
            showGrade11AttendanceFeature();
            statusLabel.setText("Attendance loaded");
        });
        dashboard.setOnUploadProposal(() -> {
            showGrade11UploadProposalFeature();
            statusLabel.setText("Upload Proposal loaded");
        });
        dashboard.setOnProposalStatus(() -> {
            showGrade11ProposalStatusFeature();
            statusLabel.setText("Proposal Status loaded");
        });
        dashboard.setOnViewGrade9(() -> {
            showGrade11ViewGrade9Feature();
            statusLabel.setText("Grade 9 Students loaded");
        });

        cardPanel.add(dashboard, BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade11Dashboard");
        cardLayout.show(contentPanel, "grade11Dashboard");
        contentPanel.revalidate();
        contentPanel.repaint();
        statusLabel.setText("Dashboard loaded");
    }

    private void showGrade11AttendanceFeature() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(createBackBar("Dashboard", this::showGrade11Dashboard), BorderLayout.NORTH);
        cardPanel.add(new Grade11SelfAttendancePanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade11Attendance");
        cardLayout.show(contentPanel, "grade11Attendance");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showGrade11UploadProposalFeature() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(createBackBar("Dashboard", this::showGrade11Dashboard), BorderLayout.NORTH);
        cardPanel.add(new ProposalSubmissionPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade11UploadProposal");
        cardLayout.show(contentPanel, "grade11UploadProposal");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showGrade11ProposalStatusFeature() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(createBackBar("Dashboard", this::showGrade11Dashboard), BorderLayout.NORTH);
        cardPanel.add(new ProposalStatusPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade11ProposalStatus");
        cardLayout.show(contentPanel, "grade11ProposalStatus");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showGrade11ViewGrade9Feature() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(createBackBar("Dashboard", this::showGrade11Dashboard), BorderLayout.NORTH);
        cardPanel.add(new Grade9StudentsViewPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade11ViewGrade9");
        cardLayout.show(contentPanel, "grade11ViewGrade9");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ── Grade 9 dashboard and feature pages ──────────────────────────────────

    private void showGrade9Dashboard() {
        setToolbarVisible(false);
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());

        Grade9LandingDashboard dashboard = new Grade9LandingDashboard(authService);
        dashboard.setOnLogout(this::logout);
        dashboard.setOnAttendance(() -> {
            showGrade9AttendanceFeature();
            statusLabel.setText("Attendance loaded");
        });

        cardPanel.add(dashboard, BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade9Dashboard");
        cardLayout.show(contentPanel, "grade9Dashboard");
        contentPanel.revalidate();
        contentPanel.repaint();
        statusLabel.setText("Dashboard loaded");
    }

    private void showGrade9AttendanceFeature() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(createBackBar("Dashboard", this::showGrade9Dashboard), BorderLayout.NORTH);
        cardPanel.add(new Grade9SelfAttendancePanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade9Attendance");
        cardLayout.show(contentPanel, "grade9Attendance");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ── Shared back-navigation bar ────────────────────────────────────────────

    private JPanel createBackBar(String dest, Runnable action) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bar.setBackground(ModernTheme.WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.MEDIUM_GRAY));
        JButton btn = new JButton("Back to " + dest);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        btn.addActionListener(e -> action.run());
        bar.add(btn);
        return bar;
    }

    private void showProposalManagement() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());

        ProposalManagementPanel proposalPanel = new ProposalManagementPanel(authService);
        proposalPanel.setBackToDashboardCallback(() -> {
            showManagerDashboard();
            statusLabel.setText("Returned to Dashboard");
        });

        cardPanel.add(proposalPanel, BorderLayout.CENTER);
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

    private void showGrade9StudentsView() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(new Grade9StudentsViewPanel(authService), BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade9StudentsView");
        cardLayout.show(contentPanel, "grade9StudentsView");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManagerAttendanceReports() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());

        ManagerAttendanceReportPanel attendancePanel = new ManagerAttendanceReportPanel(authService);
        attendancePanel.setBackToDashboardCallback(() -> {
            showManagerDashboard(); // Return to manager dashboard
        });

        cardPanel.add(attendancePanel, BorderLayout.CENTER);
        contentPanel.add(cardPanel, "managerAttendanceReports");
        cardLayout.show(contentPanel, "managerAttendanceReports");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManagerClubAssignments() {
        contentPanel.removeAll();
        JPanel cardPanel = ModernTheme.createCardPanel();
        cardPanel.setLayout(new BorderLayout());

        ManagerClubAssignmentsViewPanel assignmentsPanel = new ManagerClubAssignmentsViewPanel(authService);
        // Set callback to return to dashboard
        assignmentsPanel.setBackToDashboardCallback(() -> {
            showManagerDashboard();
            statusLabel.setText("Returned to Dashboard");
        });

        cardPanel.add(assignmentsPanel, BorderLayout.CENTER);
        contentPanel.add(cardPanel, "managerClubAssignments");
        cardLayout.show(contentPanel, "managerClubAssignments");
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

        Grade11EnhancedDashboard grade11Dashboard = new Grade11EnhancedDashboard(authService);
        grade11Dashboard.setLogoutCallback(() -> {
            logout(); // Use the existing logout method
        });

        cardPanel.add(grade11Dashboard, BorderLayout.CENTER);
        contentPanel.add(cardPanel, "grade11EnhancedDashboard");
        cardLayout.show(contentPanel, "grade11EnhancedDashboard");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Grade 9 club assignments feature disabled per user request
    /*
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
    */

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
                // "grade9clubs" case removed per user request
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
                case "attendancereports":
                    if (authService.isClubManager()) {
                        showManagerAttendanceReports();
                        statusLabel.setText("Attendance Reports loaded");
                    }
                    break;
                case "clubassignments":
                    if (authService.isClubManager()) {
                        showManagerClubAssignments();
                        statusLabel.setText("Club Assignments loaded");
                    }
                    break;
                case "selfattendance":
                    if (authService.isGrade11()) {
                        showGrade11AttendanceFeature();
                        statusLabel.setText("Attendance loaded");
                    } else if (authService.isGrade9()) {
                        showGrade9AttendanceFeature();
                        statusLabel.setText("Attendance loaded");
                    }
                    break;
                case "uploadproposal":
                    if (authService.isGrade11()) {
                        showGrade11UploadProposalFeature();
                        statusLabel.setText("Upload Proposal loaded");
                    }
                    break;
                case "proposalstatus":
                    if (authService.isGrade11()) {
                        showGrade11ProposalStatusFeature();
                        statusLabel.setText("Proposal Status loaded");
                    }
                    break;
                case "viewgrade9":
                    if (authService.isGrade11()) {
                        showGrade11ViewGrade9Feature();
                        statusLabel.setText("Grade 9 Students View loaded");
                    }
                    break;
                case "attendancehistory":
                    showAttendanceReport();
                    statusLabel.setText("Attendance History loaded");
                    break;
                // "clubassignment" case removed per user request
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