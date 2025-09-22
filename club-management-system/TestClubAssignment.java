import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.models.User;
import com.clubmanagement.models.Club;
import com.clubmanagement.services.AuthenticationService;

import java.sql.SQLException;
import java.util.List;

public class TestClubAssignment {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Club Assignment Functionality...");

            // Initialize database
            DatabaseManager.getConnection();
            System.out.println("✓ Database connection successful");

            // Test getting a club
            ClubDAO clubDAO = new ClubDAO();
            Club scienceClub = clubDAO.getClubById(1);
            if (scienceClub != null) {
                System.out.println("✓ Found Science Club: " + scienceClub.getName());
            }

            // Test authenticating a Grade 9 student
            AuthenticationService authService = new AuthenticationService();
            if (authService.login("grade9_1", "pass123")) {
                User student = authService.getCurrentUser();
                System.out.println("✓ Authenticated: " + student.getFullName());
                System.out.println("  Current club assignment: " + student.getAssignedClubId());
                System.out.println("  First login completed: " + student.isFirstLoginCompleted());

                // Test club assignment
                UserDAO userDAO = new UserDAO();
                System.out.println("\n--- Testing Club Assignment ---");

                if (userDAO.updateUserClubAssignment(student.getId(), scienceClub.getId())) {
                    System.out.println("✓ Assigned student to Science Club");

                    if (userDAO.updateFirstLoginCompleted(student.getId(), true)) {
                        System.out.println("✓ Marked first login as completed");

                        // Test by re-authenticating
                        authService.logout();
                        if (authService.login("grade9_1", "pass123")) {
                            User updatedStudent = authService.getCurrentUser();
                            System.out.println("✓ Re-authenticated successfully");
                            System.out.println("  Updated club assignment: " + updatedStudent.getAssignedClubId());
                            System.out.println("  Updated first login completed: " + updatedStudent.isFirstLoginCompleted());

                            if (authService.needsFirstLoginCompletion()) {
                                System.out.println("✗ Student still needs first login completion");
                            } else {
                                System.out.println("✓ Student no longer needs first login completion");
                            }
                        }
                    }
                }
            }

            System.out.println("\n✓ Club assignment test completed successfully!");

        } catch (SQLException e) {
            System.err.println("✗ Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection();
        }
    }
}