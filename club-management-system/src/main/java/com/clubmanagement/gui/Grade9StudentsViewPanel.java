package com.clubmanagement.gui;

import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.dao.ClubAllocationDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.User;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.ClubAllocation;
import com.clubmanagement.models.User.UserRole;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel for Grade 11 students to view Grade 9 students and their club assignments
 */
public class Grade9StudentsViewPanel extends JPanel {
    private AuthenticationService authService;
    private UserDAO userDAO;
    private ClubDAO clubDAO;
    private ClubAllocationDAO clubAllocationDAO;

    // UI Components
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JLabel statusLabel;
    private JTextField searchField;
    private JComboBox<String> clubFilterComboBox;

    // Data
    private List<User> grade9Students;
    private Map<Integer, String> clubNamesMap;
    private Map<Integer, Integer> studentClubMap;

    public Grade9StudentsViewPanel(AuthenticationService authService) {
        this.authService = authService;
        this.userDAO = new UserDAO();
        this.clubDAO = new ClubDAO();
        this.clubAllocationDAO = new ClubAllocationDAO();
        this.clubNamesMap = new HashMap<>();
        this.studentClubMap = new HashMap<>();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    private void initializeComponents() {
        // Create table model with columns
        String[] columnNames = {"Student ID", "Username", "Full Name", "Email", "Assigned Club", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table with model
        studentsTable = new JTable(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.setRowHeight(25);
        studentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Enable table sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        studentsTable.setRowSorter(sorter);

        // Create other components
        refreshButton = new JButton("🔄 Refresh Data");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(ModernTheme.PRIMARY_BLUE);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        statusLabel = ModernTheme.createBodyLabel("Loading data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Club filter combo box
        clubFilterComboBox = new JComboBox<>();
        clubFilterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center panel with table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ModernTheme.WHITE);

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Grade 9 Students and Club Assignments"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ModernTheme.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = ModernTheme.createTitleLabel("Grade 9 Students Overview");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlsPanel.setBackground(ModernTheme.WHITE);

        // Search controls
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsPanel.add(searchLabel);
        controlsPanel.add(searchField);

        // Club filter
        JLabel filterLabel = new JLabel("Filter by Club:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsPanel.add(filterLabel);
        controlsPanel.add(clubFilterComboBox);

        // Refresh button
        controlsPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(controlsPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        // Search functionality
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });

        // Club filter functionality
        clubFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterTable();
            }
        });
    }

    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Loading data...");
            refreshButton.setEnabled(false);
        });

        // Load data in background thread to avoid blocking UI
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Load all Grade 9 students
                    grade9Students = userDAO.getUsersByRole(UserRole.GRADE_9);

                    // Load all clubs for mapping
                    List<Club> clubs = clubDAO.getAllClubs();
                    clubNamesMap.clear();
                    for (Club club : clubs) {
                        clubNamesMap.put(club.getId(), club.getName());
                    }

                    // Load club allocations for Grade 9 students
                    studentClubMap.clear();
                    for (User student : grade9Students) {
                        ClubAllocation allocation = clubAllocationDAO.getAllocationByStudentId(student.getId());
                        if (allocation != null) {
                            studentClubMap.put(student.getId(), allocation.getClubId());
                        }
                    }

                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error loading data: " + e.getMessage());
                        statusLabel.setForeground(Color.RED);
                    });
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    updateTable();
                    updateClubFilter();
                    refreshButton.setEnabled(true);
                    statusLabel.setText("Data loaded successfully. Total Grade 9 students: " + grade9Students.size());
                    statusLabel.setForeground(ModernTheme.DARK_GRAY);
                });
            }
        };

        worker.execute();
    }

    private void updateTable() {
        // Clear existing data
        tableModel.setRowCount(0);

        if (grade9Students == null) {
            return;
        }

        // Add data to table
        for (User student : grade9Students) {
            String clubName = "Not Assigned";
            String status = "❌ Unassigned";

            Integer clubId = studentClubMap.get(student.getId());
            if (clubId != null && clubNamesMap.containsKey(clubId)) {
                clubName = clubNamesMap.get(clubId);
                status = "✅ Assigned";
            }

            Object[] rowData = {
                student.getId(),
                student.getUsername(),
                student.getFullName() != null ? student.getFullName() : "N/A",
                student.getEmail() != null ? student.getEmail() : "N/A",
                clubName,
                status
            };

            tableModel.addRow(rowData);
        }
    }

    private void updateClubFilter() {
        clubFilterComboBox.removeAllItems();
        clubFilterComboBox.addItem("All Clubs");
        clubFilterComboBox.addItem("Unassigned Students");

        for (String clubName : clubNamesMap.values()) {
            clubFilterComboBox.addItem(clubName);
        }
    }

    private void filterTable() {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) studentsTable.getRowSorter();

        String searchText = searchField.getText().trim().toLowerCase();
        String selectedClub = (String) clubFilterComboBox.getSelectedItem();

        if (searchText.isEmpty() && "All Clubs".equals(selectedClub)) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                // Check search text filter
                boolean matchesSearch = searchText.isEmpty();
                if (!searchText.isEmpty()) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        String value = entry.getStringValue(i).toLowerCase();
                        if (value.contains(searchText)) {
                            matchesSearch = true;
                            break;
                        }
                    }
                }

                // Check club filter
                boolean matchesClub = true;
                if (!"All Clubs".equals(selectedClub)) {
                    String clubValue = entry.getStringValue(4); // Club column
                    if ("Unassigned Students".equals(selectedClub)) {
                        matchesClub = "Not Assigned".equals(clubValue);
                    } else {
                        matchesClub = selectedClub.equals(clubValue);
                    }
                }

                return matchesSearch && matchesClub;
            }
        });
    }

    /**
     * Refresh the panel data
     */
    public void refreshPanel() {
        loadData();
    }
}