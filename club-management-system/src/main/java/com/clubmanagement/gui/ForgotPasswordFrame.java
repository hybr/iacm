package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ForgotPasswordFrame extends JFrame {
    private JTextField usernameField;
    private JButton verifyButton;
    private JButton backToLoginButton;
    private AuthenticationService authService;

    public ForgotPasswordFrame() {
        this.authService = new AuthenticationService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);

        verifyButton = new JButton("Send Reset Email");
        backToLoginButton = new JButton("Back to Login");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Reset card
        JPanel resetCard = ModernTheme.createCardPanel();
        resetCard.setLayout(new BorderLayout());
        resetCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = ModernTheme.createTitleLabel("Forgot Password");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitleLabel = ModernTheme.createBodyLabel("Enter your username or email to receive a password reset link");
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

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(ModernTheme.createBodyLabel("Username or Email"), gbc);
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Send Reset Email Button
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 20, 0);
        formPanel.add(verifyButton, gbc);

        // Back to Login Button
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setBackground(ModernTheme.WHITE);
        linkPanel.add(backToLoginButton);

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(linkPanel, gbc);

        resetCard.add(headerPanel, BorderLayout.NORTH);
        resetCard.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(resetCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendResetEmail();
            }
        });

        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });
    }

    private void sendResetEmail() {
        String usernameOrEmail = usernameField.getText().trim();

        if (usernameOrEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username or email.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Request password reset token
            boolean success = authService.requestPasswordReset(usernameOrEmail);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "<html><div style='width: 300px;'>" +
                    "A password reset link has been sent to your email address.<br><br>" +
                    "Please check your email and click the link to reset your password.<br><br>" +
                    "The link will expire in 30 minutes for security reasons." +
                    "</div></html>",
                    "Reset Link Sent",
                    JOptionPane.INFORMATION_MESSAGE);

                // Close this dialog and return to login
                goBackToLogin();
            } else {
                JOptionPane.showMessageDialog(this,
                    "User not found or no email address on file.\n" +
                    "Please contact the administrator for assistance.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void goBackToLogin() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

    private void setupFrame() {
        setTitle("Club Management System - Forgot Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }
}