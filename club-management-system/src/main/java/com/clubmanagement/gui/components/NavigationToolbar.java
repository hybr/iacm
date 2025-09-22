package com.clubmanagement.gui.components;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple navigation toolbar to replace the ribbon interface
 */
public class NavigationToolbar extends JPanel {
    private AuthenticationService authService;
    private List<ActionListener> actionListeners;

    public NavigationToolbar(AuthenticationService authService) {
        this.authService = authService;
        this.actionListeners = new ArrayList<>();
        initializeComponents();
        setupLayout();
        createNavigationButtons();
    }

    private void initializeComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        setBackground(ModernTheme.PRIMARY_BLUE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.MEDIUM_GRAY),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        setPreferredSize(new Dimension(0, 50));
    }

    private void setupLayout() {
        // Layout is handled by FlowLayout in initializeComponents
    }

    private void createNavigationButtons() {
        if (authService.isClubManager()) {
            createClubManagerButtons();
        } else if (authService.isGrade11()) {
            createGrade11Buttons();
        } else if (authService.isGrade9()) {
            createGrade9Buttons();
        }

        // Common buttons for all users
        createCommonButtons();
    }

    private void createClubManagerButtons() {
        // Dashboard button
        JButton dashboardBtn = createToolbarButton("🏠 Dashboard", "View main dashboard");
        dashboardBtn.addActionListener(e -> notifyAction("dashboard"));
        add(dashboardBtn);

        // Proposals button
        JButton proposalsBtn = createToolbarButton("📋 Proposals", "Manage club proposals");
        proposalsBtn.addActionListener(e -> notifyAction("proposals"));
        add(proposalsBtn);

        // Attendance button
        JButton attendanceBtn = createToolbarButton("📊 Attendance", "View attendance reports");
        attendanceBtn.addActionListener(e -> notifyAction("attendance"));
        add(attendanceBtn);

        // Club Allocation button
        JButton allocationBtn = createToolbarButton("🎯 Allocation", "Manage club allocations");
        allocationBtn.addActionListener(e -> notifyAction("allocation"));
        add(allocationBtn);

        // Reports button
        JButton reportsBtn = createToolbarButton("📈 Reports", "View reports and analytics");
        reportsBtn.addActionListener(e -> notifyAction("reports"));
        add(reportsBtn);

        // Separator
        add(createSeparator());
    }

    private void createGrade11Buttons() {
        // Dashboard button
        JButton dashboardBtn = createToolbarButton("🏠 Home", "View your dashboard");
        dashboardBtn.addActionListener(e -> notifyAction("dashboard"));
        add(dashboardBtn);

        // Self Attendance button
        JButton attendanceBtn = createToolbarButton("✔️ Mark Attendance", "Mark your attendance");
        attendanceBtn.addActionListener(e -> notifyAction("selfAttendance"));
        add(attendanceBtn);

        // Attendance History button
        JButton historyBtn = createToolbarButton("📈 History", "View attendance history");
        historyBtn.addActionListener(e -> notifyAction("attendanceHistory"));
        add(historyBtn);

        // Separator
        add(createSeparator());
    }

    private void createGrade9Buttons() {
        // Dashboard button
        JButton dashboardBtn = createToolbarButton("🏠 Home", "View your dashboard");
        dashboardBtn.addActionListener(e -> notifyAction("dashboard"));
        add(dashboardBtn);

        // Club Assignment button
        JButton clubBtn = createToolbarButton("🎯 My Club", "View club assignment");
        clubBtn.addActionListener(e -> notifyAction("clubAssignment"));
        add(clubBtn);

        // Self Attendance button
        JButton attendanceBtn = createToolbarButton("✔️ Attendance", "Mark attendance");
        attendanceBtn.addActionListener(e -> notifyAction("selfAttendance"));
        add(attendanceBtn);

        // Separator
        add(createSeparator());
    }

    private void createCommonButtons() {
        // Profile button
        JButton profileBtn = createToolbarButton("👤 Profile", "View profile");
        profileBtn.addActionListener(e -> notifyAction("profile"));
        add(profileBtn);

        // Change Password button
        JButton passwordBtn = createToolbarButton("🔑 Password", "Change password");
        passwordBtn.addActionListener(e -> notifyAction("changepassword"));
        add(passwordBtn);

        // Help button
        JButton helpBtn = createToolbarButton("ℹ️ Help", "Get help");
        helpBtn.addActionListener(e -> notifyAction("userguide"));
        add(helpBtn);

        // Logout button
        JButton logoutBtn = createToolbarButton("🚪 Logout", "Sign out");
        logoutBtn.addActionListener(e -> notifyAction("logout"));
        logoutBtn.setBackground(new Color(180, 50, 50)); // Darker red for logout
        logoutBtn.setForeground(Color.WHITE);
        add(logoutBtn);
    }

    private JButton createToolbarButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180)); // Steel blue
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!text.contains("Logout")) {
                    button.setBackground(new Color(100, 150, 200)); // Lighter blue
                } else {
                    button.setBackground(new Color(200, 70, 70)); // Lighter red
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!text.contains("Logout")) {
                    button.setBackground(new Color(70, 130, 180)); // Original blue
                } else {
                    button.setBackground(new Color(180, 50, 50)); // Original red
                }
            }
        });

        return button;
    }

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(new Color(255, 255, 255, 100)); // Semi-transparent white
        separator.setPreferredSize(new Dimension(1, 25));
        return separator;
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    private void notifyAction(String command) {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(new java.awt.event.ActionEvent(this, 0, command));
        }
    }
}