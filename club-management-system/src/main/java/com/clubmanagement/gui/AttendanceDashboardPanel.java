package com.clubmanagement.gui;

import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Attendance;
import com.clubmanagement.models.Attendance.AttendanceStatus;
import com.clubmanagement.models.Club;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AttendanceService;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDashboardPanel extends JPanel {
    private AuthenticationService authService;
    private AttendanceService attendanceService;
    private ClubDAO clubDAO;

    // Header components
    private JLabel clubNameLabel;
    private JLabel sessionDateLabel;
    private JLabel statsLabel;

    // Attendance marking components
    private JTable attendanceTable;
    private DefaultTableModel attendanceTableModel;
    private JButton markAllPresentButton;
    private JButton markAllAbsentButton;
    private JButton saveAttendanceButton;

    // History components
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JComboBox<String> dateFilterCombo;
    private JComboBox<String> memberFilterCombo;
    private JButton exportButton;
    private JButton refreshButton;

    private int clubId;
    private List<User> clubMembers;
    private LocalDate currentSessionDate;

    public AttendanceDashboardPanel(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceService = new AttendanceService();
        this.clubDAO = new ClubDAO();
        this.currentSessionDate = LocalDate.now();

        // Get the current user's club
        User currentUser = authService.getCurrentUser();
        if (currentUser.getAssignedClubId() != null) {
            this.clubId = currentUser.getAssignedClubId();
        } else {
            showNoClubMessage();
            return;
        }

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    private void showNoClubMessage() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);

        JLabel messageLabel = ModernTheme.createTitleLabel("No Club Assignment");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel detailLabel = ModernTheme.createBodyLabel("You are not assigned to any club. Please contact the administrator.");
        detailLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(ModernTheme.WHITE);
        messagePanel.add(messageLabel);
        messagePanel.add(Box.createVerticalStrut(10));
        messagePanel.add(detailLabel);

        add(messagePanel, BorderLayout.CENTER);
    }

    private void initializeComponents() {
        // Header components
        clubNameLabel = ModernTheme.createTitleLabel("Loading...");
        sessionDateLabel = ModernTheme.createHeadingLabel("Today: " +
            currentSessionDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        statsLabel = ModernTheme.createBodyLabel("Loading statistics...");

        // Attendance marking table
        String[] attendanceColumns = {"Student Name", "Status", "Actions"};
        attendanceTableModel = new DefaultTableModel(attendanceColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only Actions column is editable
            }
        };

        attendanceTable = new JTable(attendanceTableModel);
        attendanceTable.setRowHeight(40);
        attendanceTable.setFont(ModernTheme.BODY_FONT);
        attendanceTable.getTableHeader().setFont(ModernTheme.HEADING_FONT);
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderers
        attendanceTable.getColumnModel().getColumn(1).setCellRenderer(new StatusCellRenderer());
        attendanceTable.getColumnModel().getColumn(2).setCellRenderer(new ActionButtonRenderer());
        attendanceTable.getColumnModel().getColumn(2).setCellEditor(new ActionButtonEditor());

        // Column widths
        attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Name
        attendanceTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Status
        attendanceTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Actions

        // Buttons for bulk actions
        markAllPresentButton = ModernTheme.createPrimaryButton("Mark All Present");
        markAllAbsentButton = ModernTheme.createSecondaryButton("Mark All Absent");
        saveAttendanceButton = ModernTheme.createPrimaryButton("Save Attendance");

        // History table
        String[] historyColumns = {"Date", "Student", "Status", "Marked By"};
        historyTableModel = new DefaultTableModel(historyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        historyTable = new JTable(historyTableModel);
        historyTable.setRowHeight(30);
        historyTable.setFont(ModernTheme.BODY_FONT);
        historyTable.getTableHeader().setFont(ModernTheme.HEADING_FONT);
        historyTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        // Filter components
        dateFilterCombo = new JComboBox<>();
        memberFilterCombo = new JComboBox<>();
        exportButton = ModernTheme.createSecondaryButton("Export CSV");
        refreshButton = ModernTheme.createSecondaryButton("Refresh");

        setupFilters();
    }

    private void setupFilters() {
        // Date filter options
        dateFilterCombo.addItem("Today");
        dateFilterCombo.addItem("This Week");
        dateFilterCombo.addItem("This Month");
        dateFilterCombo.addItem("All Time");

        // Member filter will be populated with actual members
        memberFilterCombo.addItem("All Members");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create main content with tabbed interface
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ModernTheme.HEADING_FONT);

        // Attendance Marking Tab
        JPanel markingPanel = createAttendanceMarkingPanel();
        tabbedPane.addTab("📝 Mark Attendance", markingPanel);

        // Attendance History Tab
        JPanel historyPanel = createAttendanceHistoryPanel();
        tabbedPane.addTab("📊 Attendance History", historyPanel);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(ModernTheme.WHITE);

        clubNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sessionDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(clubNameLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(sessionDateLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(statsLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createAttendanceMarkingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = ModernTheme.createHeadingLabel("Mark Today's Attendance");
        panel.add(title, BorderLayout.NORTH);

        // Table
        JScrollPane tableScrollPane = new JScrollPane(attendanceTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        tableScrollPane.setPreferredSize(new Dimension(600, 300));
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.add(markAllPresentButton);
        buttonPanel.add(markAllAbsentButton);
        buttonPanel.add(saveAttendanceButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAttendanceHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(ModernTheme.WHITE);
        filterPanel.add(ModernTheme.createBodyLabel("Filter by Date:"));
        filterPanel.add(dateFilterCombo);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(ModernTheme.createBodyLabel("Filter by Student:"));
        filterPanel.add(memberFilterCombo);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(exportButton);
        filterPanel.add(refreshButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // History table
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        historyScrollPane.setPreferredSize(new Dimension(700, 400));
        panel.add(historyScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupEventHandlers() {
        markAllPresentButton.addActionListener(e -> markAllStudents(AttendanceStatus.PRESENT));
        markAllAbsentButton.addActionListener(e -> markAllStudents(AttendanceStatus.ABSENT));
        saveAttendanceButton.addActionListener(e -> saveAttendance());

        exportButton.addActionListener(e -> exportAttendance());
        refreshButton.addActionListener(e -> refreshData());

        dateFilterCombo.addActionListener(e -> filterHistory());
        memberFilterCombo.addActionListener(e -> filterHistory());
    }

    private void loadData() {
        try {
            // Load club info
            Club club = clubDAO.getClubById(clubId);
            if (club != null) {
                clubNameLabel.setText("📋 " + club.getName() + " - Attendance Dashboard");
            }

            // Load club members
            clubMembers = attendanceService.getClubMembers(clubId);

            // Populate member filter
            memberFilterCombo.removeAllItems();
            memberFilterCombo.addItem("All Members");
            for (User member : clubMembers) {
                memberFilterCombo.addItem(member.getFullName());
            }

            // Load today's attendance
            loadTodayAttendance();

            // Load history
            loadAttendanceHistory();

            // Update statistics
            updateStatistics();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTodayAttendance() throws SQLException {
        attendanceTableModel.setRowCount(0);
        List<Attendance> todayAttendance = attendanceService.getClubAttendance(clubId, currentSessionDate);

        for (User student : clubMembers) {
            // Find existing attendance for this student
            Attendance existingAttendance = todayAttendance.stream()
                .filter(a -> a.getStudentId() == student.getId())
                .findFirst()
                .orElse(null);

            AttendanceStatus status = existingAttendance != null ?
                existingAttendance.getStatus() : AttendanceStatus.ABSENT;

            Object[] rowData = {
                student.getFullName(),
                status,
                "Actions" // Placeholder for action buttons
            };
            attendanceTableModel.addRow(rowData);
        }
    }

    private void loadAttendanceHistory() throws SQLException {
        historyTableModel.setRowCount(0);

        LocalDate startDate = getFilterStartDate();
        List<Attendance> history = attendanceService.getClubAttendanceRange(clubId, startDate, LocalDate.now());

        String selectedMember = (String) memberFilterCombo.getSelectedItem();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        for (Attendance attendance : history) {
            // Apply member filter
            if (!"All Members".equals(selectedMember) &&
                !selectedMember.equals(attendance.getStudentName())) {
                continue;
            }

            Object[] rowData = {
                attendance.getSessionDate().format(dateFormatter),
                attendance.getStudentName(),
                attendance.getStatus(),
                attendance.getMarkerName()
            };
            historyTableModel.addRow(rowData);
        }

        if (history.isEmpty()) {
            Object[] noDataRow = {"No records found", "", "", ""};
            historyTableModel.addRow(noDataRow);
        }
    }

    private LocalDate getFilterStartDate() {
        String filter = (String) dateFilterCombo.getSelectedItem();
        LocalDate today = LocalDate.now();

        return switch (filter) {
            case "Today" -> today;
            case "This Week" -> today.minusDays(7);
            case "This Month" -> today.withDayOfMonth(1);
            default -> today.minusMonths(6); // All Time - last 6 months
        };
    }

    private void updateStatistics() {
        try {
            String summaryText = attendanceService.getAttendanceSummaryText(clubId);
            statsLabel.setText("📈 " + summaryText);
        } catch (SQLException e) {
            statsLabel.setText("📈 Unable to load statistics");
        }
    }

    private void markAllStudents(AttendanceStatus status) {
        for (int i = 0; i < attendanceTableModel.getRowCount(); i++) {
            attendanceTableModel.setValueAt(status, i, 1);
        }
        attendanceTable.revalidate();
        attendanceTable.repaint();
    }

    private void saveAttendance() {
        try {
            User currentUser = authService.getCurrentUser();
            List<Attendance> attendanceToSave = new ArrayList<>();

            for (int i = 0; i < attendanceTableModel.getRowCount(); i++) {
                User student = clubMembers.get(i);
                AttendanceStatus status = (AttendanceStatus) attendanceTableModel.getValueAt(i, 1);

                // Mark individual attendance
                attendanceService.markStudentAttendance(
                    student.getId(),
                    clubId,
                    currentUser.getId(),
                    currentSessionDate,
                    status,
                    null // No notes in simplified interface
                );
            }

            JOptionPane.showMessageDialog(this,
                "✅ Attendance saved successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

            refreshData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "❌ Error saving attendance: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportAttendance() {
        try {
            LocalDate startDate = getFilterStartDate();
            String csvData = attendanceService.generateAttendanceCSV(clubId, startDate, LocalDate.now());

            // Save to file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("attendance_export_" +
                currentSessionDate.format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + ".csv"));

            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                    writer.write(csvData);
                    JOptionPane.showMessageDialog(this,
                        "📁 Attendance exported successfully to: " + file.getName(),
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ Error exporting attendance: " + e.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterHistory() {
        try {
            loadAttendanceHistory();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error filtering history: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        loadData();
    }

    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof AttendanceStatus) {
                AttendanceStatus status = (AttendanceStatus) value;
                setText(status.toString());

                // Set background color based on status
                if (!isSelected) {
                    switch (status) {
                        case PRESENT -> setBackground(new Color(220, 255, 220)); // Light green
                        case ABSENT -> setBackground(new Color(255, 220, 220)); // Light red
                        default -> setBackground(Color.WHITE);
                    }
                }
            }

            return c;
        }
    }

    // Custom renderer for action buttons
    private class ActionButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton presentBtn, absentBtn;

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            setBackground(Color.WHITE);

            presentBtn = new JButton("✅");
            absentBtn = new JButton("❌");

            presentBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            absentBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            presentBtn.setPreferredSize(new Dimension(40, 30));
            absentBtn.setPreferredSize(new Dimension(40, 30));

            add(presentBtn);
            add(absentBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Custom editor for action buttons
    private class ActionButtonEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private ActionButtonRenderer renderer;
        private int editingRow;

        public ActionButtonEditor() {
            renderer = new ActionButtonRenderer();

            renderer.presentBtn.addActionListener(e -> updateStatus(AttendanceStatus.PRESENT));
            renderer.absentBtn.addActionListener(e -> updateStatus(AttendanceStatus.ABSENT));
        }

        private void updateStatus(AttendanceStatus status) {
            attendanceTableModel.setValueAt(status, editingRow, 1);
            fireEditingStopped();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            editingRow = row;
            return renderer;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}