package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.Proposal;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.services.ProposalService;
import com.clubmanagement.dao.UserDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProposalManagementPanel extends JPanel {

    private AuthenticationService authService;
    private ProposalService proposalService;
    private UserDAO userDAO;

    private JTable proposalTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilterCombo;
    private JButton acceptButton;
    private JButton rejectButton;
    private JButton refreshButton;
    private JButton backButton;
    private JLabel statusLabel;

    private Runnable backToDashboardCallback;

    public ProposalManagementPanel(AuthenticationService authService) {
        this.authService = authService;
        this.proposalService = new ProposalService();
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadProposals();
    }

    public void setBackToDashboardCallback(Runnable callback) {
        this.backToDashboardCallback = callback;
    }

    private void initializeComponents() {
        String[] columns = {"ID", "Student", "File Path", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        proposalTable = new JTable(tableModel);
        proposalTable.setRowHeight(25);
        proposalTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        proposalTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        proposalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proposalTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        statusFilterCombo = new JComboBox<>(new String[]{"All Status", "PENDING", "ACCEPTED", "REJECTED"});
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(ModernTheme.PRIMARY_BLUE);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        acceptButton = new JButton("Accept");
        acceptButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        acceptButton.setBackground(new Color(34, 139, 34));
        acceptButton.setForeground(Color.WHITE);
        acceptButton.setFocusPainted(false);
        acceptButton.setEnabled(false);

        rejectButton = new JButton("Reject");
        rejectButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rejectButton.setBackground(new Color(200, 50, 50));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFocusPainted(false);
        rejectButton.setEnabled(false);

        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        statusLabel = ModernTheme.createBodyLabel("Select a pending proposal to accept or reject it.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ModernTheme.WHITE);
        JScrollPane scrollPane = new JScrollPane(proposalTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Proposals"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = ModernTheme.createTitleLabel("Proposal Management");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controls.setBackground(ModernTheme.WHITE);
        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controls.add(filterLabel);
        controls.add(statusFilterCombo);
        controls.add(refreshButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(ModernTheme.WHITE);
        rightPanel.add(backButton);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(controls, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(ModernTheme.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(ModernTheme.WHITE);
        actionPanel.add(acceptButton);
        actionPanel.add(rejectButton);

        bottom.add(actionPanel, BorderLayout.WEST);
        bottom.add(statusLabel, BorderLayout.EAST);

        return bottom;
    }

    private void setupEventHandlers() {
        acceptButton.addActionListener(e -> updateStatus(Proposal.ProposalStatus.ACCEPTED));
        rejectButton.addActionListener(e -> updateStatus(Proposal.ProposalStatus.REJECTED));
        refreshButton.addActionListener(e -> loadProposals());

        statusFilterCombo.addActionListener(e -> applyFilter());

        proposalTable.getSelectionModel().addListSelectionListener(e -> {
            boolean pending = isPendingSelected();
            acceptButton.setEnabled(pending);
            rejectButton.setEnabled(pending);
        });

        backButton.addActionListener(e -> {
            if (backToDashboardCallback != null) backToDashboardCallback.run();
        });
    }

    private boolean isPendingSelected() {
        int row = proposalTable.getSelectedRow();
        if (row == -1) return false;
        return "PENDING".equals(tableModel.getValueAt(row, 3));
    }

    private void applyFilter() {
        String filter = (String) statusFilterCombo.getSelectedItem();
        loadProposals(filter);
    }

    private void updateStatus(Proposal.ProposalStatus status) {
        int row = proposalTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a proposal first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(row, 0);
        try {
            boolean ok = proposalService.updateProposalStatus(id, status);
            if (ok) {
                statusLabel.setText("Proposal " + status.toString().toLowerCase() + " successfully.");
                loadProposals((String) statusFilterCombo.getSelectedItem());
            } else {
                JOptionPane.showMessageDialog(this, "Could not update proposal status.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProposals() {
        loadProposals("All Status");
    }

    private void loadProposals(String filter) {
        try {
            List<Proposal> proposals = proposalService.getAllProposals();
            tableModel.setRowCount(0);

            for (Proposal p : proposals) {
                String statusStr = p.getStatus().toString();
                if (!"All Status".equals(filter) && !statusStr.equals(filter)) continue;

                User student = userDAO.getUserById(p.getStudentId());
                String name = student != null ? student.getUsername() : "Unknown";
                tableModel.addRow(new Object[]{p.getId(), name, p.getFilePath(), statusStr});
            }

            acceptButton.setEnabled(false);
            rejectButton.setEnabled(false);
            statusLabel.setText("Showing " + tableModel.getRowCount() + " proposal(s).");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading proposals: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Color-codes the Status column
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);
            String s = value != null ? value.toString() : "";
            switch (s) {
                case "ACCEPTED": setForeground(new Color(34, 139, 34)); break;
                case "REJECTED": setForeground(new Color(200, 50, 50));  break;
                case "PENDING":  setForeground(new Color(200, 130, 0));  break;
                default:         setForeground(Color.BLACK);
            }
            return this;
        }
    }
}
