package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Grade11LandingDashboard extends JPanel {

    private final AuthenticationService authService;
    private Runnable onAttendance;
    private Runnable onUploadProposal;
    private Runnable onProposalStatus;
    private Runnable onViewGrade9;
    private Runnable onLogout;

    public Grade11LandingDashboard(AuthenticationService authService) {
        this.authService = authService;
        setBackground(ModernTheme.LIGHT_GRAY);
        setLayout(new BorderLayout());
        buildUI();
    }

    public void setOnAttendance(Runnable r)     { this.onAttendance = r; }
    public void setOnUploadProposal(Runnable r) { this.onUploadProposal = r; }
    public void setOnProposalStatus(Runnable r) { this.onProposalStatus = r; }
    public void setOnViewGrade9(Runnable r)     { this.onViewGrade9 = r; }
    public void setOnLogout(Runnable r)         { this.onLogout = r; }

    private void buildUI() {
        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.LIGHT_GRAY);
        header.setBorder(BorderFactory.createEmptyBorder(40, 50, 20, 50));

        User user = authService.getCurrentUser();
        String name = user.getFullName() != null && !user.getFullName().isBlank()
                      ? user.getFullName() : user.getUsername();

        JLabel welcomeLabel = new JLabel("Welcome, " + name + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(ModernTheme.PRIMARY_BLUE);

        JLabel subtitleLabel = new JLabel("Select an option below to get started.");
        subtitleLabel.setFont(ModernTheme.BODY_FONT);
        subtitleLabel.setForeground(ModernTheme.TEXT_LIGHT);

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setBackground(ModernTheme.LIGHT_GRAY);
        textStack.add(welcomeLabel);
        textStack.add(Box.createVerticalStrut(6));
        textStack.add(subtitleLabel);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        logoutBtn.addActionListener(e -> { if (onLogout != null) onLogout.run(); });
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { logoutBtn.setBackground(new Color(170, 30, 30)); }
            public void mouseExited(MouseEvent e)  { logoutBtn.setBackground(new Color(200, 50, 50)); }
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setBackground(ModernTheme.LIGHT_GRAY);
        rightPanel.add(logoutBtn);

        header.add(textStack, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── 2×2 Cards grid ──────────────────────────────────────────────────
        JPanel cardsWrapper = new JPanel(new GridBagLayout());
        cardsWrapper.setBackground(ModernTheme.LIGHT_GRAY);
        cardsWrapper.setBorder(BorderFactory.createEmptyBorder(10, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        cardsWrapper.add(buildCard(
            "Mark Attendance",
            "Record your attendance for today's club session.",
            ModernTheme.PRIMARY_BLUE,
            () -> { if (onAttendance != null) onAttendance.run(); }
        ), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        cardsWrapper.add(buildCard(
            "Upload Proposal",
            "Submit a new club proposal document for review.",
            new Color(217, 119, 6),
            () -> { if (onUploadProposal != null) onUploadProposal.run(); }
        ), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        cardsWrapper.add(buildCard(
            "Proposal Status",
            "Check the status of your submitted proposals.",
            new Color(14, 116, 144),
            () -> { if (onProposalStatus != null) onProposalStatus.run(); }
        ), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        cardsWrapper.add(buildCard(
            "View Grade 9 Students",
            "Browse Grade 9 students and their club assignments.",
            new Color(22, 163, 74),
            () -> { if (onViewGrade9 != null) onViewGrade9.run(); }
        ), gbc);

        add(cardsWrapper, BorderLayout.CENTER);
    }

    private JPanel buildCard(String title, String description,
                              Color accentColor, Runnable action) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(ModernTheme.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.MEDIUM_GRAY, 1, true),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(240, 200));

        JPanel accentBar = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        accentBar.setOpaque(false);
        accentBar.setPreferredSize(new Dimension(0, 6));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ModernTheme.TEXT_DARK);

        JLabel descLabel = new JLabel(
            "<html><div style='text-align:center;'>" + description + "</div></html>",
            SwingConstants.CENTER);
        descLabel.setFont(ModernTheme.BODY_FONT);
        descLabel.setForeground(ModernTheme.TEXT_LIGHT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 14, 0));

        JButton openBtn = new JButton("Open");
        openBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        openBtn.setForeground(ModernTheme.WHITE);
        openBtn.setBackground(accentColor);
        openBtn.setOpaque(true);
        openBtn.setFocusPainted(false);
        openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openBtn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        openBtn.addActionListener(e -> action.run());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(openBtn);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        descLabel.setAlignmentX(CENTER_ALIGNMENT);
        btnPanel.setAlignmentX(CENTER_ALIGNMENT);
        content.add(Box.createVerticalStrut(20));
        content.add(titleLabel);
        content.add(descLabel);
        content.add(btnPanel);

        card.add(accentBar, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(accentColor.getRed(),
                                             accentColor.getGreen(),
                                             accentColor.getBlue(), 15));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2, true),
                    BorderFactory.createEmptyBorder(29, 29, 29, 29)
                ));
                card.repaint();
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(ModernTheme.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernTheme.MEDIUM_GRAY, 1, true),
                    BorderFactory.createEmptyBorder(30, 30, 30, 30)
                ));
                card.repaint();
            }
            public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return card;
    }
}
