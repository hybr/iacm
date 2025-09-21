package com.clubmanagement.gui;

import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.ClubAllocation;
import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.services.ClubAllocationService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ClubInfoPanel extends JPanel {
    private AuthenticationService authService;
    private ClubAllocationService allocationService;
    private ClubDAO clubDAO;
    private JTextArea infoArea;
    private JButton refreshButton;

    public ClubInfoPanel(AuthenticationService authService) {
        this.authService = authService;
        this.allocationService = new ClubAllocationService();
        this.clubDAO = new ClubDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadClubInfo();
    }

    private void initializeComponents() {
        infoArea = new JTextArea(15, 40);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        refreshButton = new JButton("Refresh");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("My Club Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Club Details"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadClubInfo());
    }

    private void loadClubInfo() {
        try {
            int studentId = authService.getCurrentUser().getId();
            ClubAllocation allocation = allocationService.getStudentAllocation(studentId);

            infoArea.setText("");

            if (allocation == null) {
                infoArea.append("You have not been allocated to any club yet.\n");
                infoArea.append("Please wait for the club allocation process to complete.\n");
                return;
            }

            Club club = clubDAO.getClubById(allocation.getClubId());
            if (club == null) {
                infoArea.append("Error: Club information not found.\n");
                return;
            }

            infoArea.append("YOUR CLUB INFORMATION\n");
            infoArea.append("====================\n\n");
            infoArea.append("Club Name: " + club.getName() + "\n");
            infoArea.append("Club ID: " + club.getId() + "\n");
            infoArea.append("Your Student ID: " + studentId + "\n\n");

            int memberCount = allocationService.getClubMemberCount(club.getId());
            infoArea.append("Total Members: " + memberCount + "\n\n");

            infoArea.append("IMPORTANT REMINDERS:\n");
            infoArea.append("===================\n");
            infoArea.append("• Attend at least 3 out of 5 sessions\n");
            infoArea.append("• Mark your attendance regularly\n");
            infoArea.append("• Participate actively in club activities\n");
            infoArea.append("• Follow club guidelines and rules\n");

        } catch (SQLException ex) {
            infoArea.setText("Error loading club information: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}