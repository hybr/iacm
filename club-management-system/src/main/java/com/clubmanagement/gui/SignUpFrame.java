package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SignUpFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JComboBox<String> securityQuestionCombo;
    private JTextField securityAnswerField;
    private JComboBox<User.UserRole> roleCombo;
    private JButton signUpButton;
    private JButton backToLoginButton;
    private AuthenticationService authService;

    private String[] securityQuestions = {
        "What was the name of your first pet?",
        "What is your mother's maiden name?",
        "What was the name of your first school?",
        "What is your favorite color?",
        "What city were you born in?"
    };

    public SignUpFrame() {
        this.authService = new AuthenticationService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        usernameField = ModernTheme.createStyledTextField();
        passwordField = ModernTheme.createStyledPasswordField();
        confirmPasswordField = ModernTheme.createStyledPasswordField();
        emailField = ModernTheme.createStyledTextField();
        fullNameField = ModernTheme.createStyledTextField();

        securityQuestionCombo = ModernTheme.createStyledComboBox(securityQuestions);
        securityAnswerField = ModernTheme.createStyledTextField();

        roleCombo = new JComboBox<>(User.UserRole.values());
        roleCombo.setFont(ModernTheme.BODY_FONT);
        roleCombo.setForeground(ModernTheme.TEXT_DARK);
        roleCombo.setBackground(ModernTheme.WHITE);
        roleCombo.setBorder(ModernTheme.createRoundedBorder(ModernTheme.MEDIUM_GRAY, 4));
        roleCombo.setPreferredSize(new Dimension(200, 35));

        signUpButton = ModernTheme.createPrimaryButton("Create Account");
        backToLoginButton = ModernTheme.createSecondaryButton("Back to Login");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Sign up card
        JPanel signUpCard = ModernTheme.createCardPanel();
        signUpCard.setLayout(new BorderLayout());
        signUpCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = ModernTheme.createTitleLabel("Create New Account");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitleLabel = ModernTheme.createBodyLabel("Fill in your information to get started");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Form panel with scroll
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(ModernTheme.createBodyLabel("Username"), gbc);
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 2;
        formPanel.add(ModernTheme.createBodyLabel("Password"), gbc);
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridy = 4;
        formPanel.add(ModernTheme.createBodyLabel("Confirm Password"), gbc);
        gbc.gridy = 5;
        formPanel.add(confirmPasswordField, gbc);

        // Email
        gbc.gridy = 6;
        formPanel.add(ModernTheme.createBodyLabel("Email"), gbc);
        gbc.gridy = 7;
        formPanel.add(emailField, gbc);

        // Full Name
        gbc.gridy = 8;
        formPanel.add(ModernTheme.createBodyLabel("Full Name"), gbc);
        gbc.gridy = 9;
        formPanel.add(fullNameField, gbc);

        // Security Question
        gbc.gridy = 10;
        formPanel.add(ModernTheme.createBodyLabel("Security Question"), gbc);
        gbc.gridy = 11;
        formPanel.add(securityQuestionCombo, gbc);

        // Security Answer
        gbc.gridy = 12;
        formPanel.add(ModernTheme.createBodyLabel("Security Answer"), gbc);
        gbc.gridy = 13;
        formPanel.add(securityAnswerField, gbc);

        // Role
        gbc.gridy = 14;
        formPanel.add(ModernTheme.createBodyLabel("Role"), gbc);
        gbc.gridy = 15;
        formPanel.add(roleCombo, gbc);

        // Buttons
        gbc.gridy = 16;
        gbc.insets = new Insets(25, 0, 10, 0);
        formPanel.add(signUpButton, gbc);

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setBackground(ModernTheme.WHITE);
        linkPanel.add(backToLoginButton);

        gbc.gridy = 17;
        gbc.insets = new Insets(5, 0, 0, 0);
        formPanel.add(linkPanel, gbc);

        // Add form to scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(ModernTheme.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        signUpCard.add(headerPanel, BorderLayout.NORTH);
        signUpCard.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(signUpCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSignUp();
            }
        });

        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });
    }

    private void performSignUp() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String securityQuestion = (String) securityQuestionCombo.getSelectedItem();
        String securityAnswer = securityAnswerField.getText().trim();
        User.UserRole role = (User.UserRole) roleCombo.getSelectedItem();

        // Validate input
        String validationError = authService.validateRegistrationData(username, password, confirmPassword,
                                                                     email, fullName, securityQuestion, securityAnswer);
        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = authService.registerUser(username, password, email, fullName,
                                                      securityQuestion, securityAnswer, role);
            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully! You can now log in.",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                goBackToLogin();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create account. Please try again.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackToLogin() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

    private void setupFrame() {
        setTitle("Club Management System - Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(600, 750));
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(500, 600));

        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);
    }
}