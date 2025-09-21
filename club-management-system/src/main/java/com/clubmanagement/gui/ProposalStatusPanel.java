package com.clubmanagement.gui;

import com.clubmanagement.models.Proposal;
import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.services.ProposalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProposalStatusPanel extends JPanel {
    private AuthenticationService authService;
    private ProposalService proposalService;
    private JTable statusTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ProposalStatusPanel(AuthenticationService authService) {
        this.authService = authService;
        this.proposalService = new ProposalService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadProposalStatus();
    }

    private void initializeComponents() {
        String[] columnNames = {"Proposal ID", "File Path", "Status", "Submission Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        statusTable = new JTable(tableModel);
        refreshButton = new JButton("Refresh Status");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("My Proposal Status", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(statusTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadProposalStatus());
    }

    private void loadProposalStatus() {
        try {
            int studentId = authService.getCurrentUser().getId();
            List<Proposal> proposals = proposalService.getProposalsByStudent(studentId);
            tableModel.setRowCount(0);

            if (proposals.isEmpty()) {
                Object[] row = {"No proposals found", "", "", ""};
                tableModel.addRow(row);
            } else {
                for (Proposal proposal : proposals) {
                    Object[] row = {
                        proposal.getId(),
                        proposal.getFilePath(),
                        proposal.getStatus().toString(),
                        "N/A"
                    };
                    tableModel.addRow(row);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading proposal status: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}