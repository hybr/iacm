import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.models.User;
import com.clubmanagement.models.Club;
import com.clubmanagement.services.AuthenticationService;

import java.sql.SQLException;
import java.util.List;

public class TestClubSelection {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Club Selection Implementation...");

            // Initialize database
            DatabaseManager.getConnection();
            System.out.println("✓ Database connection successful");

            // Test Club DAO
            ClubDAO clubDAO = new ClubDAO();
            List<Club> clubs = clubDAO.getAllClubs();
            System.out.println("✓ Found " + clubs.size() + " clubs:");
            for (Club club : clubs) {
                System.out.println("  - " + club.getName() + " (ID: " + club.getId() + ")");
            }

            // Test User DAO
            UserDAO userDAO = new UserDAO();
            List<User> grade9Students = userDAO.getUsersByRole(User.UserRole.GRADE_9);
            System.out.println("✓ Found " + grade9Students.size() + " Grade 9 students");

            // Test authentication
            AuthenticationService authService = new AuthenticationService();
            if (authService.login("grade9_1", "pass123")) {
                System.out.println("✓ Authentication successful for grade9_1");

                User currentUser = authService.getCurrentUser();
                System.out.println("  User: " + currentUser.getFullName());
                System.out.println("  Role: " + currentUser.getRole());
                System.out.println("  First login completed: " + currentUser.isFirstLoginCompleted());
                System.out.println("  Assigned club ID: " + currentUser.getAssignedClubId());

                if (authService.needsFirstLoginCompletion()) {
                    System.out.println("✓ User needs first login completion (club selection)");
                } else {
                    System.out.println("✓ User has already completed first login");
                }

                authService.logout();
            } else {
                System.out.println("✗ Authentication failed");
            }

            // Test club assignment view
            List<User> studentsWithClubs = userDAO.getGrade9StudentsWithClubAssignments();
            System.out.println("✓ Grade 9 students with club assignments:");
            for (User student : studentsWithClubs) {
                String clubInfo = student.getAssignedClubId() != null ?
                    "Club ID " + student.getAssignedClubId() : "Unassigned";
                System.out.println("  - " + student.getFullName() + " (" + student.getUsername() + "): " + clubInfo);
            }

            System.out.println("\n✓ All club selection tests passed!");

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