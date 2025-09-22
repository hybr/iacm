import javax.swing.SwingUtilities;

/**
 * Simple test to verify login UI components work
 */
public class TestLoginUI {
    public static void main(String[] args) {
        System.out.println("🔍 TESTING LOGIN UI COMPONENTS...");

        try {
            // Test if LoginFrame can be instantiated
            Class<?> loginClass = Class.forName("com.clubmanagement.gui.LoginFrame");
            System.out.println("✅ LoginFrame class found");

            // Test if SignUpFrame can be instantiated
            Class<?> signUpClass = Class.forName("com.clubmanagement.gui.SignUpFrame");
            System.out.println("✅ SignUpFrame class found");

            // Test if ForgotPasswordFrame can be instantiated
            Class<?> forgotClass = Class.forName("com.clubmanagement.gui.ForgotPasswordFrame");
            System.out.println("✅ ForgotPasswordFrame class found");

            // Try to actually create the login frame
            SwingUtilities.invokeLater(() -> {
                try {
                    Object loginFrame = loginClass.getDeclaredConstructor().newInstance();
                    System.out.println("✅ LoginFrame instantiated successfully");

                    // Show the frame briefly then close it
                    javax.swing.JFrame frame = (javax.swing.JFrame) loginFrame;
                    frame.setVisible(true);

                    // Close after 2 seconds
                    new javax.swing.Timer(2000, e -> {
                        frame.dispose();
                        System.out.println("✅ Login UI test completed successfully!");
                        System.exit(0);
                    }).start();

                } catch (Exception e) {
                    System.err.println("❌ Error instantiating LoginFrame: " + e.getMessage());
                    e.printStackTrace();
                    System.exit(1);
                }
            });

        } catch (Exception e) {
            System.err.println("❌ Login UI test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
