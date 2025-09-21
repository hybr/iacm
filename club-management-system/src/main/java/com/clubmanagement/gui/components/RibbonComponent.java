package com.clubmanagement.gui.components;

import com.clubmanagement.gui.theme.ModernTheme;
import com.clubmanagement.services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RibbonComponent extends JPanel {
    private JPanel tabPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private List<RibbonTab> tabs;
    private AuthenticationService authService;

    public RibbonComponent(AuthenticationService authService) {
        this.authService = authService;
        this.tabs = new ArrayList<>();
        initializeComponents();
        setupLayout();
        createTabs();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.RIBBON_BACKGROUND);

        tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setBackground(ModernTheme.RIBBON_BACKGROUND);
        tabPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.RIBBON_BORDER));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ModernTheme.RIBBON_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.RIBBON_BORDER));
        contentPanel.setPreferredSize(new Dimension(0, 120));
    }

    private void setupLayout() {
        add(tabPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void createTabs() {
        // Home Tab
        RibbonTab homeTab = createHomeTab();
        addTab(homeTab);

        // Account Tab
        RibbonTab accountTab = createAccountTab();
        addTab(accountTab);

        // Help Tab
        RibbonTab helpTab = createHelpTab();
        addTab(helpTab);

        // Select first tab by default
        if (!tabs.isEmpty()) {
            selectTab(0);
        }
    }

    private RibbonTab createHomeTab() {
        RibbonTab tab = new RibbonTab("Home");

        // Dashboard group
        RibbonGroup dashboardGroup = new RibbonGroup("Dashboard");
        if (authService.isClubManager()) {
            dashboardGroup.addButton("Proposals", "Manage club proposals", e -> notifyAction("proposals"));
            dashboardGroup.addButton("Allocations", "Manage club allocations", e -> notifyAction("allocations"));
            dashboardGroup.addButton("Reports", "View attendance reports", e -> notifyAction("reports"));
        } else if (authService.isGrade11()) {
            dashboardGroup.addButton("Submit", "Submit proposal", e -> notifyAction("submit"));
            dashboardGroup.addButton("Status", "Check proposal status", e -> notifyAction("status"));
            dashboardGroup.addButton("Attendance", "Track attendance", e -> notifyAction("attendance"));
            dashboardGroup.addButton("Members", "View club members", e -> notifyAction("members"));
        } else if (authService.isGrade9()) {
            dashboardGroup.addButton("Club Info", "View club information", e -> notifyAction("clubinfo"));
        }
        tab.addGroup(dashboardGroup);

        // Quick Actions group
        RibbonGroup actionsGroup = new RibbonGroup("Quick Actions");
        actionsGroup.addButton("Refresh", "Refresh current view", e -> notifyAction("refresh"));
        actionsGroup.addButton("Settings", "Application settings", e -> notifyAction("settings"));
        tab.addGroup(actionsGroup);

        return tab;
    }

    private RibbonTab createAccountTab() {
        RibbonTab tab = new RibbonTab("Account");

        // Profile group
        RibbonGroup profileGroup = new RibbonGroup("Profile");
        profileGroup.addButton("Change Password", "Change your password", e -> notifyAction("changepassword"));
        profileGroup.addButton("Profile Info", "View profile information", e -> notifyAction("profile"));
        tab.addGroup(profileGroup);

        // Session group
        RibbonGroup sessionGroup = new RibbonGroup("Session");
        sessionGroup.addButton("Logout", "Sign out of application", e -> notifyAction("logout"));
        tab.addGroup(sessionGroup);

        return tab;
    }

    private RibbonTab createHelpTab() {
        RibbonTab tab = new RibbonTab("Help");

        // Support group
        RibbonGroup supportGroup = new RibbonGroup("Support");
        supportGroup.addButton("User Guide", "View user documentation", e -> notifyAction("userguide"));
        supportGroup.addButton("About", "About this application", e -> notifyAction("about"));
        tab.addGroup(supportGroup);

        return tab;
    }

    public void addTab(RibbonTab tab) {
        tabs.add(tab);

        JButton tabButton = createTabButton(tab.getName(), tabs.size() - 1);
        tabPanel.add(tabButton);

        contentPanel.add(tab.getContentPanel(), tab.getName());

        revalidate();
        repaint();
    }

    private JButton createTabButton(String name, int index) {
        JButton button = new JButton(name);
        button.setFont(ModernTheme.BODY_FONT);
        button.setForeground(ModernTheme.TEXT_DARK);
        button.setBackground(ModernTheme.TAB_INACTIVE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 0, 1, ModernTheme.RIBBON_BORDER),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> selectTab(index));

        return button;
    }

    public void selectTab(int index) {
        if (index >= 0 && index < tabs.size()) {
            // Update tab button states
            Component[] components = tabPanel.getComponents();
            for (int i = 0; i < components.length; i++) {
                JButton tabButton = (JButton) components[i];
                if (i == index) {
                    tabButton.setBackground(ModernTheme.TAB_ACTIVE);
                    tabButton.setForeground(ModernTheme.PRIMARY_BLUE);
                } else {
                    tabButton.setBackground(ModernTheme.TAB_INACTIVE);
                    tabButton.setForeground(ModernTheme.TEXT_DARK);
                }
            }

            // Show selected tab content
            cardLayout.show(contentPanel, tabs.get(index).getName());
        }
    }

    // Action listener for ribbon buttons
    private List<ActionListener> actionListeners = new ArrayList<>();

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    private void notifyAction(String command) {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(new java.awt.event.ActionEvent(this, 0, command));
        }
    }

    // Inner classes for ribbon structure
    public static class RibbonTab {
        private String name;
        private JPanel contentPanel;

        public RibbonTab(String name) {
            this.name = name;
            this.contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            this.contentPanel.setBackground(ModernTheme.RIBBON_BACKGROUND);
        }

        public void addGroup(RibbonGroup group) {
            contentPanel.add(group.getPanel());
        }

        public String getName() { return name; }
        public JPanel getContentPanel() { return contentPanel; }
    }

    public static class RibbonGroup {
        private String name;
        private JPanel panel;
        private JPanel buttonPanel;

        public RibbonGroup(String name) {
            this.name = name;
            setupPanel();
        }

        private void setupPanel() {
            panel = new JPanel(new BorderLayout());
            panel.setBackground(ModernTheme.RIBBON_BACKGROUND);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, ModernTheme.RIBBON_BORDER),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));

            buttonPanel = new JPanel(new GridLayout(2, 0, 5, 5));
            buttonPanel.setBackground(ModernTheme.RIBBON_BACKGROUND);

            JLabel groupLabel = new JLabel(name, SwingConstants.CENTER);
            groupLabel.setFont(ModernTheme.SMALL_FONT);
            groupLabel.setForeground(ModernTheme.TEXT_LIGHT);

            panel.add(buttonPanel, BorderLayout.CENTER);
            panel.add(groupLabel, BorderLayout.SOUTH);
        }

        public void addButton(String text, String tooltip, ActionListener action) {
            JButton button = ModernTheme.createRibbonButton(text, null);
            button.setToolTipText(tooltip);
            button.addActionListener(action);
            buttonPanel.add(button);
        }

        public JPanel getPanel() { return panel; }
    }
}