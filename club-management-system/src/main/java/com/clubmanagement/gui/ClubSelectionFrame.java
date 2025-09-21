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
import java.util.List;

public class ClubSelectionFrame extends JFrame {
    private AuthenticationService authService;
    private ClubDAO clubDAO;
    private UserDAO userDAO;
    private JList<Club> clubList;
    private DefaultListModel<Club> clubListModel;
    private JButton selectButton;
    private JButton logoutButton;
    private JTextArea clubDescriptionArea;

    public ClubSelectionFrame(AuthenticationService authService) {
        this.authService = authService;
        this.clubDAO = new ClubDAO();
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
        loadClubs();
    }

    private void initializeComponents() {
        clubListModel = new DefaultListModel<>();
        clubList = new JList<>(clubListModel);
        clubList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clubList.setFont(ModernTheme.BODY_FONT);
        clubList.setBackground(ModernTheme.WHITE);
        clubList.setBorder(ModernTheme.createRoundedBorder(ModernTheme.MEDIUM_GRAY, 4));

        clubDescriptionArea = new JTextArea();
        clubDescriptionArea.setEditable(false);
        clubDescriptionArea.setWrapStyleWord(true);
        clubDescriptionArea.setLineWrap(true);
        clubDescriptionArea.setFont(ModernTheme.BODY_FONT);
        clubDescriptionArea.setBackground(ModernTheme.LIGHT_GRAY);
        clubDescriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        selectButton = ModernTheme.createPrimaryButton("Join Club");
        selectButton.setEnabled(false);

        logoutButton = ModernTheme.createSecondaryButton("Logout");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header card
        JPanel headerCard = ModernTheme.createCardPanel();
        headerCard.setLayout(new BorderLayout());
        headerCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = ModernTheme.createTitleLabel("Welcome to Club Management!");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        User currentUser = authService.getCurrentUser();
        JLabel welcomeLabel = ModernTheme.createHeadingLabel("Hello " + currentUser.getFullName());
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel instructionLabel = ModernTheme.createBodyLabel(
            "<html><div style='text-align: center; width: 400px;'>" +
            "Since this is your first login, please select a club to join. " +
            "You can view club information and make your selection below." +
            "</div></html>");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(ModernTheme.WHITE);
        headerContent.add(titleLabel);
        headerContent.add(Box.createVerticalStrut(10));
        headerContent.add(welcomeLabel);
        headerContent.add(Box.createVerticalStrut(15));
        headerContent.add(instructionLabel);

        headerCard.add(headerContent, BorderLayout.CENTER);

        // Club selection card
        JPanel selectionCard = ModernTheme.createCardPanel();
        selectionCard.setLayout(new BorderLayout());
        selectionCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Club list section
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(ModernTheme.WHITE);

        JLabel clubListLabel = ModernTheme.createHeadingLabel("Available Clubs");
        listPanel.add(clubListLabel, BorderLayout.NORTH);

        JScrollPane clubScrollPane = new JScrollPane(clubList);
        clubScrollPane.setPreferredSize(new Dimension(250, 200));
        clubScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        listPanel.add(clubScrollPane, BorderLayout.CENTER);

        // Club description section
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBackground(ModernTheme.WHITE);
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JLabel descriptionLabel = ModernTheme.createHeadingLabel("Club Information");
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);

        JScrollPane descScrollPane = new JScrollPane(clubDescriptionArea);
        descScrollPane.setPreferredSize(new Dimension(300, 200));
        descScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        descriptionPanel.add(descScrollPane, BorderLayout.CENTER);

