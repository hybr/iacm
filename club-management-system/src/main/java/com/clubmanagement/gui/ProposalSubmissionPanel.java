package com.clubmanagement.gui;

import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.services.ProposalService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;

public class ProposalSubmissionPanel extends JPanel {
    private AuthenticationService authService;
    private ProposalService proposalService;
    private JTextField filePathField;
    private JButton browseButton;
    private JButton submitButton;
    private JTextArea statusArea;

    public ProposalSubmissionPanel(AuthenticationService authService) {
        this.authService = authService;
        this.proposalService = new ProposalService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        checkExistingProposals();
    }

    private void initializeComponents() {
        filePathField = new JTextField(30);
        filePathField.setEditable(false);
        browseButton = new JButton("Browse PDF File");
        submitButton = new JButton("Submit Proposal");
        statusArea = new JTextArea(10, 40);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Submit Proposal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 5, 5);
        centerPanel.add(new JLabel("Select PDF File:"), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseButton, BorderLayout.EAST);
        centerPanel.add(filePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        centerPanel.add(submitButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.insets = new Insets(20, 10, 10, 10);
        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Proposals"));
        centerPanel.add(scrollPane, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseForFile();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitProposal();
            }
        });
    }

    private void browseForFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
            }

            @Override
            public String getDescription() {
                return "PDF Files (*.pdf)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void submitProposal() {
        String filePath = filePathField.getText().trim();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a PDF file.",
                                        "No File Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Selected file does not exist.",
                                        "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int studentId = authService.getCurrentUser().getId();
            boolean success = proposalService.submitProposal(studentId, filePath);

            if (success) {
                JOptionPane.showMessageDialog(this, "Proposal submitted successfully!",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                filePathField.setText("");
                checkExistingProposals();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit proposal.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void checkExistingProposals() {
        try {
            int studentId = authService.getCurrentUser().getId();
            var proposals = proposalService.getProposalsByStudent(studentId);

            statusArea.setText("");
            if (proposals.isEmpty()) {
                statusArea.append("No proposals submitted yet.\n");
            } else {
                statusArea.append("Your Submitted Proposals:\n");
                statusArea.append("========================\n\n");
                for (var proposal : proposals) {
                    statusArea.append("Proposal ID: " + proposal.getId() + "\n");
                    statusArea.append("File: " + proposal.getFilePath() + "\n");
                    statusArea.append("Status: " + proposal.getStatus() + "\n");
                    statusArea.append("------------------------\n");
                }
            }
        } catch (SQLException ex) {
            statusArea.setText("Error loading proposals: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}