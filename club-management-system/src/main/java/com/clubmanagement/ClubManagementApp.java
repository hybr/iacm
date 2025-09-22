package com.clubmanagement;

import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.gui.LoginFrame;

import javax.swing.*;
import java.sql.SQLException;

public class ClubManagementApp {
    public static void main(String[] args) {
        // Set uncaught exception handler for EDT
        System.setProperty("sun.awt.exception.handler", "com.clubmanagement.ClubManagementApp$EdtExceptionHandler");

        // Set macOS specific properties
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Club Management System");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Club Management System");

        // Set system Look and Feel for better macOS integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default if system LAF fails
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception ex) {
                // Continue with default
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {

                    DatabaseManager.getConnection();
                    System.out.println("Database initialized successfully!");

                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);

                } catch (SQLException e) {
                    System.err.println("Failed to initialize database: " + e.getMessage());
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(null,
                        "Failed to initialize database.\nPlease ensure SQLite JDBC driver is available.\n\n" +
                        "Error: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);

                    System.exit(1);
                } catch (Exception e) {
                    System.err.println("Unexpected error during application startup: " + e.getMessage());
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(null,
                        "Unexpected error during application startup:\n" + e.getMessage(),
                        "Application Error",
                        JOptionPane.ERROR_MESSAGE);

                    System.exit(1);
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseManager.closeConnection();
                System.out.println("Database connection closed.");
            }
        }));
    }

    /**
     * Exception handler for Event Dispatch Thread
     */
    public static class EdtExceptionHandler {
        public void handle(Throwable throwable) {
            System.err.println("Uncaught exception in EDT:");
            throwable.printStackTrace();

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "An unexpected error occurred:\n" + throwable.getMessage() +
                    "\n\nPlease check the console for details.",
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE);
            });
        }
    }
}