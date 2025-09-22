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
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Read-only club assignments view panel for managers
 */
public class ManagerClubAssignmentsViewPanel extends JPanel {
    private AuthenticationService authService;
    private UserDAO userDAO;
    private ClubDAO clubDAO;
    private ClubAllocationDAO clubAllocationDAO;
    private Runnable backToDashboardCallback;

    // UI Components
    private JTable assignmentsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> clubFilterCombo;
    private JComboBox<String> gradeFilterCombo;
    private JTextField searchField;
    private JButton refreshButton;
    private JButton exportButton;
    private JButton backToDashboardButton;
    private JLabel statusLabel;
    private JLabel summaryLabel;

    // Data
    private List<User> allStudents;
    private Map<Integer, String> clubNamesMap;
    private Map<Integer, Integer> studentClubMap;

    public ManagerClubAssignmentsViewPanel(AuthenticationService authService) {
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

    /**
     * Set callback for back to dashboard action
     */
    public void setBackToDashboardCallback(Runnable callback) {
        this.backToDashboardCallback = callback;
    }

    private void initializeComponents() {
        // Create table model
        String[] columnNames = {"Student ID", "Username", "Full Name", "Grade", "Email", "Assigned Club", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table
            }
        };

        // Create table
        assignmentsTable = new JTable(tableModel);
        assignmentsTable.setRowHeight(25);
        assignmentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        assignmentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        assignmentsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Enable table sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        assignmentsTable.setRowSorter(sorter);

        // Create filter components
        clubFilterCombo = new JComboBox<>();
        clubFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        gradeFilterCombo = new JComboBox<>();
        gradeFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gradeFilterCombo.addItem("All Grades");
        gradeFilterCombo.addItem("Grade 9");
        gradeFilterCombo.addItem("Grade 11");

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Create buttons
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(ModernTheme.PRIMARY_BLUE);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        exportButton = new JButton("📊 Export to Excel");
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportButton.setBackground(new Color(34, 139, 34)); // Green
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);

        backToDashboardButton = new JButton("🏠 Back to Dashboard");
        backToDashboardButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backToDashboardButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        backToDashboardButton.setForeground(Color.WHITE);
        backToDashboardButton.setFocusPainted(false);
        backToDashboardButton.setToolTipText("Return to main dashboard");

        // Create labels
        statusLabel = ModernTheme.createBodyLabel("Loading assignment data...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        summaryLabel = ModernTheme.createBodyLabel("");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
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

        JScrollPane scrollPane = new JScrollPane(assignmentsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Club Assignments"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with status and summary
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = ModernTheme.createTitleLabel("Club Assignments Overview");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(ModernTheme.WHITE);
        JLabel infoLabel = ModernTheme.createBodyLabel("📋 Students self-select their clubs. This is a read-only view for monitoring assignments.");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(infoLabel);

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlsPanel.setBackground(ModernTheme.WHITE);

        // Search controls
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsPanel.add(searchLabel);
        controlsPanel.add(searchField);

        // Filter controls
        JLabel gradeLabel = new JLabel("Grade:");
        gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsPanel.add(gradeLabel);
        controlsPanel.add(gradeFilterCombo);

        JLabel clubLabel = new JLabel("Club:");
        clubLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsPanel.add(clubLabel);
        controlsPanel.add(clubFilterCombo);

        // Action buttons
        controlsPanel.add(refreshButton);
        controlsPanel.add(exportButton);
        controlsPanel.add(backToDashboardButton);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(infoPanel, BorderLayout.CENTER);
        headerPanel.add(controlsPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ModernTheme.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(summaryLabel, BorderLayout.EAST);

        return bottomPanel;
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel();
            }
        });

        backToDashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (backToDashboardCallback != null) {
                    backToDashboardCallback.run();
                } else {
                    // Fallback: show message if no callback is set
                    JOptionPane.showMessageDialog(
                        ManagerClubAssignmentsViewPanel.this,
                        "Use the navigation toolbar above to return to the dashboard.",
                        "Navigation Info",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Search functionality
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });

