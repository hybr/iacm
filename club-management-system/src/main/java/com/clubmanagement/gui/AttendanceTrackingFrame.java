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
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttendanceTrackingFrame extends JFrame {
    private AuthenticationService authService;
    private AttendanceService attendanceService;
    private ClubDAO clubDAO;

    private JLabel clubNameLabel;
    private JLabel sessionDateLabel;
    private JLabel sessionStatsLabel;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JButton markAllPresentButton;
    private JButton saveAttendanceButton;
    private JButton exportButton;
    private JButton refreshButton;
    private JButton closeButton;

    private LocalDate currentSessionDate;
    private int clubId;
    private List<User> clubMembers;
    private List<Attendance> currentAttendance;

    public AttendanceTrackingFrame(AuthenticationService authService) {
        this.authService = authService;
        this.attendanceService = new AttendanceService();
        this.clubDAO = new ClubDAO();
        this.currentSessionDate = LocalDate.now();

        // Get the current user's club
        User currentUser = authService.getCurrentUser();
        if (currentUser.getAssignedClubId() != null) {
            this.clubId = currentUser.getAssignedClubId();
        } else {
            JOptionPane.showMessageDialog(this,
                "You are not assigned to any club. Please contact the administrator.",
                "No Club Assignment", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
        loadClubData();
        loadAttendanceData();
    }

    private void initializeComponents() {
        clubNameLabel = ModernTheme.createTitleLabel("Loading...");
        sessionDateLabel = ModernTheme.createHeadingLabel("Session Date: " +
            currentSessionDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        sessionStatsLabel = ModernTheme.createBodyLabel("Loading attendance statistics...");

        // Table setup
        String[] columnNames = {"Student Name", "Status", "Notes", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3; // Only notes and actions are editable
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(40);
        attendanceTable.setFont(ModernTheme.BODY_FONT);
        attendanceTable.getTableHeader().setFont(ModernTheme.HEADING_FONT);
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer for status column
        attendanceTable.getColumnModel().getColumn(1).setCellRenderer(new StatusCellRenderer());

        // Custom renderer for actions column
        attendanceTable.getColumnModel().getColumn(3).setCellRenderer(new ActionCellRenderer());
        attendanceTable.getColumnModel().getColumn(3).setCellEditor(new ActionCellEditor());

        // Set column widths
        attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Student Name
        attendanceTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Status
        attendanceTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Notes
        attendanceTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Actions

        // Buttons
        markAllPresentButton = ModernTheme.createSecondaryButton("Mark All Present");
        saveAttendanceButton = ModernTheme.createPrimaryButton("Save Attendance");
        exportButton = ModernTheme.createSecondaryButton("Export CSV");
        refreshButton = ModernTheme.createSecondaryButton("Refresh");
        closeButton = ModernTheme.createSecondaryButton("Close");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernTheme.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = ModernTheme.createCardPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(ModernTheme.WHITE);

        clubNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sessionDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sessionStatsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerContent.add(clubNameLabel);
        headerContent.add(Box.createVerticalStrut(5));
        headerContent.add(sessionDateLabel);
        headerContent.add(Box.createVerticalStrut(10));
        headerContent.add(sessionStatsLabel);

        headerPanel.add(headerContent, BorderLayout.CENTER);

        // Table panel
        JPanel tablePanel = ModernTheme.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tableTitle = ModernTheme.createHeadingLabel("Attendance Tracking");
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        JScrollPane tableScrollPane = new JScrollPane(attendanceTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        tableScrollPane.setPreferredSize(new Dimension(800, 400));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(ModernTheme.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        buttonPanel.add(markAllPresentButton);
        buttonPanel.add(saveAttendanceButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panels to main
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        markAllPresentButton.addActionListener(e -> markAllPresent());
        saveAttendanceButton.addActionListener(e -> saveAttendance());
        exportButton.addActionListener(e -> exportAttendance());
        refreshButton.addActionListener(e -> refreshData());
        closeButton.addActionListener(e -> dispose());
    }

    private void loadClubData() {
        try {
            Club club = clubDAO.getClubById(clubId);
            if (club != null) {
                clubNameLabel.setText(club.getName() + " - Attendance Tracking");
            }

            clubMembers = attendanceService.getClubMembers(clubId);
            updateSessionStats();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading club data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAttendanceData() {
        try {
            currentAttendance = attendanceService.getClubAttendance(clubId, currentSessionDate);
            updateTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading attendance data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);

        for (User student : clubMembers) {
            // Find existing attendance for this student
            Attendance existingAttendance = currentAttendance.stream()
                .filter(a -> a.getStudentId() == student.getId())
                .findFirst()
                .orElse(null);

            AttendanceStatus status = existingAttendance != null ?
                existingAttendance.getStatus() : AttendanceStatus.ABSENT;
            String notes = existingAttendance != null ?
                (existingAttendance.getNotes() != null ? existingAttendance.getNotes() : "") : "";

            Object[] rowData = {
                student.getFullName(),
                status,
                notes,
                "Actions" // Placeholder for action buttons
            };
            tableModel.addRow(rowData);
        }
    }

    private void updateSessionStats() {
        try {
            String statsText = attendanceService.getAttendanceSummaryText(clubId);
            sessionStatsLabel.setText(statsText);
        } catch (SQLException e) {
            sessionStatsLabel.setText("Unable to load statistics");
        }
    }

    private void markAllPresent() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(AttendanceStatus.PRESENT, i, 1);
        }
        updateSessionStats();
    }

    private void saveAttendance() {
        try {
            List<Attendance> attendanceToSave = new ArrayList<>();
            User currentUser = authService.getCurrentUser();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                User student = clubMembers.get(i);
                AttendanceStatus status = (AttendanceStatus) tableModel.getValueAt(i, 1);
                String notes = (String) tableModel.getValueAt(i, 2);

                Attendance attendance = new Attendance(
                    student.getId(),
                    clubId,
                    currentUser.getId(),
                    currentSessionDate,
                    status,
                    notes
                );
                attendanceToSave.add(attendance);
            }

            boolean success = attendanceService.markBulkAttendance(
                attendanceToSave.stream().mapToInt(Attendance::getStudentId).boxed().toList(),
                clubId,
                currentUser.getId(),
                currentSessionDate,
                AttendanceStatus.PRESENT // This will be overridden by individual markings
            );

            // Mark individual attendance
            for (Attendance attendance : attendanceToSave) {
                attendanceService.markStudentAttendance(
                    attendance.getStudentId(),
                    attendance.getClubId(),
                    attendance.getMarkedById(),
                    attendance.getSessionDate(),
                    attendance.getStatus(),
                    attendance.getNotes()
                );
            }

            JOptionPane.showMessageDialog(this,
                "Attendance saved successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

            refreshData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving attendance: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportAttendance() {
        try {
            LocalDate startDate = currentSessionDate.withDayOfMonth(1);
            LocalDate endDate = currentSessionDate.withDayOfMonth(currentSessionDate.lengthOfMonth());

            String csvData = attendanceService.generateAttendanceCSV(clubId, startDate, endDate);

            // Save to file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("attendance_" +
                currentSessionDate.format(DateTimeFormatter.ofPattern("yyyy_MM")) + ".csv"));

            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                    writer.write(csvData);
                    JOptionPane.showMessageDialog(this,
                        "Attendance exported successfully!",
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error exporting attendance: " + e.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        loadClubData();
        loadAttendanceData();
        updateSessionStats();
    }

    private void setupFrame() {
        setTitle("Attendance Tracking - Club Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(900, 700));
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(ModernTheme.LIGHT_GRAY);
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
                        case PRESENT -> setBackground(new Color(220, 255, 220));
                        case LATE -> setBackground(new Color(255, 255, 180));
                        case ABSENT -> setBackground(new Color(255, 220, 220));
                        case EXCUSED -> setBackground(new Color(220, 230, 255));
                    }
                }
            }

            return c;
        }
    }

    // Custom cell renderer for action buttons
    private class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private JButton presentBtn, lateBtn, absentBtn, excusedBtn;

        public ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
            setBackground(ModernTheme.WHITE);

            presentBtn = new JButton("✅");
            lateBtn = new JButton("⏱️");
            absentBtn = new JButton("❌");
            excusedBtn = new JButton("📝");

            presentBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            lateBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            absentBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            excusedBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

            presentBtn.setPreferredSize(new Dimension(30, 25));
            lateBtn.setPreferredSize(new Dimension(30, 25));
            absentBtn.setPreferredSize(new Dimension(30, 25));
            excusedBtn.setPreferredSize(new Dimension(30, 25));

            add(presentBtn);
            add(lateBtn);
            add(absentBtn);
            add(excusedBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Custom cell editor for action buttons
    private class ActionCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private ActionCellRenderer renderer;
        private int editingRow;

        public ActionCellEditor() {
            renderer = new ActionCellRenderer();

            renderer.presentBtn.addActionListener(e -> updateStatus(AttendanceStatus.PRESENT));
            renderer.lateBtn.addActionListener(e -> updateStatus(AttendanceStatus.LATE));
            renderer.absentBtn.addActionListener(e -> updateStatus(AttendanceStatus.ABSENT));
            renderer.excusedBtn.addActionListener(e -> updateStatus(AttendanceStatus.EXCUSED));
        }

        private void updateStatus(AttendanceStatus status) {
            tableModel.setValueAt(status, editingRow, 1);
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