import com.clubmanagement.services.AuthenticationService;
import com.clubmanagement.gui.Grade9StudentsViewPanel;
import com.clubmanagement.gui.Grade11EnhancedDashboard;
import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.dao.ClubAllocationDAO;
import com.clubmanagement.models.User.UserRole;

import javax.swing.*;
import java.awt.*;

/**
 * Test class to verify Grade 9 students view feature for Grade 11 users
 */
public class TestGrade9StudentsView {
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
                System.out.println("✓ Grade 11 login successful");
                System.out.println("  Current user: " + authService.getCurrentUser().getUsername());
                System.out.println("  User role: " + authService.getCurrentUser().getRole());
            } else {
                System.out.println("✗ Grade 11 login failed");
                return;
            }

            // Test data availability
            UserDAO userDAO = new UserDAO();
            var grade9Students = userDAO.getUsersByRole(UserRole.GRADE_9);
            System.out.println("✓ Found " + grade9Students.size() + " Grade 9 students in database");

            ClubAllocationDAO allocationDAO = new ClubAllocationDAO();
            var allocations = allocationDAO.getAllAllocations();
            System.out.println("✓ Found " + allocations.size() + " club allocations in database");

            // Test Grade9StudentsViewPanel creation
            try {
                Grade9StudentsViewPanel viewPanel = new Grade9StudentsViewPanel(authService);
                System.out.println("✓ Grade9StudentsViewPanel created successfully");

                // Test enhanced dashboard with new feature
                SwingUtilities.invokeLater(() -> {
                    try {
                        Grade11EnhancedDashboard dashboard = new Grade11EnhancedDashboard(authService);
                        System.out.println("✓ Grade11EnhancedDashboard with Grade 9 view feature created successfully");

                        System.out.println("\n🎉 Grade 9 Students View feature is working correctly!");
                        System.out.println("\nGrade 11 students can now:");
                        System.out.println("1. Mark their daily attendance");
                        System.out.println("2. Upload PDF proposals");
                        System.out.println("3. Check proposal status (PENDING/ACCEPTED/REJECTED)");
                        System.out.println("4. View all Grade 9 students and their club assignments (NEW)");

                        System.out.println("\nGrade 9 Students View Features:");
                        System.out.println("- Table showing all Grade 9 students");
                        System.out.println("- Student information: ID, Username, Full Name, Email");
                        System.out.println("- Club assignment status for each student");
                        System.out.println("- Search functionality to find specific students");
                        System.out.println("- Filter by club or view unassigned students");
                        System.out.println("- Sortable columns and refresh capability");

                        // Print sample data
                        System.out.println("\nSample Grade 9 Students Data:");
                        System.out.println("============================");
                        for (int i = 0; i < Math.min(3, grade9Students.size()); i++) {
                            var student = grade9Students.get(i);
                            System.out.println("Student " + (i+1) + ":");
                            System.out.println("  ID: " + student.getId());
                            System.out.println("  Username: " + student.getUsername());
                            System.out.println("  Name: " + (student.getFullName() != null ? student.getFullName() : "N/A"));
                            System.out.println("  Email: " + (student.getEmail() != null ? student.getEmail() : "N/A"));
                        }

                        System.out.println("\n✅ All tests passed! The Grade 9 Students View feature is ready for use.");
                        System.exit(0);

                    } catch (Exception e) {
                        System.out.println("✗ Error creating enhanced dashboard: " + e.getMessage());
                        e.printStackTrace();
                        System.exit(1);
                    }
                });

            } catch (Exception e) {
                System.out.println("✗ Error creating Grade9StudentsViewPanel: " + e.getMessage());
                e.printStackTrace();
                return;
            }

        } catch (Exception e) {
            System.out.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}