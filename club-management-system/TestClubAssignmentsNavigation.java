import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.gui.MainDashboard;
import com.clubmanagement.database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Test to specifically check the club assignments navigation issue
 */
public class TestClubAssignmentsNavigation {
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
            } else {
                System.out.println("✗ Manager login failed");
                return;
            }

            // Create and show the dashboard
            SwingUtilities.invokeLater(() -> {
                try {
                    MainDashboard mainDash = new MainDashboard(authService);
                    mainDash.setVisible(true);

                    System.out.println("\n🔍 Club Assignments Navigation Test:");
                    System.out.println("═══════════════════════════════════════");
                    System.out.println("1. Manager dashboard should open with Proposals view");
                    System.out.println("2. Click 'Club Assignments' - should show assignments table");
                    System.out.println("3. Click 'Dashboard' - should return to Proposals view");
                    System.out.println("4. Click 'Proposals' - should also return to Proposals view");
                    System.out.println("5. Test navigation between all features");

                    System.out.println("\n📋 Navigation Test Instructions:");
                    System.out.println("• Start: Default view should be Proposals");
                    System.out.println("• Click 'Club Assignments' and wait for it to load");
                    System.out.println("• Try clicking 'Dashboard' - does it work?");
                    System.out.println("• Try clicking 'Proposals' - does it work?");
                    System.out.println("• If buttons don't respond, that's the bug!");

                    System.out.println("\n🐛 Expected Issue:");
                    System.out.println("Navigation toolbar buttons become unresponsive");
                    System.out.println("after entering Club Assignments view");

                    // Add a timer to periodically check the status
                    Timer statusTimer = new Timer(5000, new ActionListener() {
                        private int count = 0;
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            count++;
                            System.out.println("\n⏰ Status Check " + count + ":");
                            System.out.println("• Dashboard is still running");
                            System.out.println("• Test navigation if you haven't already");
                            if (count >= 6) { // Stop after 30 seconds
                                System.out.println("• Stopping status checks");
                                ((Timer)e.getSource()).stop();
                            }
                        }
                    });
                    statusTimer.start();

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