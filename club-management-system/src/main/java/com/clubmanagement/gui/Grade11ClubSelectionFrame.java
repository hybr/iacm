package com.clubmanagement.gui;

import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.dao.Grade11ClubAssignmentDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Club selection frame for Grade 11 students (first login only)
 */
public class Grade11ClubSelectionFrame extends JFrame {
    private AuthenticationService authService;
    private ClubDAO clubDAO;
    private Grade11ClubAssignmentDAO assignmentDAO;

    // UI Components
    private JLabel titleLabel;
    private JLabel instructionLabel;
    private JPanel clubPanel;
    private JButton saveButton;
    private JButton backButton;

    // Club checkboxes
    private List<JCheckBox> clubCheckboxes;
    private List<Club> availableClubs;

    public Grade11ClubSelectionFrame(AuthenticationService authService) {
        this.authService = authService;
        this.clubDAO = new ClubDAO();
        this.assignmentDAO = new Grade11ClubAssignmentDAO();
        this.clubCheckboxes = new ArrayList<>();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadClubs();
        setupFrame();
    }

    private void initializeComponents() {
        // Title
        titleLabel = ModernTheme.createTitleLabel("Select Your Clubs");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Instructions
        instructionLabel = ModernTheme.createBodyLabel(
            "<html><div style='text-align: center; width: 400px;'>" +
            "Choose the clubs you want to work in. You can select multiple clubs.<br>" +
            "This selection can only be made once during your first login." +
            "</div></html>"
        );
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Club panel
        clubPanel = new JPanel();
        clubPanel.setLayout(new BoxLayout(clubPanel, BoxLayout.Y_AXIS));
        clubPanel.setBackground(ModernTheme.WHITE);
        clubPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Available Clubs"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Buttons
        saveButton = ModernTheme.createPrimaryButton("Save & Continue");
        backButton = ModernTheme.createSecondaryButton("Back to Login");
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
        contentCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(instructionLabel);

        // Club selection panel
        JScrollPane scrollPane = new JScrollPane(clubPanel);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);

        contentCard.add(headerPanel, BorderLayout.NORTH);
        contentCard.add(scrollPane, BorderLayout.CENTER);
        contentCard.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadClubs() {
        try {
            // Get all available clubs
            availableClubs = clubDAO.getAllClubs();

            // Create checkboxes for each club
            clubCheckboxes.clear();
            clubPanel.removeAll();

            for (Club club : availableClubs) {
                JCheckBox clubCheckbox = createClubCheckbox(club);
                clubCheckboxes.add(clubCheckbox);

                // Create panel for each club option
                JPanel clubOptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                clubOptionPanel.setBackground(ModernTheme.WHITE);
                clubOptionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                clubOptionPanel.add(clubCheckbox);

                // Add club description/icon if desired
                JLabel clubDescLabel = ModernTheme.createBodyLabel(getClubDescription(club.getName()));
                clubDescLabel.setForeground(ModernTheme.DARK_GRAY);
                clubOptionPanel.add(clubDescLabel);

                clubPanel.add(clubOptionPanel);
            }

            clubPanel.revalidate();
            clubPanel.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading clubs: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JCheckBox createClubCheckbox(Club club) {
        JCheckBox checkbox = new JCheckBox(club.getName());
        checkbox.setFont(ModernTheme.HEADING_FONT);
        checkbox.setBackground(ModernTheme.WHITE);
        checkbox.setFocusPainted(false);

        // Add some styling
        checkbox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        checkbox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return checkbox;
    }

    private String getClubDescription(String clubName) {
        return switch (clubName.toLowerCase()) {
            case "science" -> "- Explore scientific experiments and research";
            case "humanities" -> "- Literature, history, and cultural studies";
            case "social science" -> "- Psychology, sociology, and current events";
            case "math" -> "- Advanced mathematics and problem solving";
            case "art" -> "- Creative arts, drawing, and design";
            case "mind matters" -> "- Mental health awareness and wellness";
            default -> "- " + clubName + " activities and projects";
        };
    }

    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveClubSelections();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });
    }

    private void saveClubSelections() {
        // Get selected clubs
        List<Club> selectedClubs = new ArrayList<>();

        for (int i = 0; i < clubCheckboxes.size(); i++) {
            if (clubCheckboxes.get(i).isSelected()) {
                selectedClubs.add(availableClubs.get(i));
            }
        }

        // Validation: at least one club must be selected
        if (selectedClubs.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one club.",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User currentUser = authService.getCurrentUser();

            // Save club assignments to database
            boolean success = assignmentDAO.assignClubsToStudent(currentUser.getId(), selectedClubs);

            if (success) {
                // Mark first login as completed
                authService.markFirstLoginCompleted(currentUser.getId());

                // Show success message
                String clubNames = selectedClubs.stream()
                    .map(Club::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

                JOptionPane.showMessageDialog(this,
                    "✅ Club selection saved successfully!\n\n" +
                    "Selected clubs: " + clubNames + "\n\n" +
                    "You will now be taken to your dashboard.",
                    "Selection Saved", JOptionPane.INFORMATION_MESSAGE);

                // Redirect to dashboard
                redirectToDashboard();

            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to save club selection. Please try again.",
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void redirectToDashboard() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            try {
                new MainDashboard(authService).setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Error opening dashboard: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void goBackToLogin() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to go back to login?\n" +
            "You will need to complete club selection later.",
            "Confirm", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            authService.logout();
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }

    private void setupFrame() {
        setTitle("Club Management System - Select Your Clubs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(600, 700));
        setLocationRelativeTo(null);
        setResizable(false);

        // Set modern appearance
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);
    }
}