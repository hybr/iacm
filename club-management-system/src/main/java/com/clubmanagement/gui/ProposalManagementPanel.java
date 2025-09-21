package com.clubmanagement.gui;

import com.clubmanagement.models.Proposal;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.services.ProposalService;
import com.clubmanagement.dao.UserDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ProposalManagementPanel extends JPanel {
    private AuthenticationService authService;
    private ProposalService proposalService;
    private UserDAO userDAO;
    private JTable proposalTable;
    private DefaultTableModel tableModel;
    private JButton acceptButton;
    private JButton rejectButton;
    private JButton refreshButton;

    public ProposalManagementPanel(AuthenticationService authService) {
        this.authService = authService;
        this.proposalService = new ProposalService();
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadProposals();
    }

    private void initializeComponents() {
        String[] columnNames = {"ID", "Student", "File Path", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        proposalTable = new JTable(tableModel);
        proposalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        acceptButton = new JButton("Accept Proposal");
        rejectButton = new JButton("Reject Proposal");
        refreshButton = new JButton("Refresh");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Proposal Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(proposalTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProposalStatus(Proposal.ProposalStatus.ACCEPTED);
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProposalStatus(Proposal.ProposalStatus.REJECTED);
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProposals();
            }
        });

        proposalTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = proposalTable.getSelectedRow() != -1;
            acceptButton.setEnabled(hasSelection && isPendingProposal());
            rejectButton.setEnabled(hasSelection && isPendingProposal());
        });
    }

    private boolean isPendingProposal() {
        int selectedRow = proposalTable.getSelectedRow();
        if (selectedRow == -1) return false;

        String status = (String) tableModel.getValueAt(selectedRow, 3);
        return "PENDING".equals(status);
    }

    private void updateProposalStatus(Proposal.ProposalStatus status) {
        int selectedRow = proposalTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a proposal.",
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int proposalId = (Integer) tableModel.getValueAt(selectedRow, 0);

        try {
            boolean success = proposalService.updateProposalStatus(proposalId, status);
            if (success) {
                JOptionPane.showMessageDialog(this,
                                            "Proposal " + status.toString().toLowerCase() + " successfully!",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProposals();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update proposal status.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadProposals() {
        try {
            List<Proposal> proposals = proposalService.getAllProposals();
            tableModel.setRowCount(0);

            for (Proposal proposal : proposals) {
                User student = userDAO.getUserById(proposal.getStudentId());
                String studentName = student != null ? student.getUsername() : "Unknown";

                Object[] row = {
                    proposal.getId(),
                    studentName,
                    proposal.getFilePath(),
                    proposal.getStatus().toString()
                };
                tableModel.addRow(row);
            }

            acceptButton.setEnabled(false);
            rejectButton.setEnabled(false);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading proposals: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}