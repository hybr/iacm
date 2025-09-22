package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ResetPasswordFrame extends JFrame {
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton resetButton;
    private JButton backToLoginButton;
    private JLabel strengthLabel;
    private AuthenticationService authService;
    private String resetToken;

    public ResetPasswordFrame(String token) {
        this.resetToken = token;
        this.authService = new AuthenticationService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        newPasswordField = ModernTheme.createStyledPasswordField();
        confirmPasswordField = ModernTheme.createStyledPasswordField();

        resetButton = ModernTheme.createPrimaryButton("Reset Password");
        backToLoginButton = ModernTheme.createSecondaryButton("Back to Login");

        strengthLabel = ModernTheme.createBodyLabel("");
        strengthLabel.setHorizontalAlignment(SwingConstants.CENTER);
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

        JLabel titleLabel = ModernTheme.createTitleLabel("Reset Password");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitleLabel = ModernTheme.createBodyLabel("Enter your new password below");
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

        // New Password
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(ModernTheme.createBodyLabel("New Password"), gbc);
        gbc.gridy = 1;
        formPanel.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridy = 2;
        formPanel.add(ModernTheme.createBodyLabel("Confirm Password"), gbc);
        gbc.gridy = 3;
        formPanel.add(confirmPasswordField, gbc);

        // Password strength indicator
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(strengthLabel, gbc);

        // Password requirements
        JPanel requirementsPanel = new JPanel();
        requirementsPanel.setLayout(new BoxLayout(requirementsPanel, BoxLayout.Y_AXIS));
        requirementsPanel.setBackground(ModernTheme.WHITE);
        requirementsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Password Requirements"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        requirementsPanel.add(ModernTheme.createBodyLabel("• At least 8 characters"));
        requirementsPanel.add(ModernTheme.createBodyLabel("• At least one uppercase letter"));
        requirementsPanel.add(ModernTheme.createBodyLabel("• At least one lowercase letter"));
        requirementsPanel.add(ModernTheme.createBodyLabel("• At least one number"));
        requirementsPanel.add(ModernTheme.createBodyLabel("• At least one special character"));

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 20, 0);
        formPanel.add(requirementsPanel, gbc);

        // Reset Password Button
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 20, 0);
        formPanel.add(resetButton, gbc);

        // Back to Login Button
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setBackground(ModernTheme.WHITE);
        linkPanel.add(backToLoginButton);

        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(linkPanel, gbc);

        resetCard.add(headerPanel, BorderLayout.NORTH);
        resetCard.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(resetCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        // Real-time password strength checking
        newPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updatePasswordStrength();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPassword();
            }
        });

        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPassword();
            }
        });

        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });
    }

    private void updatePasswordStrength() {
        String password = new String(newPasswordField.getPassword());
        PasswordStrength strength = checkPasswordStrength(password);

        switch (strength) {
            case WEAK:
                strengthLabel.setText("Password Strength: Weak");
                strengthLabel.setForeground(Color.RED);
                break;
            case MEDIUM:
                strengthLabel.setText("Password Strength: Medium");
                strengthLabel.setForeground(Color.ORANGE);
                break;
            case STRONG:
                strengthLabel.setText("Password Strength: Strong");
                strengthLabel.setForeground(Color.GREEN);
                break;
            case VERY_STRONG:
                strengthLabel.setText("Password Strength: Very Strong");
                strengthLabel.setForeground(Color.GREEN.darker());
                break;
            default:
                strengthLabel.setText("");
        }
    }

    private PasswordStrength checkPasswordStrength(String password) {
        if (password.length() < 6) {
            return PasswordStrength.WEAK;
        }

        int score = 0;

        // Length bonus
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // Character variety
        if (password.matches(".*[a-z].*")) score++; // lowercase
        if (password.matches(".*[A-Z].*")) score++; // uppercase
        if (password.matches(".*[0-9].*")) score++; // numbers
        if (password.matches(".*[^a-zA-Z0-9].*")) score++; // special chars

        if (score < 3) return PasswordStrength.WEAK;
        if (score < 5) return PasswordStrength.MEDIUM;
        if (score < 6) return PasswordStrength.STRONG;
        return PasswordStrength.VERY_STRONG;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[a-z].*")) return false; // lowercase
        if (!password.matches(".*[A-Z].*")) return false; // uppercase
        if (!password.matches(".*[0-9].*")) return false; // numbers
        if (!password.matches(".*[^a-zA-Z0-9].*")) return false; // special chars
        return true;
    }

    private void resetPassword() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate inputs
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a new password.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isPasswordValid(newPassword)) {
            JOptionPane.showMessageDialog(this,
                "Password does not meet the requirements.\n" +
                "Please ensure it has:\n" +
                "• At least 8 characters\n" +
                "• Uppercase and lowercase letters\n" +
                "• At least one number\n" +
                "• At least one special character",
                "Invalid Password", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Error", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.setText("");
            return;
        }

        try {
            // Reset password using token
            boolean success = authService.resetPasswordWithToken(resetToken, newPassword);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Password has been successfully reset!\n\n" +
                    "You can now log in with your new password.",
                    "Password Reset Successful",
                    JOptionPane.INFORMATION_MESSAGE);

                // Go back to login
                goBackToLogin();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to reset password.\n\n" +
                    "The reset link may have expired or been used already.\n" +
                    "Please request a new password reset.",
                    "Reset Failed",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Clear password fields for security
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private void goBackToLogin() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

    private void setupFrame() {
        setTitle("Club Management System - Reset Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private enum PasswordStrength {
        WEAK, MEDIUM, STRONG, VERY_STRONG
    }
}