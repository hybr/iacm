import com.clubmanagement.database.DatabaseManager;
import java.sql.*;

public class CheckPassword {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseManager.getConnection();

            String sql = "SELECT username, password FROM users WHERE username = 'grade11_1'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Password: " + rs.getString("password"));
            } else {
                System.out.println("User not found");
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}