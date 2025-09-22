package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private JButton forgotPasswordButton;
    private AuthenticationService authService;

    public LoginFrame() {
        this.authService = new AuthenticationService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        usernameField = ModernTheme.createStyledTextField();
        passwordField = ModernTheme.createStyledPasswordField();
        loginButton = ModernTheme.createPrimaryButton("Login");
        signUpButton = ModernTheme.createSecondaryButton("Sign Up");

        // Create forgot password as a styled link/text button
        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordButton.setForeground(ModernTheme.PRIMARY_BLUE);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setHorizontalAlignment(SwingConstants.CENTER);

        // Add hover effect for better UX
        forgotPasswordButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                forgotPasswordButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
                forgotPasswordButton.setText("<html><u>Forgot Password?</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                forgotPasswordButton.setText("Forgot Password?");
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Main container with modern styling
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Login card
        JPanel loginCard = ModernTheme.createCardPanel();
        loginCard.setLayout(new BorderLayout());
        loginCard.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Header section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = ModernTheme.createTitleLabel("Club Management System");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitleLabel = ModernTheme.createBodyLabel("Sign in to your account");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Form section
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel usernameLabel = ModernTheme.createBodyLabel("Username");
        formPanel.add(usernameLabel, gbc);

        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridy = 2;
        JLabel passwordLabel = ModernTheme.createBodyLabel("Password");
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        // Forgot Password link - positioned right below password field
        gbc.gridy = 4;
        gbc.insets = new Insets(8, 0, 8, 0);
        JPanel forgotPasswordPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPasswordPanel.setBackground(ModernTheme.WHITE);
        forgotPasswordPanel.add(forgotPasswordButton);
        formPanel.add(forgotPasswordPanel, gbc);

        // Login button
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 15, 0);
        formPanel.add(loginButton, gbc);

        // Sign Up button (separate from Forgot Password)
        JPanel signUpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        signUpPanel.setBackground(ModernTheme.WHITE);
        signUpPanel.add(signUpButton);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(signUpPanel, gbc);

        loginCard.add(headerPanel, BorderLayout.NORTH);
        loginCard.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(loginCard, BorderLayout.CENTER);

        // Info panel with modern styling
        JPanel infoPanel = ModernTheme.createCardPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel infoTitle = ModernTheme.createHeadingLabel("Default Login Credentials");
        infoTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel credentialsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        credentialsPanel.setBackground(ModernTheme.WHITE);
        credentialsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        credentialsPanel.add(ModernTheme.createBodyLabel("Club Manager: manager / manager123"));
        credentialsPanel.add(ModernTheme.createBodyLabel("11th Grader: grade11_1 / pass123"));
        credentialsPanel.add(ModernTheme.createBodyLabel("9th Grader: grade9_1 / pass123"));

        infoPanel.add(infoTitle, BorderLayout.NORTH);
        infoPanel.add(credentialsPanel, BorderLayout.CENTER);

        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignUpFrame();
            }
        });

        forgotPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openForgotPasswordFrame();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                                        "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (authService.login(username, password)) {
                JOptionPane.showMessageDialog(this, "Login successful!",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);

                this.dispose();

                try {
                    // Check if 9th grader needs to complete first login
                    if (authService.needsFirstLoginCompletion()) {
                        // Grade 9 students need club selection
                        SwingUtilities.invokeLater(() -> {
                            try {
                                new ClubSelectionFrame(authService).setVisible(true);
                            } catch (Exception e) {
                                System.err.println("Error opening ClubSelectionFrame: " + e.getMessage());
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(null,
                                    "Error opening club selection: " + e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    } else if (authService.needsClubSelection()) {
                        // Grade 11 students need club selection (multiple clubs)
                        SwingUtilities.invokeLater(() -> {
                            try {
                                new Grade11ClubSelectionFrame(authService).setVisible(true);
                            } catch (Exception e) {
                                System.err.println("Error opening Grade11ClubSelectionFrame: " + e.getMessage());
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(null,
                                    "Error opening Grade 11 club selection: " + e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            try {
                                new MainDashboard(authService).setVisible(true);
                            } catch (Exception e) {
                                System.err.println("Error opening MainDashboard: " + e.getMessage());
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(null,
                                    "Error opening main dashboard: " + e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                } catch (Exception e) {
                    System.err.println("Error during post-login processing: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Error during login processing: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.",
                                            "Login Error", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openSignUpFrame() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new SignUpFrame().setVisible(true);
        });
    }

    private void openForgotPasswordFrame() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new ForgotPasswordFrame().setVisible(true);
        });
    }

    private void setupFrame() {
        setTitle("Club Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(500, 650));
        setLocationRelativeTo(null);
        setResizable(false);

        // Set icon and modern appearance
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Add subtle window shadow effect (if supported)
        try {
            setOpacity(0.98f);
        } catch (Exception e) {
            // Ignore if not supported
        }
    }
}