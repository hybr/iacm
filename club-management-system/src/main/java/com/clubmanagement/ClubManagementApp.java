package com.clubmanagement;

import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.gui.LoginFrame;

import javax.swing.*;
import java.sql.SQLException;

public class ClubManagementApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    DatabaseManager.getConnection();
                    System.out.println("Database initialized successfully!");

                    new LoginFrame().setVisible(true);

                } catch (SQLException e) {
                    System.err.println("Failed to initialize database: " + e.getMessage());
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(null,
                        "Failed to initialize database.\nPlease ensure SQLite JDBC driver is available.\n\n" +
                        "Error: " + e.getMessage(),
                        "Database Error",
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
}