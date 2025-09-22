package com.clubmanagement.gui;

import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.User;
import com.clubmanagement.security.PasswordHasher;

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
    private UserDAO userDAO;

    private String[] securityQuestions = {
        "What was the name of your first pet?",
        "What is your mother's maiden name?",
        "What was the name of your first school?",
        "What is your favorite color?",
        "What city were you born in?",
        "What was your childhood nickname?",
        "What was the make of your first car?",
        "What is your favorite book?"
    };

    public SignUpFrame() {
        this.userDAO = new UserDAO();
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

        securityQuestionCombo = new JComboBox<>(securityQuestions);
        securityQuestionCombo.setFont(ModernTheme.BODY_FONT);
        securityQuestionCombo.setBackground(Color.WHITE);
        securityQuestionCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        securityAnswerField = ModernTheme.createStyledTextField();

        roleCombo = new JComboBox<>(User.UserRole.values());
        roleCombo.setFont(ModernTheme.BODY_FONT);
        roleCombo.setBackground(Color.WHITE);
        roleCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

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

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            email.isEmpty() || fullName.isEmpty() || securityAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields.",
                "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match. Please try again.",
                "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.setText("");
            return;
        }

        if (!PasswordHasher.isPasswordStrong(password)) {
            JOptionPane.showMessageDialog(this,
                "Password does not meet security requirements:\n\n" +
                PasswordHasher.getPasswordRequirements(),
                "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Check if username already exists
            if (userDAO.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(this,
                    "Username already exists. Please choose a different username.",
                    "Username Taken", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hash password and security answer
            PasswordHasher.HashedPassword hashedPassword = PasswordHasher.hashPassword(password);
            PasswordHasher.HashedPassword hashedAnswer = PasswordHasher.hashPassword(securityAnswer.toLowerCase());

            // Create user object
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setRole(role);
            newUser.setPasswordHash(hashedPassword.getHash());
            newUser.setPasswordSalt(hashedPassword.getSalt());
            newUser.setSecurityQuestion(securityQuestion);
            newUser.setSecurityAnswer(hashedAnswer.getHash());
            newUser.setFirstLoginCompleted(false);

            // Save to database
            boolean success = userDAO.insertUser(newUser);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "✅ Account created successfully!\n\n" +
                    "Username: " + username + "\n" +
                    "You can now log in with your credentials.",
                    "Account Created", JOptionPane.INFORMATION_MESSAGE);

                // Clear sensitive data
                passwordField.setText("");
                confirmPasswordField.setText("");
                securityAnswerField.setText("");

                // Go back to login
                goBackToLogin();

            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to create account. Please try again.",
                    "Creation Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Unexpected error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
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