import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.gui.MainDashboard;
import com.clubmanagement.database.DatabaseManager;

import javax.swing.*;
import java.awt.*;

/**
 * Test class to verify manager navigation functionality
 */
public class TestManagerNavigation {
    public static void main(String[] args) {
        try {
            // Initialize database
            DatabaseManager.getConnection();
            System.out.println("✓ Database connection established");

            // Create authentication service
            AuthenticationService authService = new AuthenticationService();
            System.out.println("✓ Authentication service created");

            // Test Manager login
            boolean loginSuccess = authService.login("manager", "manager123");
            if (loginSuccess) {
                System.out.println("✓ Manager login successful");
                System.out.println("  Current user: " + authService.getCurrentUser().getUsername());
                System.out.println("  User role: " + authService.getCurrentUser().getRole());
            } else {
                System.out.println("✗ Manager login failed");
                return;
            }

            // Create and show the dashboard for manual testing
            SwingUtilities.invokeLater(() -> {
                try {
                    MainDashboard mainDash = new MainDashboard(authService);
                    mainDash.setVisible(true);

                    System.out.println("✓ Manager Dashboard opened successfully");
                    System.out.println("\n📋 Manager Navigation Testing:");
                    System.out.println("═══════════════════════════════════");
                    System.out.println("Available navigation buttons:");
                    System.out.println("• 🏠 Dashboard - Should show proposal management");
                    System.out.println("• 📋 Proposals - Should show proposal management");
                    System.out.println("• 📊 Attendance Reports - Should show read-only attendance data");
                    System.out.println("• 👥 Club Assignments - Should show read-only assignment data");
                    System.out.println("• 👤 Profile, 🔑 Password, ℹ️ Help, 🚪 Logout");
                    System.out.println("\n❌ REMOVED:");
                    System.out.println("• 📈 Reports button (no longer available)");
                    System.out.println("\n📝 Test Instructions:");
                    System.out.println("1. Click on 'Attendance Reports' - verify it loads");
                    System.out.println("2. Click on 'Proposals' - verify you can navigate back");
                    System.out.println("3. Click on 'Club Assignments' - verify it loads");
                    System.out.println("4. Click on 'Dashboard' - verify you can navigate back");
                    System.out.println("5. Verify that 'Reports' button is no longer present");
                    System.out.println("\nIf navigation works properly, the issue is resolved!");

                } catch (Exception e) {
                    System.out.println("✗ Error creating MainDashboard: " + e.getMessage());
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            System.out.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}