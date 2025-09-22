package com.clubmanagement.gui;

import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.User;
import com.clubmanagement.security.PasswordHasher;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ForgotPasswordFrame extends JFrame {
    private JTextField usernameField;
    private JLabel securityQuestionLabel;
    private JTextField securityAnswerField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton verifyUsernameButton;
    private JButton verifyAnswerButton;
    private JButton resetPasswordButton;
    private JButton backToLoginButton;
    private AuthenticationService authService;
    private UserDAO userDAO;

    // Multi-step flow components
    private JPanel usernamePanel;
    private JPanel securityQuestionPanel;
    private JPanel newPasswordPanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private User currentUser;

    public ForgotPasswordFrame() {
        this.authService = new AuthenticationService();
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        // Initialize card layout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Step 1: Username entry
        usernameField = ModernTheme.createStyledTextField();
        verifyUsernameButton = ModernTheme.createPrimaryButton("Continue");

        // Step 2: Security question
        securityQuestionLabel = ModernTheme.createBodyLabel("Loading...");
        securityAnswerField = ModernTheme.createStyledTextField();
        verifyAnswerButton = ModernTheme.createPrimaryButton("Verify Answer");

        // Step 3: New password
        newPasswordField = ModernTheme.createStyledPasswordField();
        confirmPasswordField = ModernTheme.createStyledPasswordField();
        resetPasswordButton = ModernTheme.createPrimaryButton("Reset Password");

        // Common components
        backToLoginButton = ModernTheme.createSecondaryButton("Back to Login");

        // Create the three panels
        createUsernamePanel();
        createSecurityQuestionPanel();
        createNewPasswordPanel();
    }

    private void createUsernamePanel() {
        usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setBackground(ModernTheme.WHITE);
        usernamePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ModernTheme.WHITE);

        JLabel titleLabel = ModernTheme.createTitleLabel("Forgot Password");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instructionLabel = ModernTheme.createBodyLabel("Enter your username to begin password reset");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(instructionLabel);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(ModernTheme.createBodyLabel("Username:"), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.add(backToLoginButton);
        buttonPanel.add(verifyUsernameButton);

        usernamePanel.add(headerPanel, BorderLayout.NORTH);
        usernamePanel.add(formPanel, BorderLayout.CENTER);
        usernamePanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createSecurityQuestionPanel() {
        securityQuestionPanel = new JPanel(new BorderLayout());
        securityQuestionPanel.setBackground(ModernTheme.WHITE);
        securityQuestionPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ModernTheme.WHITE);

        JLabel titleLabel = ModernTheme.createTitleLabel("Security Question");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instructionLabel = ModernTheme.createBodyLabel("Answer your security question to continue");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(instructionLabel);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(ModernTheme.createBodyLabel("Question:"), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(securityQuestionLabel, gbc);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(15, 0, 5, 0);
        formPanel.add(ModernTheme.createBodyLabel("Your Answer:"), gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(securityAnswerField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.add(backToLoginButton);
        buttonPanel.add(verifyAnswerButton);

        securityQuestionPanel.add(headerPanel, BorderLayout.NORTH);
        securityQuestionPanel.add(formPanel, BorderLayout.CENTER);
        securityQuestionPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createNewPasswordPanel() {
        newPasswordPanel = new JPanel(new BorderLayout());
        newPasswordPanel.setBackground(ModernTheme.WHITE);
        newPasswordPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ModernTheme.WHITE);

        JLabel titleLabel = ModernTheme.createTitleLabel("Set New Password");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instructionLabel = ModernTheme.createBodyLabel("Enter your new password");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(instructionLabel);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(ModernTheme.createBodyLabel("New Password:"), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(newPasswordField, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(15, 0, 5, 0);
        formPanel.add(ModernTheme.createBodyLabel("Confirm Password:"), gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(confirmPasswordField, gbc);

        // Password requirements
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 5, 0);
        JLabel requirementsLabel = ModernTheme.createBodyLabel(
            "<html><small>" + PasswordHasher.getPasswordRequirements().replace("\n", "<br>") + "</small></html>"
        );
        requirementsLabel.setForeground(ModernTheme.DARK_GRAY);
        formPanel.add(requirementsLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.add(backToLoginButton);
        buttonPanel.add(resetPasswordButton);

        newPasswordPanel.add(headerPanel, BorderLayout.NORTH);
        newPasswordPanel.add(formPanel, BorderLayout.CENTER);
        newPasswordPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Content card
        JPanel contentCard = ModernTheme.createCardPanel();
        contentCard.setLayout(new BorderLayout());

        // Add panels to card layout
        cardPanel.add(usernamePanel, "username");
        cardPanel.add(securityQuestionPanel, "security");
        cardPanel.add(newPasswordPanel, "password");

        contentCard.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(contentCard, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Start with username panel
        cardLayout.show(cardPanel, "username");
    }

    private void setupEventHandlers() {
        verifyUsernameButton.addActionListener(e -> verifyUsername());
        verifyAnswerButton.addActionListener(e -> verifySecurityAnswer());
        resetPasswordButton.addActionListener(e -> resetPassword());
        backToLoginButton.addActionListener(e -> goBackToLogin());
    }

    private void verifyUsername() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your username.",
                "Username Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                JOptionPane.showMessageDialog(this,
                    "Username not found. Please check your username and try again.",
                    "User Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (user.getSecurityQuestion() == null || user.getSecurityQuestion().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No security question is set for this account.\n" +
                    "Please contact an administrator for password reset assistance.",
                    "No Security Question", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Store user and show security question
            currentUser = user;
            securityQuestionLabel.setText(user.getSecurityQuestion());
            cardLayout.show(cardPanel, "security");
            securityAnswerField.requestFocus();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verifySecurityAnswer() {
        String answer = securityAnswerField.getText().trim();

        if (answer.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your security answer.",
                "Answer Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Verify the security answer (using password hashing for security)
            boolean isCorrect = PasswordHasher.verifyPassword(
                answer.toLowerCase(), // Case insensitive
                currentUser.getSecurityAnswer(),
                currentUser.getPasswordSalt() // Use same salt as password
            );

            if (isCorrect) {
                // Correct answer, proceed to password reset
                cardLayout.show(cardPanel, "password");
                newPasswordField.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Incorrect security answer. Please try again.",
                    "Incorrect Answer", JOptionPane.ERROR_MESSAGE);
                securityAnswerField.setText("");
                securityAnswerField.requestFocus();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error verifying answer: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetPassword() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in both password fields.",
                "Password Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match. Please try again.",
                "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.setText("");
            return;
        }

        if (!PasswordHasher.isPasswordStrong(newPassword)) {
            JOptionPane.showMessageDialog(this,
                "Password does not meet security requirements:\n\n" +
                PasswordHasher.getPasswordRequirements(),
                "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Hash the new password
            PasswordHasher.HashedPassword hashedPassword = PasswordHasher.hashPassword(newPassword);

            // Update password in database
            boolean success = userDAO.updatePassword(
                currentUser.getId(),
                hashedPassword.getHash(),
                hashedPassword.getSalt()
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "✅ Password reset successfully!\n\n" +
                    "You can now log in with your new password.",
                    "Password Reset Complete", JOptionPane.INFORMATION_MESSAGE);

                // Clear sensitive data
                newPasswordField.setText("");
                confirmPasswordField.setText("");

                // Go back to login
                goBackToLogin();

            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update password. Please try again.",
                    "Update Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
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
        setSize(new Dimension(500, 600));
        setLocationRelativeTo(null);
        setResizable(false);

        // Set modern appearance
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);
    }
}