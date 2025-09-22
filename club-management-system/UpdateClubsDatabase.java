import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.dao.ClubDAO;
import com.clubmanagement.models.Club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class UpdateClubsDatabase {
    public static void main(String[] args) {
        try {
            System.out.println("Updating Clubs Database to Grade 9 Specification...");
            System.out.println("====================================================");

            // Initialize database
            DatabaseManager.getConnection();
            System.out.println("✓ Database connection successful");

            // Show current clubs
            ClubDAO clubDAO = new ClubDAO();
            List<Club> currentClubs = clubDAO.getAllClubs();
            System.out.println("\nCurrent clubs in database:");
            for (Club club : currentClubs) {
                System.out.println("  - " + club.getName() + " (ID: " + club.getId() + ")");
            }

            // Clear all existing clubs
            clearAllClubs();
            System.out.println("\n✓ Cleared all existing clubs");

            // Insert the 6 specified clubs
            String[] newClubs = {
                "Science",
                "Humanities",
                "Social Science",
                "Math",
                "Art",
                "Mind Matters"
            };

            System.out.println("\n✓ Inserting new clubs:");
            for (String clubName : newClubs) {
                insertClub(clubName);
                System.out.println("  + " + clubName);
            }

            // Verify the update
            System.out.println("\n✓ Updated clubs in database:");
            List<Club> updatedClubs = clubDAO.getAllClubs();
            for (Club club : updatedClubs) {
                System.out.println("  - " + club.getName() + " (ID: " + club.getId() + ")");
            }

            // Reset all Grade 9 student club assignments since clubs changed
            resetGrade9ClubAssignments();
            System.out.println("\n✓ Reset all Grade 9 student club assignments");

            System.out.println("\n🎉 CLUBS DATABASE UPDATE COMPLETE!");
            System.out.println("===================================");
            System.out.println("✅ Database now contains only the 6 specified clubs");
            System.out.println("✅ Grade 9 students will see only these clubs in selection");
            System.out.println("✅ All existing club assignments have been reset");

        } catch (SQLException e) {
            System.err.println("✗ Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                DatabaseManager.closeConnection();
                System.out.println("✓ Database connection closed");
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private static void clearAllClubs() throws SQLException {
        String deleteQuery = "DELETE FROM clubs";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
        }
    }

    private static void insertClub(String clubName) throws SQLException {
        String insertQuery = "INSERT INTO clubs (name) VALUES (?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, clubName);
            pstmt.executeUpdate();
        }
    }


    private static void resetGrade9ClubAssignments() throws SQLException {
        String resetQuery = """
            UPDATE users
            SET assigned_club_id = NULL, first_login_completed = false
            WHERE role = 'GRADE_9'
        """;

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            int updated = stmt.executeUpdate(resetQuery);
            System.out.println("  Reset " + updated + " Grade 9 student assignments");
        }
    }
}