        // Combine list and description
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ModernTheme.WHITE);
        contentPanel.add(listPanel, BorderLayout.WEST);
        contentPanel.add(descriptionPanel, BorderLayout.CENTER);

        selectionCard.add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonPanel.add(selectButton);
        buttonPanel.add(logoutButton);

        selectionCard.add(buttonPanel, BorderLayout.SOUTH);

        // Add all to main panel
        mainPanel.add(headerCard, BorderLayout.NORTH);
        mainPanel.add(selectionCard, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        clubList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Club selectedClub = clubList.getSelectedValue();
                if (selectedClub != null) {
                    updateClubDescription(selectedClub);
                    selectButton.setEnabled(true);
                } else {
                    clubDescriptionArea.setText("");
                    selectButton.setEnabled(false);
                }
            }
        });

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectClub();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }

    private void loadClubs() {
        try {
            List<Club> clubs = clubDAO.getAllClubs();
            clubListModel.clear();
            for (Club club : clubs) {
                clubListModel.addElement(club);
            }

            if (!clubs.isEmpty()) {
                clubDescriptionArea.setText("Select a club from the list to view details and join.");
            } else {
                clubDescriptionArea.setText("No clubs are currently available. Please contact the administrator.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading clubs: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateClubDescription(Club club) {
        StringBuilder description = new StringBuilder();
        description.append("Club: ").append(club.getName()).append("\n\n");

        // Add some default descriptions based on club name
        switch (club.getName().toLowerCase()) {
            case "science club":
                description.append("Explore the wonders of science through experiments, projects, and field trips. " +
                                 "Perfect for students interested in chemistry, physics, biology, and environmental science.");
                break;
            case "literature club":
                description.append("Dive into the world of books, creative writing, and literary discussion. " +
                                 "Share your favorite reads and develop your writing skills.");
                break;
            case "sports club":
                description.append("Stay active and competitive with various sports activities. " +
                                 "Participate in tournaments and improve your athletic abilities.");
                break;
            case "art club":
                description.append("Express your creativity through drawing, painting, sculpture, and digital art. " +
                                 "Learn new techniques and showcase your artistic talents.");
                break;
            case "music club":
                description.append("Make beautiful music together! Learn instruments, form bands, " +
                                 "and perform at school events.");
                break;
            case "technology club":
                description.append("Code, create, and innovate! Learn programming, web development, " +
                                 "robotics, and explore the latest in technology.");
                break;
            default:
                description.append("Join this exciting club and discover new interests and friendships!");
        }

        description.append("\n\nActivities include regular meetings, special events, competitions, and community service projects.");
        description.append("\n\nMeeting Schedule: Weekly meetings after school");
        description.append("\nRequirements: Active participation and enthusiasm");

        clubDescriptionArea.setText(description.toString());
        clubDescriptionArea.setCaretPosition(0);
    }

    private void selectClub() {
        Club selectedClub = clubList.getSelectedValue();
        if (selectedClub == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a club first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to join " + selectedClub.getName() + "?\n" +
            "This selection will be saved and you won't be prompted again.",
            "Confirm Club Selection",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                User currentUser = authService.getCurrentUser();

                // Update user's assigned club and first login status
                currentUser.setAssignedClubId(selectedClub.getId());
                currentUser.setFirstLoginCompleted(true);

                // Save to database
                if (userDAO.updateUserClubAssignment(currentUser.getId(), selectedClub.getId())) {
                    userDAO.updateFirstLoginCompleted(currentUser.getId(), true);

                    // Send confirmation email
                    EmailService.sendClubAssignmentEmail(
                        currentUser.getEmail(),
                        currentUser.getUsername(),
                        selectedClub.getName()
                    );

                    JOptionPane.showMessageDialog(this,
                        "Successfully joined " + selectedClub.getName() + "!\n" +
                        "A confirmation email has been sent to you.",
                        "Club Selection Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Close this frame and open main dashboard
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        new MainDashboard(authService).setVisible(true);
                    });
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to save club selection. Please try again.",
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
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?\n" +
            "You will need to select a club when you login again.",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            authService.logout();
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }

    private void setupFrame() {
        setTitle("Club Selection - Club Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(750, 600));
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);
    }
}