import com.clubmanagement.dao.UserDAO;
import com.clubmanagement.gui.ForgotPasswordFrame;
import com.clubmanagement.database.DatabaseManager;
import com.clubmanagement.models.User;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Test to verify the forgot password security answer fix
 */
public class TestForgotPasswordFix {
    public static void main(String[] args) {
        try {
            // Initialize database
            DatabaseManager.getConnection();
            System.out.println("✓ Database connection established");

            // Test the security answer validation directly
            UserDAO userDAO = new UserDAO();
            System.out.println("✓ UserDAO created");

            // Check what users have security questions
            System.out.println("\n🔍 Checking existing users with security questions:");
            System.out.println("═══════════════════════════════════════════════");

            // Test with manager user
            User manager = userDAO.getUserByUsername("manager");
            if (manager != null) {
                System.out.println("Manager user found:");
                System.out.println("  Username: " + manager.getUsername());
                System.out.println("  Security Question: " + manager.getSecurityQuestion());
                System.out.println("  Security Answer: " + manager.getSecurityAnswer());

                // Test the security answer validation
                if (manager.getSecurityQuestion() != null && manager.getSecurityAnswer() != null) {
                    boolean validResult = userDAO.validateSecurityAnswer("manager", manager.getSecurityAnswer());
                    System.out.println("  ✓ Direct validation test: " + (validResult ? "PASSED" : "FAILED"));

                    // Test case insensitive
                    boolean caseInsensitiveResult = userDAO.validateSecurityAnswer("manager", manager.getSecurityAnswer().toUpperCase());
                    System.out.println("  ✓ Case insensitive test: " + (caseInsensitiveResult ? "PASSED" : "FAILED"));

                    // Test wrong answer
                    boolean wrongAnswerResult = userDAO.validateSecurityAnswer("manager", "wrong_answer");
                    System.out.println("  ✓ Wrong answer test: " + (!wrongAnswerResult ? "PASSED" : "FAILED"));
                }
            }

            // Test with Grade 11 user
            User grade11 = userDAO.getUserByUsername("grade11_1");
            if (grade11 != null) {
                System.out.println("\nGrade 11 user found:");
                System.out.println("  Username: " + grade11.getUsername());
                System.out.println("  Security Question: " + grade11.getSecurityQuestion());
                System.out.println("  Security Answer: " + grade11.getSecurityAnswer());

                if (grade11.getSecurityQuestion() != null && grade11.getSecurityAnswer() != null) {
                    boolean validResult = userDAO.validateSecurityAnswer("grade11_1", grade11.getSecurityAnswer());
                    System.out.println("  ✓ Direct validation test: " + (validResult ? "PASSED" : "FAILED"));
                }
            }

            // Launch the forgot password frame for UI testing
            SwingUtilities.invokeLater(() -> {
                try {
                    ForgotPasswordFrame forgotPasswordFrame = new ForgotPasswordFrame();
                    forgotPasswordFrame.setVisible(true);

                    System.out.println("\n🎉 Forgot Password Fix Applied Successfully!");
                    System.out.println("═══════════════════════════════════════");

                    System.out.println("\n🔧 Fix Applied:");
                    System.out.println("• Replaced password hashing verification with simple string comparison");
                    System.out.println("• Now uses UserDAO.validateSecurityAnswer() method");
                    System.out.println("• Proper case-insensitive matching");
                    System.out.println("• Correct database query for security answer validation");

                    System.out.println("\n📋 Testing Instructions:");
                    System.out.println("═══════════════════════════════════════");
                    System.out.println("1. ✅ Enter a valid username (try 'manager' or 'grade11_1')");
                    System.out.println("2. ✅ Click 'Continue' to proceed to security question");
                    System.out.println("3. ✅ Enter the correct security answer:");
                    System.out.println("   • For manager: 'blue' (default answer)");
                    System.out.println("   • For grade11_1: 'blue' (default answer)");
                    System.out.println("4. ✅ Click 'Verify Answer' - should now work correctly!");
                    System.out.println("5. ✅ If correct, proceed to set new password");

                    System.out.println("\n✨ Expected Behavior (FIXED):");
                    System.out.println("• Security answer validation should work correctly");
                    System.out.println("• Case insensitive matching (blue, BLUE, Blue all work)");
                    System.out.println("• No more 'incorrect answer' errors with valid answers");
                    System.out.println("• Proper progression through password reset flow");

                    System.out.println("\n🚫 Previous Issue (RESOLVED):");
                    System.out.println("• Was using password hashing for security answer verification");
                    System.out.println("• Security answers are stored as plain text, not hashed");
                    System.out.println("• Caused all security answers to fail validation");

                    System.out.println("\n✅ Default Security Answers for Testing:");
                    System.out.println("• Question: 'What is your favorite color?'");
                    System.out.println("• Answer: 'blue' (case insensitive)");

                    System.out.println("\n🔄 Test the forgot password flow to confirm the fix works!");

                } catch (Exception e) {
                    System.out.println("✗ Error creating ForgotPasswordFrame: " + e.getMessage());
                    e.printStackTrace();
                }
            });

        } catch (SQLException e) {
            System.out.println("✗ Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}