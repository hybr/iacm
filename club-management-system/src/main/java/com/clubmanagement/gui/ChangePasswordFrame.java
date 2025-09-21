package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ChangePasswordFrame extends JFrame {
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changePasswordButton;
    private JButton cancelButton;
    private AuthenticationService authService;

    public ChangePasswordFrame(AuthenticationService authService) {
        this.authService = authService;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        currentPasswordField = ModernTheme.createStyledPasswordField();
        newPasswordField = ModernTheme.createStyledPasswordField();
        confirmPasswordField = ModernTheme.createStyledPasswordField();
        changePasswordButton = ModernTheme.createPrimaryButton("Change Password");
        cancelButton = ModernTheme.createSecondaryButton("Cancel");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Change password card
        JPanel passwordCard = ModernTheme.createCardPanel();
        passwordCard.setLayout(new BorderLayout());
        passwordCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = ModernTheme.createTitleLabel("Change Password");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitleLabel = ModernTheme.createBodyLabel("Update your account password");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Current Password
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(ModernTheme.createBodyLabel("Current Password"), gbc);
        gbc.gridy = 1;
        formPanel.add(currentPasswordField, gbc);

        // New Password
        gbc.gridy = 2;
        formPanel.add(ModernTheme.createBodyLabel("New Password"), gbc);
        gbc.gridy = 3;
        formPanel.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridy = 4;
        formPanel.add(ModernTheme.createBodyLabel("Confirm New Password"), gbc);
        gbc.gridy = 5;
        formPanel.add(confirmPasswordField, gbc);

        // Change Password Button
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(changePasswordButton, gbc);

        // Cancel Button
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setBackground(ModernTheme.WHITE);
        linkPanel.add(cancelButton);

        gbc.gridy = 7;
        gbc.insets = new Insets(5, 0, 0, 0);
        formPanel.add(linkPanel, gbc);

        passwordCard.add(headerPanel, BorderLayout.NORTH);
        passwordCard.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(passwordCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Add instructions panel
        JPanel instructionPanel = ModernTheme.createCardPanel();
        instructionPanel.setLayout(new BorderLayout());
        instructionPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel instructionTitle = ModernTheme.createHeadingLabel("Instructions");
        instructionTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel instructionLabel = ModernTheme.createBodyLabel(
            "<html><div style='width: 350px; text-align: center;'>" +
            "Enter your current password and choose a new password.<br>" +
            "New password must be at least 6 characters long." +
            "</div></html>");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        instructionPanel.add(instructionTitle, BorderLayout.NORTH);
        instructionPanel.add(instructionLabel, BorderLayout.CENTER);

        mainPanel.add(instructionPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Allow Enter key to trigger password change
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (currentPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your current password.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            currentPasswordField.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a new password.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            newPasswordField.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters long.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            newPasswordField.requestFocus();
            newPasswordField.selectAll();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.requestFocus();
            confirmPasswordField.selectAll();
            return;
        }

        if (currentPassword.equals(newPassword)) {
            JOptionPane.showMessageDialog(this, "New password must be different from current password.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            newPasswordField.requestFocus();
            newPasswordField.selectAll();
            return;
        }

        try {
            boolean success = authService.changePassword(currentPassword, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Current password is incorrect.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                currentPasswordField.requestFocus();
                currentPasswordField.selectAll();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Clear password fields for security
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private void setupFrame() {
        setTitle("Change Password");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(500, 500));
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);
    }
}