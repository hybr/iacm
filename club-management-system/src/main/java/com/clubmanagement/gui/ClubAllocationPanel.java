package com.clubmanagement.gui;

import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.ClubAllocation;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.services.ClubAllocationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ClubAllocationPanel extends JPanel {
    private AuthenticationService authService;
    private ClubAllocationService allocationService;
    private ClubDAO clubDAO;
    private UserDAO userDAO;
    private JTable allocationTable;
    private DefaultTableModel tableModel;
    private JButton allocateButton;
    private JButton refreshButton;

    public ClubAllocationPanel(AuthenticationService authService) {
        this.authService = authService;
        this.allocationService = new ClubAllocationService();
        this.clubDAO = new ClubDAO();
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAllocations();
    }

    private void initializeComponents() {
        String[] columnNames = {"Club Name", "Student Name", "Student ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        allocationTable = new JTable(tableModel);

        allocateButton = new JButton("Auto-Allocate Students");
        refreshButton = new JButton("Refresh");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Club Allocation Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(allocationTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(allocateButton);
        buttonPanel.add(refreshButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        JLabel infoLabel = new JLabel("<html>Auto-allocation distributes 9th grade students evenly across all 6 clubs.</html>");
        infoPanel.add(infoLabel);
        southPanel.add(infoPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        allocateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAutoAllocation();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllocations();
            }
        });
    }

    private void performAutoAllocation() {
        int result = JOptionPane.showConfirmDialog(this,
                "This will redistribute all 9th grade students across clubs.\n" +
                "Existing allocations will be overwritten. Continue?",
                "Confirm Auto-Allocation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                boolean success = allocationService.allocateStudentsToClubs();
                if (success) {
                    JOptionPane.showMessageDialog(this,
                                                "Students have been successfully allocated to clubs!",
                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllocations();
                } else {
                    JOptionPane.showMessageDialog(this,
                                                "Failed to allocate students. Make sure clubs exist.",
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                                            "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void loadAllocations() {
        try {
            List<ClubAllocation> allocations = allocationService.getAllAllocations();
            tableModel.setRowCount(0);

            for (ClubAllocation allocation : allocations) {
                Club club = clubDAO.getClubById(allocation.getClubId());
                User student = userDAO.getUserById(allocation.getStudentId());

                String clubName = club != null ? club.getName() : "Unknown Club";
                String studentName = student != null ? student.getUsername() : "Unknown Student";

                Object[] row = {
                    clubName,
                    studentName,
                    allocation.getStudentId()
                };
                tableModel.addRow(row);
            }

            updateAllocationStatus();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading allocations: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateAllocationStatus() {
        try {
            List<Club> clubs = clubDAO.getAllClubs();
            List<User> grade9Students = userDAO.getUsersByRole(User.UserRole.GRADE_9);

            StringBuilder statusText = new StringBuilder();
            statusText.append("ALLOCATION SUMMARY:\n");
            statusText.append("Total 9th Grade Students: ").append(grade9Students.size()).append("\n");
            statusText.append("Total Clubs: ").append(clubs.size()).append("\n\n");

            for (Club club : clubs) {
                int memberCount = allocationService.getClubMemberCount(club.getId());
                statusText.append(club.getName()).append(": ").append(memberCount).append(" students\n");
            }

            boolean isComplete = allocationService.isAllocationComplete();
            statusText.append("\nAllocation Status: ").append(isComplete ? "COMPLETE" : "INCOMPLETE");

            JOptionPane.showMessageDialog(this, statusText.toString(),
                                        "Allocation Status", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}