package com.clubmanagement.gui;

import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grade9ClubAssignmentsPanel extends JPanel {
    private AuthenticationService authService;
    private UserDAO userDAO;
    private ClubDAO clubDAO;
    private JTable assignmentsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton exportButton;
    private JLabel statsLabel;
    private JComboBox<String> clubFilterCombo;

    public Grade9ClubAssignmentsPanel(AuthenticationService authService) {
        this.authService = authService;
        this.userDAO = new UserDAO();
        this.clubDAO = new ClubDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAssignments();
    }

    private void initializeComponents() {
        // Table setup
        String[] columnNames = {
            "Student Name", "Username", "Email", "Assigned Club",
            "First Login Completed", "Last Login"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignmentsTable = new JTable(tableModel);
        assignmentsTable.setFont(ModernTheme.BODY_FONT);
        assignmentsTable.setRowHeight(25);
        assignmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        assignmentsTable.setRowSorter(sorter);

        // Buttons
        refreshButton = ModernTheme.createPrimaryButton("Refresh");
        exportButton = ModernTheme.createSecondaryButton("Export to CSV");

        // Stats label
        statsLabel = ModernTheme.createBodyLabel("Loading statistics...");

        // Filter combo
        clubFilterCombo = new JComboBox<>();
        clubFilterCombo.setFont(ModernTheme.BODY_FONT);
        clubFilterCombo.addItem("All Clubs");
        clubFilterCombo.addItem("Unassigned Students");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = ModernTheme.createTitleLabel("Grade 9 Club Assignments");
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setBackground(ModernTheme.WHITE);

        JLabel filterLabel = ModernTheme.createBodyLabel("Filter by club: ");
        controlsPanel.add(filterLabel);
        controlsPanel.add(clubFilterCombo);
        controlsPanel.add(refreshButton);
        controlsPanel.add(exportButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlsPanel, BorderLayout.EAST);

        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(ModernTheme.WHITE);
        statsPanel.add(statsLabel);

        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(assignmentsTable);
        tableScrollPane.setBorder(ModernTheme.createRoundedBorder(ModernTheme.MEDIUM_GRAY, 1));

        // Footer panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(ModernTheme.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        footerPanel.add(statsPanel, BorderLayout.WEST);

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAssignments();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });

        clubFilterCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyClubFilter();
            }
        });
    }

    private void loadAssignments() {
        try {
            // Load clubs for filter
            loadClubFilter();

            // Load student data
            List<User> students = userDAO.getGrade9StudentsWithClubAssignments();
            List<Club> clubs = clubDAO.getAllClubs();
            Map<Integer, String> clubMap = new HashMap<>();

            for (Club club : clubs) {
                clubMap.put(club.getId(), club.getName());
            }

            // Clear existing data
            tableModel.setRowCount(0);

            // Populate table
            for (User student : students) {
                String clubName = "Unassigned";
                if (student.getAssignedClubId() != null) {
                    clubName = clubMap.get(student.getAssignedClubId());
                    if (clubName == null) {
                        clubName = "Unknown Club";
                    }
                }

                String lastLogin = "Never";
                if (student.getLastLogin() != null) {
                    lastLogin = student.getLastLogin().toString().substring(0, 16);
                }

                Object[] rowData = {
                    student.getFullName(),
                    student.getUsername(),
                    student.getEmail(),
                    clubName,
                    student.isFirstLoginCompleted() ? "Yes" : "No",
                    lastLogin
                };
                tableModel.addRow(rowData);
            }

            // Update statistics
            updateStatistics(students, clubMap);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading club assignments: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadClubFilter() throws SQLException {
        List<Club> clubs = clubDAO.getAllClubs();

        // Clear existing items except the first two
        while (clubFilterCombo.getItemCount() > 2) {
            clubFilterCombo.removeItemAt(2);
        }

        // Add club names
        for (Club club : clubs) {
            clubFilterCombo.addItem(club.getName());
        }
    }

    private void applyClubFilter() {
        String selectedFilter = (String) clubFilterCombo.getSelectedItem();
        TableRowSorter<DefaultTableModel> sorter =
            (TableRowSorter<DefaultTableModel>) assignmentsTable.getRowSorter();

        if ("All Clubs".equals(selectedFilter)) {
            sorter.setRowFilter(null);
        } else if ("Unassigned Students".equals(selectedFilter)) {
            sorter.setRowFilter(RowFilter.regexFilter("Unassigned", 3)); // Club column
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("^" + selectedFilter + "$", 3));
        }
    }

    private void updateStatistics(List<User> students, Map<Integer, String> clubMap) {
        int totalStudents = students.size();
        int assignedStudents = 0;
        int completedFirstLogin = 0;
        Map<String, Integer> clubCounts = new HashMap<>();

        for (User student : students) {
            if (student.getAssignedClubId() != null) {
                assignedStudents++;
                String clubName = clubMap.get(student.getAssignedClubId());
                if (clubName != null) {
                    clubCounts.put(clubName, clubCounts.getOrDefault(clubName, 0) + 1);
                }
            }
            if (student.isFirstLoginCompleted()) {
                completedFirstLogin++;
            }
        }

        int unassignedStudents = totalStudents - assignedStudents;

        String statsText = String.format(
            "Total: %d students | Assigned: %d | Unassigned: %d | Completed First Login: %d",
            totalStudents, assignedStudents, unassignedStudents, completedFirstLogin
        );

        statsLabel.setText(statsText);
    }

    private void exportToCSV() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Club Assignments");
            fileChooser.setSelectedFile(new java.io.File("grade9_club_assignments.csv"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {
                    // Write header
                    writer.println("Student Name,Username,Email,Assigned Club,First Login Completed,Last Login");

                    // Write data
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        StringBuilder line = new StringBuilder();
                        for (int j = 0; j < tableModel.getColumnCount(); j++) {
                            if (j > 0) line.append(",");
                            String value = tableModel.getValueAt(i, j).toString();
                            // Escape commas in values
                            if (value.contains(",")) {
                                value = "\"" + value + "\"";
                            }
                            line.append(value);
                        }
                        writer.println(line.toString());
                    }

                    JOptionPane.showMessageDialog(this,
                        "Club assignments exported successfully to:\n" + fileToSave.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (java.io.IOException e) {
                    JOptionPane.showMessageDialog(this,
                        "Error writing to file: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error during export: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}