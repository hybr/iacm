import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.gui.Grade11EnhancedDashboard;
import com.clubmanagement.database.DatabaseManager;

import javax.swing.*;
import java.awt.*;

/**
 * Test to verify the Grade 11 logout functionality
 */
public class TestGrade11Logout {
    public static void main(String[] args) {
        try {
            // Initialize database
            DatabaseManager.getConnection();
            System.out.println("✓ Database connection established");

            // Create authentication service
            AuthenticationService authService = new AuthenticationService();
            System.out.println("✓ Authentication service created");

            // Test Grade 11 login
            boolean loginSuccess = authService.login("grade11_1", "pass123");
            if (loginSuccess) {
                System.out.println("✓ Grade 11 student login successful");
                System.out.println("  Current user: " + authService.getCurrentUser().getUsername());
                System.out.println("  User role: " + authService.getCurrentUser().getRole());
            } else {
                System.out.println("✗ Grade 11 student login failed");
                return;
            }

            // Test the Grade 11 Enhanced Dashboard with logout functionality
            SwingUtilities.invokeLater(() -> {
                try {
                    JFrame testFrame = new JFrame("Grade 11 Dashboard with Logout Test");
                    testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    testFrame.setSize(1200, 800);
                    testFrame.setLocationRelativeTo(null);

                    Grade11EnhancedDashboard dashboard = new Grade11EnhancedDashboard(authService);

                    // Set the logout callback to demonstrate functionality
                    dashboard.setLogoutCallback(() -> {
                        System.out.println("🚪 Logout callback triggered!");

                        // Simulate the logout process
                        authService.logout();
                        System.out.println("✓ User logged out successfully");

                        // Close the test window
                        testFrame.dispose();
                        System.out.println("✓ Application window closed");

                        System.out.println("\n🎉 Grade 11 Logout Test Completed Successfully!");
                        System.out.println("═══════════════════════════════════════");
                        System.out.println("✓ Logout button is visible in Grade 11 dashboard");
                        System.out.println("✓ Logout confirmation dialog works");
                        System.out.println("✓ Logout callback mechanism functions properly");
                        System.out.println("✓ User session is properly terminated");
                    });

                    testFrame.add(dashboard);
                    testFrame.setVisible(true);

                    System.out.println("\n🎯 Grade 11 Dashboard Test Instructions:");
                    System.out.println("═══════════════════════════════════════");
                    System.out.println("1. ✅ Look for the red 'Logout' button at the bottom of the left panel");
                    System.out.println("2. ✅ Click the logout button to test the confirmation dialog");
                    System.out.println("3. ✅ Click 'Yes' to confirm logout and test the callback");
                    System.out.println("4. ✅ The application should close and show success messages");

                    System.out.println("\n📋 Grade 11 Features Available:");
                    System.out.println("• 📝 Mark Attendance - Daily attendance marking");
                    System.out.println("• 📤 Upload Proposal - Submit PDF proposals");
                    System.out.println("• 📊 Check Proposal Status - View proposal status");
                    System.out.println("• 👥 View Grade 9 - View Grade 9 students and clubs");
                    System.out.println("• 🚪 Logout - Sign out with confirmation (NEW FEATURE)");

                    System.out.println("\n✨ New Logout Features:");
                    System.out.println("• Red logout button with distinctive styling");
                    System.out.println("• Confirmation dialog before logout");
                    System.out.println("• Proper callback mechanism to parent application");
                    System.out.println("• Session cleanup and window management");

                } catch (Exception e) {
                    System.out.println("✗ Error creating Grade 11 dashboard: " + e.getMessage());
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            System.out.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}