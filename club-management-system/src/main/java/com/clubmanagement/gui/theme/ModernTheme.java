package com.clubmanagement.gui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ModernTheme {
    // Primary Blue Colors (Microsoft Word inspired)
    public static final Color PRIMARY_BLUE = new Color(43, 87, 154);           // Dark blue
    public static final Color SECONDARY_BLUE = new Color(68, 114, 196);        // Medium blue
    public static final Color LIGHT_BLUE = new Color(91, 155, 213);            // Light blue
    public static final Color ACCENT_BLUE = new Color(155, 194, 230);          // Very light blue
    public static final Color HOVER_BLUE = new Color(214, 234, 248);           // Hover blue

    // Neutral Colors
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color LIGHT_GRAY = new Color(248, 249, 250);
    public static final Color MEDIUM_GRAY = new Color(209, 213, 219);
    public static final Color DARK_GRAY = new Color(107, 114, 128);
    public static final Color TEXT_DARK = new Color(31, 41, 55);
    public static final Color TEXT_LIGHT = new Color(75, 85, 99);

    // Status Colors
    public static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    public static final Color WARNING_YELLOW = new Color(251, 191, 36);
    public static final Color ERROR_RED = new Color(239, 68, 68);

    // Ribbon Colors
    public static final Color RIBBON_BACKGROUND = new Color(250, 251, 252);
    public static final Color RIBBON_BORDER = new Color(209, 213, 219);
    public static final Color TAB_ACTIVE = WHITE;
    public static final Color TAB_INACTIVE = new Color(243, 244, 246);

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);

    // Component Styling Methods
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(WHITE);
        button.setBackground(PRIMARY_BLUE);
        button.setBorder(createRoundedBorder(PRIMARY_BLUE, 6));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_BLUE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_BLUE);
            }
        });

        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(PRIMARY_BLUE);
        button.setBackground(WHITE);
        button.setBorder(createRoundedBorder(PRIMARY_BLUE, 6));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_BLUE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(WHITE);
            }
        });

        return button;
    }

    public static JButton createRibbonButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(SMALL_FONT);
        button.setForeground(TEXT_DARK);
        button.setBackground(RIBBON_BACKGROUND);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_BLUE);
                button.setBorder(createRoundedBorder(MEDIUM_GRAY, 3));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(RIBBON_BACKGROUND);
                button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            }
        });

        return button;
    }

    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(BODY_FONT);
        textField.setForeground(TEXT_DARK);
        textField.setBackground(WHITE);
        textField.setBorder(createRoundedBorder(MEDIUM_GRAY, 4));
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setCaretColor(PRIMARY_BLUE);

        // Focus effect
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(createRoundedBorder(PRIMARY_BLUE, 4));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(createRoundedBorder(MEDIUM_GRAY, 4));
            }
        });

        return textField;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(BODY_FONT);
        passwordField.setForeground(TEXT_DARK);
        passwordField.setBackground(WHITE);
        passwordField.setBorder(createRoundedBorder(MEDIUM_GRAY, 4));
        passwordField.setPreferredSize(new Dimension(200, 35));
        passwordField.setCaretColor(PRIMARY_BLUE);

        // Focus effect
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(createRoundedBorder(PRIMARY_BLUE, 4));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(createRoundedBorder(MEDIUM_GRAY, 4));
            }
        });

        return passwordField;
    }

    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(BODY_FONT);
        comboBox.setForeground(TEXT_DARK);
        comboBox.setBackground(WHITE);
        comboBox.setBorder(createRoundedBorder(MEDIUM_GRAY, 4));
        comboBox.setPreferredSize(new Dimension(200, 35));

        return comboBox;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_BLUE);
        return label;
    }

    public static JLabel createHeadingLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADING_FONT);
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_LIGHT);
        return label;
    }

    public static JPanel createRibbonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(RIBBON_BACKGROUND);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, RIBBON_BORDER));
        return panel;
    }

    public static JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(LIGHT_GRAY);
        return panel;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(createShadowBorder());
        return panel;
    }

    public static Border createRoundedBorder(Color color, int radius) {
        return new RoundedBorder(color, radius);
    }

    public static Border createShadowBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(0, 0, 0, 30)),
                BorderFactory.createLineBorder(MEDIUM_GRAY, 1)
            )
        );
    }

    // Custom rounded border class
    static class RoundedBorder implements Border {
        private Color color;
        private int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}