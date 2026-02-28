package com.clubmanagement.gui;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.models.User;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ManagerDashboardPanel extends JPanel {

    private final AuthenticationService authService;
    private Runnable onProposals;
    private Runnable onAttendanceReports;
    private Runnable onClubAssignments;
    private Runnable onLogout;

    public ManagerDashboardPanel(AuthenticationService authService) {
        this.authService = authService;
        setBackground(ModernTheme.LIGHT_GRAY);
        setLayout(new BorderLayout());
        buildUI();
    }

    public void setOnProposals(Runnable r)          { this.onProposals = r; }
    public void setOnAttendanceReports(Runnable r)  { this.onAttendanceReports = r; }
    public void setOnClubAssignments(Runnable r)    { this.onClubAssignments = r; }
    public void setOnLogout(Runnable r)             { this.onLogout = r; }

    private void buildUI() {
        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.LIGHT_GRAY);
        header.setBorder(BorderFactory.createEmptyBorder(40, 50, 20, 50));

        User user = authService.getCurrentUser();
        String name = user.getFullName() != null && !user.getFullName().isBlank()
                      ? user.getFullName() : user.getUsername();

        JLabel welcomeLabel = new JLabel("Welcome back, " + name + "!");
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

        // ── Cards grid ──────────────────────────────────────────────────────
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
            "Proposal Management",
            "Review, accept, or reject student club proposals.",
            ModernTheme.PRIMARY_BLUE,
            () -> { if (onProposals != null) onProposals.run(); }
        ), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        cardsWrapper.add(buildCard(
            "Attendance Reports",
            "View and export attendance records across all clubs.",
            new Color(22, 163, 74),   // green
            () -> { if (onAttendanceReports != null) onAttendanceReports.run(); }
        ), gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        cardsWrapper.add(buildCard(
            "Club Assignments",
            "View student club assignments and enrollment details.",
            new Color(124, 58, 237),  // purple
            () -> { if (onClubAssignments != null) onClubAssignments.run(); }
        ), gbc);

        add(cardsWrapper, BorderLayout.CENTER);
    }

    private JPanel buildCard(String title, String description,
                              Color accentColor, Runnable action) {

        JPanel card = new JPanel() {
            @Override
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
        card.setPreferredSize(new Dimension(240, 220));

        // Accent bar at top
        JPanel accentBar = new JPanel() {
            @Override
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

        // Title label
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(ModernTheme.TEXT_DARK);

        // Description label (wrapping)
        JLabel descLabel = new JLabel("<html><div style='text-align:center;'>" +
                                      description + "</div></html>",
                                      SwingConstants.CENTER);
        descLabel.setFont(ModernTheme.BODY_FONT);
        descLabel.setForeground(ModernTheme.TEXT_LIGHT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 16, 0));

        // "Open" button
        JButton openBtn = new JButton("Open");
        openBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        openBtn.setForeground(ModernTheme.WHITE);
        openBtn.setBackground(accentColor);
        openBtn.setOpaque(true);
        openBtn.setFocusPainted(false);
        openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
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

        // Hover effect on the whole card
        Color normalBg = ModernTheme.WHITE;
        Color hoverBg  = new Color(
            Math.max(accentColor.getRed()   - 20, 0),
            Math.max(accentColor.getGreen() - 20, 0),
            Math.max(accentColor.getBlue()  - 20, 0),
            30
        );

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(accentColor.getRed(),
                                             accentColor.getGreen(),
                                             accentColor.getBlue(), 15));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2, true),
                    BorderFactory.createEmptyBorder(29, 29, 29, 29)
                ));
                card.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(normalBg);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernTheme.MEDIUM_GRAY, 1, true),
                    BorderFactory.createEmptyBorder(30, 30, 30, 30)
                ));
                card.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return card;
    }
}
