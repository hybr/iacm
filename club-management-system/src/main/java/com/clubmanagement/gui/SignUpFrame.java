package com.clubmanagement.gui;

import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.User;
import com.clubmanagement.models.Club;
import com.clubmanagement.security.PasswordHasher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class SignUpFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JComboBox<String> securityQuestionCombo;
    private JTextField securityAnswerField;
    private JComboBox<User.UserRole> roleCombo;
    private JComboBox<Club> clubCombo;
    private JLabel clubLabel;
    private JButton signUpButton;
    private JButton backToLoginButton;
    private UserDAO userDAO;
    private ClubDAO clubDAO;

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
        this.clubDAO = new ClubDAO();
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

        // Initialize club components (initially hidden)
        clubLabel = ModernTheme.createBodyLabel("Select Club");
        clubCombo = new JComboBox<>();
        clubCombo.setFont(ModernTheme.BODY_FONT);
        clubCombo.setBackground(Color.WHITE);
        clubCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        // Initially hide club selection
        clubLabel.setVisible(false);
        clubCombo.setVisible(false);

        // Load clubs into combo box
        loadClubs();

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

        // Club Selection (initially hidden)
        gbc.gridy = 16;
        formPanel.add(clubLabel, gbc);
        gbc.gridy = 17;
        formPanel.add(clubCombo, gbc);

        // Buttons
        gbc.gridy = 18;
        gbc.insets = new Insets(25, 0, 10, 0);
        formPanel.add(signUpButton, gbc);

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setBackground(ModernTheme.WHITE);
        linkPanel.add(backToLoginButton);

        gbc.gridy = 19;
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

        // Add role change listener to show/hide club selection
        roleCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClubVisibility();
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
        Club selectedClub = (Club) clubCombo.getSelectedItem();

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            email.isEmpty() || fullName.isEmpty() || securityAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields.",
                "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate club selection for Grade 9 and Grade 11
        if ((role == User.UserRole.GRADE_9 || role == User.UserRole.GRADE_11) && selectedClub == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a club for " + role.toString() + " students.",
                "Club Selection Required", JOptionPane.WARNING_MESSAGE);
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
            newUser.setFirstLoginCompleted(true); // Set to true since club is selected during signup

            // Assign club for Grade 9 and Grade 11
            if (selectedClub != null) {
                newUser.setAssignedClubId(selectedClub.getId());
            }

            // Save to database
            boolean success = userDAO.insertUser(newUser);

            if (success) {
                String clubInfo = "";
                if (selectedClub != null) {
                    clubInfo = "\nAssigned Club: " + selectedClub.getName() + "\n" +
                              "Note: Your club assignment is permanent and cannot be changed.";
                }

                JOptionPane.showMessageDialog(this,
                    "✅ Account created successfully!\n\n" +
                    "Username: " + username + clubInfo + "\n\n" +
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

    /**
     * Load available clubs into the club combo box
     */
    private void loadClubs() {
        try {
            List<Club> clubs = clubDAO.getAllClubs();
            clubCombo.removeAllItems();

            // Add default option
            clubCombo.addItem(null);

            // Add all clubs
            for (Club club : clubs) {
                clubCombo.addItem(club);
            }

            // Custom renderer to show club names
            clubCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (value == null) {
                        setText("-- Select a Club --");
                        setForeground(ModernTheme.TEXT_LIGHT);
                    } else if (value instanceof Club) {
                        setText(((Club) value).getName());
                        setForeground(ModernTheme.TEXT_DARK);
                    }

                    return this;
                }
            });

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading clubs: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Update visibility of club selection based on selected role
     */
    private void updateClubVisibility() {
        User.UserRole selectedRole = (User.UserRole) roleCombo.getSelectedItem();
        boolean showClub = (selectedRole == User.UserRole.GRADE_9 || selectedRole == User.UserRole.GRADE_11);

        clubLabel.setVisible(showClub);
        clubCombo.setVisible(showClub);

        // Reset club selection when hiding
        if (!showClub) {
            clubCombo.setSelectedIndex(0);
        }

        // Revalidate and repaint to update layout
        revalidate();
        repaint();
    }
}