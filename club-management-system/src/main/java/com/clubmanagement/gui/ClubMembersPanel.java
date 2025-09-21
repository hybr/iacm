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
import java.sql.SQLException;
import java.util.List;

public class ClubMembersPanel extends JPanel {
    private AuthenticationService authService;
    private ClubAllocationService allocationService;
    private ClubDAO clubDAO;
    private UserDAO userDAO;
    private JComboBox<Club> clubComboBox;
    private JTable membersTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ClubMembersPanel(AuthenticationService authService) {
        this.authService = authService;
        this.allocationService = new ClubAllocationService();
        this.clubDAO = new ClubDAO();
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadClubs();
    }

    private void initializeComponents() {
        clubComboBox = new JComboBox<>();
        String[] columnNames = {"Student ID", "Student Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        membersTable = new JTable(tableModel);
        refreshButton = new JButton("Refresh");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Club Members", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Select Club:"));
        controlPanel.add(clubComboBox);
        controlPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(membersTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        clubComboBox.addActionListener(e -> loadClubMembers());
        refreshButton.addActionListener(e -> {
            loadClubs();
            loadClubMembers();
        });
    }

    private void loadClubs() {
        try {
            List<Club> clubs = clubDAO.getAllClubs();
            clubComboBox.removeAllItems();
            for (Club club : clubs) {
                clubComboBox.addItem(club);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clubs: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadClubMembers() {
        Club selectedClub = (Club) clubComboBox.getSelectedItem();
        if (selectedClub == null) return;

        try {
            List<ClubAllocation> allocations = allocationService.getAllocationsByClub(selectedClub.getId());
            tableModel.setRowCount(0);

            for (ClubAllocation allocation : allocations) {
                User student = userDAO.getUserById(allocation.getStudentId());
                if (student != null) {
                    Object[] row = {
                        student.getId(),
                        student.getUsername()
                    };
                    tableModel.addRow(row);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading club members: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}