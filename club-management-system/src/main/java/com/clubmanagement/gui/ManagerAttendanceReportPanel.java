package com.clubmanagement.gui;

import com.clubmanagement.dao.AttendanceDAO;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Attendance;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only attendance report panel for managers with Excel export functionality
 */
public class ManagerAttendanceReportPanel extends JPanel {
    private AuthenticationService authService;
    private AttendanceDAO attendanceDAO;
    private ClubDAO clubDAO;
    private UserDAO userDAO;

    // UI Components
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> clubFilterCombo;
    private JComboBox<String> dateFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JButton refreshButton;
    private JButton exportExcelButton;
    private JButton backToDashboardButton;
    private JLabel statusLabel;
    private JLabel summaryLabel;

    // Callback for navigation
    private Runnable backToDashboardCallback;

    // Data
    private List<Attendance> allAttendance;
    private Map<Integer, String> clubNamesMap;
    private Map<Integer, String> userNamesMap;

    public ManagerAttendanceReportPanel(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceDAO = new AttendanceDAO();
        this.clubDAO = new ClubDAO();
        this.userDAO = new UserDAO();
        this.clubNamesMap = new HashMap<>();
        this.userNamesMap = new HashMap<>();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    private void initializeComponents() {
        // Create table model
        String[] columnNames = {"Date", "Student", "Club", "Status", "Marked By", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table
            }
        };

        // Create table
        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(25);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        attendanceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        attendanceTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Set custom renderer for status column
        attendanceTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        // Create filter components
        clubFilterCombo = new JComboBox<>();
        clubFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        dateFilterCombo = new JComboBox<>();
        dateFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateFilterCombo.addItem("Today");
        dateFilterCombo.addItem("This Week");
        dateFilterCombo.addItem("This Month");
        dateFilterCombo.addItem("All Time");

        statusFilterCombo = new JComboBox<>();
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilterCombo.addItem("All Status");
        statusFilterCombo.addItem("Present");
        statusFilterCombo.addItem("Absent");
        statusFilterCombo.addItem("Late");
        statusFilterCombo.addItem("Excused");

        // Create buttons
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(ModernTheme.PRIMARY_BLUE);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        exportExcelButton = new JButton("📊 Export to Excel");
        exportExcelButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportExcelButton.setBackground(new Color(34, 139, 34)); // Green
        exportExcelButton.setForeground(Color.WHITE);
        exportExcelButton.setFocusPainted(false);

        backToDashboardButton = new JButton("🏠 Back to Dashboard");
        backToDashboardButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backToDashboardButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        backToDashboardButton.setForeground(Color.WHITE);
        backToDashboardButton.setFocusPainted(false);
        backToDashboardButton.setToolTipText("Return to main dashboard");

        // Create labels
        statusLabel = ModernTheme.createBodyLabel("Loading attendance data...");
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

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Attendance Records"));
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
        JLabel titleLabel = ModernTheme.createTitleLabel("Attendance Reports");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlsPanel.setBackground(ModernTheme.WHITE);

        // Filter controls
        JLabel clubLabel = new JLabel("Club:");
        clubLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsPanel.add(clubLabel);
        controlsPanel.add(clubFilterCombo);

        JLabel dateLabel = new JLabel("Period:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsPanel.add(dateLabel);
        controlsPanel.add(dateFilterCombo);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsPanel.add(statusLabel);
        controlsPanel.add(statusFilterCombo);

        // Action buttons
        controlsPanel.add(refreshButton);
        controlsPanel.add(exportExcelButton);
        controlsPanel.add(backToDashboardButton);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
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

        exportExcelButton.addActionListener(new ActionListener() {
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
                        ManagerAttendanceReportPanel.this,
                        "Use the navigation toolbar above to return to the dashboard.",
                        "Navigation Info",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Filter change listeners
        clubFilterCombo.addActionListener(e -> applyFilters());
        dateFilterCombo.addActionListener(e -> applyFilters());
        statusFilterCombo.addActionListener(e -> applyFilters());
    }

    private void loadData() {
        statusLabel.setText("Loading attendance data...");
        refreshButton.setEnabled(false);
        exportExcelButton.setEnabled(false);

        // Load data in a simpler way to avoid UI blocking
        new Thread(() -> {
            try {
                // Load attendance data
                List<Attendance> tempAllAttendance = attendanceDAO.getAllAttendance();

                // Load club names
                List<Club> clubs = clubDAO.getAllClubs();
                Map<Integer, String> tempClubNamesMap = new HashMap<>();
                for (Club club : clubs) {
                    tempClubNamesMap.put(club.getId(), club.getName());
                }

                // Load user names
                List<User> users = userDAO.getAllUsers();
                Map<Integer, String> tempUserNamesMap = new HashMap<>();
                for (User user : users) {
                    tempUserNamesMap.put(user.getId(), user.getFullName() != null ? user.getFullName() : user.getUsername());
                }

                // Update UI on EDT
                SwingUtilities.invokeLater(() -> {
                    allAttendance = tempAllAttendance;
                    clubNamesMap.clear();
                    clubNamesMap.putAll(tempClubNamesMap);
                    userNamesMap.clear();
                    userNamesMap.putAll(tempUserNamesMap);

                    updateClubFilter();
                    updateTable();
                    refreshButton.setEnabled(true);
                    exportExcelButton.setEnabled(true);
                    statusLabel.setText("Data loaded successfully");
                    statusLabel.setForeground(ModernTheme.DARK_GRAY);
                });

            } catch (SQLException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error loading data: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    refreshButton.setEnabled(true);
                    exportExcelButton.setEnabled(true);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void updateClubFilter() {
        clubFilterCombo.removeAllItems();
        clubFilterCombo.addItem("All Clubs");
        for (String clubName : clubNamesMap.values()) {
            clubFilterCombo.addItem(clubName);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);

        if (allAttendance == null) {
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        int totalRecords = 0;
        int presentCount = 0;

        for (Attendance attendance : allAttendance) {
            String studentName = userNamesMap.getOrDefault(attendance.getStudentId(), "Unknown");
            String clubName = clubNamesMap.getOrDefault(attendance.getClubId(), "Unknown");
            String markedByName = userNamesMap.getOrDefault(attendance.getMarkedById(), "System");

            Object[] rowData = {
                attendance.getSessionDate().format(dateFormatter),
                studentName,
                clubName,
                attendance.getStatus().toString(),
                markedByName,
                attendance.getNotes() != null ? attendance.getNotes() : ""
            };

            tableModel.addRow(rowData);
            totalRecords++;
            if (attendance.getStatus().toString().equals("PRESENT")) {
                presentCount++;
            }
        }

        // Update summary
        double attendanceRate = totalRecords > 0 ? (double) presentCount / totalRecords * 100 : 0;
        summaryLabel.setText(String.format("Total: %d records | Present: %d | Attendance Rate: %.1f%%",
                                          totalRecords, presentCount, attendanceRate));
    }

    private void applyFilters() {
        // This would implement filtering logic
        // For now, just refresh the table
        updateTable();
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Attendance Report");
        fileChooser.setSelectedFile(new java.io.File("attendance_report_" +
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write header
                writer.append("Date,Student,Club,Status,Marked By,Notes\n");

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
                    "Attendance report exported successfully to:\n" + fileToSave.getAbsolutePath(),
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

    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String status = value.toString();
                switch (status) {
                    case "PRESENT":
                        setForeground(new Color(34, 139, 34)); // Green
                        setText("✅ Present");
                        break;
                    case "ABSENT":
                        setForeground(new Color(220, 20, 60)); // Red
                        setText("❌ Absent");
                        break;
                    case "LATE":
                        setForeground(new Color(255, 165, 0)); // Orange
                        setText("⏰ Late");
                        break;
                    case "EXCUSED":
                        setForeground(new Color(70, 130, 180)); // Blue
                        setText("📝 Excused");
                        break;
                    default:
                        setForeground(Color.BLACK);
                        setText(status);
                }
            }

            return this;
        }
    }

    /**
     * Set callback for back to dashboard action
     */
    public void setBackToDashboardCallback(Runnable callback) {
        this.backToDashboardCallback = callback;
    }

    /**
     * Refresh the panel data
     */
    public void refreshPanel() {
        loadData();
    }
}