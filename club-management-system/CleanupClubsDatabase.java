import com.clubmanagement.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class CleanupClubsDatabase {
    public static void main(String[] args) {
        try {
            System.out.println("Cleaning up Clubs Database...");
            System.out.println("=============================");

            // Initialize database
            DatabaseManager.getConnection();
            System.out.println("✓ Database connection successful");

            // Delete all clubs completely
            String deleteQuery = "DELETE FROM clubs";

            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                int deleted = stmt.executeUpdate(deleteQuery);
                System.out.println("✓ Deleted " + deleted + " existing clubs");
            }

            // Reset auto-increment counter
            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='clubs'");
                System.out.println("✓ Reset club ID counter");
            }

            // Insert only the 6 specified clubs
            String[] newClubs = {
                "Science",
                "Humanities",
                "Social Science",
                "Math",
                "Art",
                "Mind Matters"
            };

            String insertQuery = "INSERT INTO clubs (name) VALUES (?)";

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

                for (String clubName : newClubs) {
                    pstmt.setString(1, clubName);
                    pstmt.executeUpdate();
                    System.out.println("  + " + clubName);
                }
            }

            // Reset Grade 9 club assignments
            String resetQuery = """
                UPDATE users
                SET assigned_club_id = NULL, first_login_completed = false
                WHERE role = 'GRADE_9'
            """;

            try (Connection conn = DatabaseManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                int updated = stmt.executeUpdate(resetQuery);
                System.out.println("✓ Reset " + updated + " Grade 9 student assignments");
            }

            System.out.println("\n🎉 CLUBS DATABASE CLEANUP COMPLETE!");
            System.out.println("===================================");

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
}