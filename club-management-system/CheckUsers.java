import com.clubmanagement.database.DatabaseManager;
import java.sql.*;

public class CheckUsers {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseManager.getConnection();

            String sql = "SELECT id, username, role FROM users WHERE role = 'GRADE_11'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Available Grade 11 users:");
            System.out.println("========================");

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                                 ", Username: " + rs.getString("username") +
                                 ", Role: " + rs.getString("role"));
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}