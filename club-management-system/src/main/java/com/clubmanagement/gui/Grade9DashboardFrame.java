package com.clubmanagement.gui;

import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;

/**
 * Standalone frame for Grade 9 students - matches Grade 11 style without MainDashboard blue bar
 */
public class Grade9DashboardFrame extends JFrame {
    private AuthenticationService authService;
    private Grade9SimpleDashboardPanel dashboardPanel;

    public Grade9DashboardFrame(AuthenticationService authService) {
        this.authService = authService;

        initializeFrame();
        setupContent();
    }

    private void initializeFrame() {
        setTitle("Club Management System - Grade 9");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    private void setupContent() {
        setLayout(new BorderLayout());

        // Create dashboard panel
        dashboardPanel = new Grade9SimpleDashboardPanel(authService);

        // Set logout callback to close this frame and open login
        dashboardPanel.setLogoutCallback(() -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                authService.logout();
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new LoginFrame().setVisible(true);
                });
            }
        });

        // Add dashboard to frame
        add(dashboardPanel, BorderLayout.CENTER);
    }
}
