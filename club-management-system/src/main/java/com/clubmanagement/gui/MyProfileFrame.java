package com.clubmanagement.gui;

import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.services.EmailService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class MyProfileFrame extends JFrame {
    private AuthenticationService authService;
    private UserDAO userDAO;
    private ClubDAO clubDAO;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField roleField;
    private JTextField clubField;
    private JTextField memberSinceField;
    private JTextField lastLoginField;
    private JButton editProfileButton;
    private JButton changePasswordButton;
    private JButton closeButton;
    private boolean editMode = false;

    public MyProfileFrame(AuthenticationService authService) {
        this.authService = authService;
        this.userDAO = new UserDAO();
        this.clubDAO = new ClubDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
        loadUserProfile();
    }

    private void initializeComponents() {
        usernameField = ModernTheme.createStyledTextField();
        emailField = ModernTheme.createStyledTextField();
        fullNameField = ModernTheme.createStyledTextField();
        roleField = ModernTheme.createStyledTextField();
        clubField = ModernTheme.createStyledTextField();
        memberSinceField = ModernTheme.createStyledTextField();
        lastLoginField = ModernTheme.createStyledTextField();

        // Initially disable editing
        setFieldsEditable(false);

        editProfileButton = ModernTheme.createSecondaryButton("Edit Profile");
        changePasswordButton = ModernTheme.createSecondaryButton("Change Password");
        closeButton = ModernTheme.createPrimaryButton("Close");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Profile card
        JPanel profileCard = ModernTheme.createCardPanel();
        profileCard.setLayout(new BorderLayout());
        profileCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = ModernTheme.createTitleLabel("My Profile");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitleLabel = ModernTheme.createBodyLabel("View and manage your account information");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Profile picture placeholder
        JLabel avatarLabel = new JLabel("👤", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        avatarLabel.setForeground(ModernTheme.PRIMARY_BLUE);
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(avatarLabel, gbc);

        // Form fields
        addFormField(formPanel, "Username:", usernameField, gbc, 1);
        addFormField(formPanel, "Email:", emailField, gbc, 2);
        addFormField(formPanel, "Full Name:", fullNameField, gbc, 3);
        addFormField(formPanel, "Role:", roleField, gbc, 4);
        addFormField(formPanel, "Assigned Club:", clubField, gbc, 5);
        addFormField(formPanel, "Member Since:", memberSinceField, gbc, 6);
        addFormField(formPanel, "Last Login:", lastLoginField, gbc, 7);

        profileCard.add(headerPanel, BorderLayout.NORTH);
        profileCard.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        buttonPanel.add(editProfileButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(closeButton);

        profileCard.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(profileCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel parent, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        JLabel label = ModernTheme.createBodyLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        parent.add(label, gbc);

        gbc.gridx = 1; gbc.gridwidth = 1; gbc.weightx = 1.0;
        field.setPreferredSize(new Dimension(250, 35));
        parent.add(field, gbc);
        gbc.weightx = 0;
    }

    private void setupEventHandlers() {
        editProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEditMode();
            }
        });

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openChangePasswordDialog();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editMode) {
                    int result = JOptionPane.showConfirmDialog(MyProfileFrame.this,
                        "You have unsaved changes. Are you sure you want to close?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                } else {
                    dispose();
                }
            }
        });
    }

    private void loadUserProfile() {
        try {
            User currentUser = authService.getCurrentUser();
            // Refresh user data from database
            User freshUser = userDAO.getUserById(currentUser.getId());
            if (freshUser != null) {
                currentUser = freshUser;
                // Update the auth service with fresh user data
                authService.setCurrentUser(currentUser);
            }

            usernameField.setText(currentUser.getUsername());
            emailField.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
            fullNameField.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
            roleField.setText(formatRole(currentUser.getRole()));

            // Load club information
            if (currentUser.getAssignedClubId() != null) {
                try {
                    Club club = clubDAO.getClubById(currentUser.getAssignedClubId());
                    clubField.setText(club != null ? club.getName() : "Not assigned");
                } catch (SQLException e) {
                    clubField.setText("Error loading club");
                }
            } else {
                clubField.setText("Not assigned");
            }

            // Format dates
            if (currentUser.getCreatedAt() != null) {
                memberSinceField.setText(currentUser.getCreatedAt().format(
                    DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            } else {
                memberSinceField.setText("Unknown");
            }

            if (currentUser.getLastLogin() != null) {
                lastLoginField.setText(currentUser.getLastLogin().format(
                    DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
            } else {
                lastLoginField.setText("First login");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading profile: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleEditMode() {
        editMode = !editMode;

        if (editMode) {
            setFieldsEditable(true);
            editProfileButton.setText("Save Changes");
            editProfileButton = ModernTheme.createPrimaryButton("Save Changes");
            // Replace the button
            Container parent = editProfileButton.getParent();
            if (parent != null) {
                parent.remove(0); // Remove old button
                editProfileButton.addActionListener(e -> toggleEditMode());
                parent.add(editProfileButton, 0);
                parent.revalidate();
                parent.repaint();
            }
        } else {
            saveChanges();
        }
    }

    private void setFieldsEditable(boolean editable) {
        emailField.setEditable(editable);
        fullNameField.setEditable(editable);
        // Username, role, club, and dates should not be editable
        usernameField.setEditable(false);
        roleField.setEditable(false);
        clubField.setEditable(false);
        memberSinceField.setEditable(false);
        lastLoginField.setEditable(false);

        if (editable) {
            emailField.setBackground(ModernTheme.WHITE);
            fullNameField.setBackground(ModernTheme.WHITE);
        } else {
            emailField.setBackground(ModernTheme.LIGHT_GRAY);
            fullNameField.setBackground(ModernTheme.LIGHT_GRAY);
        }
    }

    private void saveChanges() {
        try {
            User currentUser = authService.getCurrentUser();
            String newEmail = emailField.getText().trim();
            String newFullName = fullNameField.getText().trim();

            // Validate email
            if (!newEmail.isEmpty() && !EmailService.isValidEmail(newEmail)) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Invalid Email",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if email is already taken by another user
            if (!newEmail.equals(currentUser.getEmail())) {
                if (userDAO.emailExists(newEmail)) {
                    JOptionPane.showMessageDialog(this,
                        "This email address is already in use by another account.",
                        "Email Already Exists",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Update user data
            currentUser.setEmail(newEmail);
            currentUser.setFullName(newFullName);

            // Save to database
            if (userDAO.updateUserProfile(currentUser)) {
                setFieldsEditable(false);
                editMode = false;
                editProfileButton.setText("Edit Profile");
                editProfileButton = ModernTheme.createSecondaryButton("Edit Profile");

                // Replace the button
                Container parent = editProfileButton.getParent();
                if (parent != null) {
                    parent.remove(0);
                    editProfileButton.addActionListener(e -> toggleEditMode());
                    parent.add(editProfileButton, 0);
                    parent.revalidate();
                    parent.repaint();
                }

                JOptionPane.showMessageDialog(this,
                    "Profile updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update profile. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openChangePasswordDialog() {
        SwingUtilities.invokeLater(() -> {
            new ChangePasswordFrame(authService).setVisible(true);
        });
    }

    private String formatRole(User.UserRole role) {
        switch (role) {
            case CLUB_MANAGER: return "Club Manager";
            case GRADE_11: return "11th Grader";
            case GRADE_9: return "9th Grader";
            default: return role.toString();
        }
    }

    private void setupFrame() {
        setTitle("My Profile - Club Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(500, 600));
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);
    }
}