        // Filter change listeners
        gradeFilterCombo.addActionListener(e -> filterTable());
        clubFilterCombo.addActionListener(e -> filterTable());
    }

    private void loadData() {
        statusLabel.setText("Loading assignment data...");
        refreshButton.setEnabled(false);
        exportButton.setEnabled(false);

        // Load data in a simpler way to avoid UI blocking
        new Thread(() -> {
            try {
                // Load all students (Grade 9 and Grade 11)
                List<User> grade9Students = userDAO.getUsersByRole(UserRole.GRADE_9);
                List<User> grade11Students = userDAO.getUsersByRole(UserRole.GRADE_11);
                List<User> tempAllStudents = new java.util.ArrayList<>();
                tempAllStudents.addAll(grade9Students);
                tempAllStudents.addAll(grade11Students);

                // Load club names
                List<Club> clubs = clubDAO.getAllClubs();
                Map<Integer, String> tempClubNamesMap = new HashMap<>();
                for (Club club : clubs) {
                    tempClubNamesMap.put(club.getId(), club.getName());
                }

                // Load club allocations
                Map<Integer, Integer> tempStudentClubMap = new HashMap<>();
                List<ClubAllocation> allocations = clubAllocationDAO.getAllAllocations();
                for (ClubAllocation allocation : allocations) {
                    tempStudentClubMap.put(allocation.getStudentId(), allocation.getClubId());
                }

                // Update UI on EDT
                SwingUtilities.invokeLater(() -> {
                    allStudents = tempAllStudents;
                    clubNamesMap.clear();
                    clubNamesMap.putAll(tempClubNamesMap);
                    studentClubMap.clear();
                    studentClubMap.putAll(tempStudentClubMap);

                    updateClubFilter();
                    updateTable();
                    refreshButton.setEnabled(true);
                    exportButton.setEnabled(true);
                    statusLabel.setText("Data loaded successfully");
                    statusLabel.setForeground(ModernTheme.DARK_GRAY);
                });

            } catch (SQLException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error loading data: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    refreshButton.setEnabled(true);
                    exportButton.setEnabled(true);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void updateClubFilter() {
        clubFilterCombo.removeAllItems();
        clubFilterCombo.addItem("All Clubs");
        clubFilterCombo.addItem("Unassigned Students");
        for (String clubName : clubNamesMap.values()) {
            clubFilterCombo.addItem(clubName);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);

        if (allStudents == null) {
            return;
        }

        int totalStudents = 0;
        int assignedStudents = 0;
        Map<String, Integer> clubCounts = new HashMap<>();

        for (User student : allStudents) {
            String clubName = "Not Assigned";
            String status = "❌ Unassigned";

            Integer clubId = studentClubMap.get(student.getId());
            if (clubId != null && clubNamesMap.containsKey(clubId)) {
                clubName = clubNamesMap.get(clubId);
                status = "✅ Assigned";
                assignedStudents++;
                clubCounts.put(clubName, clubCounts.getOrDefault(clubName, 0) + 1);
            }

            String grade = student.getRole() == UserRole.GRADE_9 ? "Grade 9" : "Grade 11";

            Object[] rowData = {
                student.getId(),
                student.getUsername(),
                student.getFullName() != null ? student.getFullName() : "N/A",
                grade,
                student.getEmail() != null ? student.getEmail() : "N/A",
                clubName,
                status
            };

            tableModel.addRow(rowData);
            totalStudents++;
        }

        // Update summary
        double assignmentRate = totalStudents > 0 ? (double) assignedStudents / totalStudents * 100 : 0;
        summaryLabel.setText(String.format("Total: %d students | Assigned: %d | Assignment Rate: %.1f%%",
                                          totalStudents, assignedStudents, assignmentRate));
    }

    private void filterTable() {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) assignmentsTable.getRowSorter();

        String searchText = searchField.getText().trim().toLowerCase();
        String selectedGrade = (String) gradeFilterCombo.getSelectedItem();
        String selectedClub = (String) clubFilterCombo.getSelectedItem();

        if (searchText.isEmpty() && "All Grades".equals(selectedGrade) && "All Clubs".equals(selectedClub)) {
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

                // Check grade filter
                boolean matchesGrade = "All Grades".equals(selectedGrade) ||
                                     selectedGrade.equals(entry.getStringValue(3));

                // Check club filter
                boolean matchesClub = true;
                if (!"All Clubs".equals(selectedClub)) {
                    String clubValue = entry.getStringValue(5); // Club column
                    if ("Unassigned Students".equals(selectedClub)) {
                        matchesClub = "Not Assigned".equals(clubValue);
                    } else {
                        matchesClub = selectedClub.equals(clubValue);
                    }
                }

                return matchesSearch && matchesGrade && matchesClub;
            }
        });
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Club Assignments Report");
        fileChooser.setSelectedFile(new java.io.File("club_assignments_" +
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write header
                writer.append("Student ID,Username,Full Name,Grade,Email,Assigned Club,Status\n");

                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        writer.append(value != null ? value.toString() : "");
                        if (j < tableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }

                JOptionPane.showMessageDialog(this,
                    "Club assignments report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);

                statusLabel.setText("Report exported to: " + fileToSave.getName());

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting file: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Refresh the panel data
     */
    public void refreshPanel() {
        loadData();
    }
}