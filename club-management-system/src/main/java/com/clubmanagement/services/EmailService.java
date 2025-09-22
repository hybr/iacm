package com.clubmanagement.services;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailService {
    private static final String FROM_EMAIL = "noreply@clubmanagement.edu";
    private static final String SMTP_HOST = "smtp.clubmanagement.edu";
    private static final boolean EMAIL_ENABLED = false; // Set to true in production

    /**
     * Send password reset email with token
     */
    public static boolean sendPasswordResetEmail(String toEmail, String username, String resetToken) {
        String subject = "Password Reset Request - Club Management System";
        String body = buildPasswordResetEmailBody(username, resetToken);

        return sendEmail(toEmail, subject, body);
    }

    /**
     * Send welcome email for new user registration
     */
    public static boolean sendWelcomeEmail(String toEmail, String username, String role) {
        String subject = "Welcome to Club Management System";
        String body = buildWelcomeEmailBody(username, role);

        return sendEmail(toEmail, subject, body);
    }

    /**
     * Send club assignment notification
     */
    public static boolean sendClubAssignmentEmail(String toEmail, String username, String clubName) {
        String subject = "Club Assignment Confirmation";
        String body = buildClubAssignmentEmailBody(username, clubName);

        return sendEmail(toEmail, subject, body);
    }

    /**
     * Generic email sending method
     */
    private static boolean sendEmail(String toEmail, String subject, String body) {
        if (!EMAIL_ENABLED) {
            // For demo purposes, show email content in dialog
            showEmailPreview(toEmail, subject, body);
            return true;
        }

        // In production, implement actual SMTP email sending
        try {
            // TODO: Implement actual email sending using JavaMail API
            // Properties props = new Properties();
            // props.put("mail.smtp.host", SMTP_HOST);
            // props.put("mail.smtp.port", "587");
            // props.put("mail.smtp.auth", "true");
            // props.put("mail.smtp.starttls.enable", "true");

            // Session session = Session.getInstance(props, authenticator);
            // MimeMessage message = new MimeMessage(session);
            // message.setFrom(new InternetAddress(FROM_EMAIL));
            // message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            // message.setSubject(subject);
            // message.setText(body, "utf-8", "html");
            // Transport.send(message);

            return true;
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Show email preview for demo purposes
     */
    private static void showEmailPreview(String toEmail, String subject, String body) {
        SwingUtilities.invokeLater(() -> {
            JDialog emailDialog = new JDialog();
            emailDialog.setTitle("Email Sent - Demo Mode");
            emailDialog.setModal(true);
            emailDialog.setSize(600, 500);
            emailDialog.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            panel.add(new JLabel("Email sent to: " + toEmail));
            panel.add(Box.createVerticalStrut(10));
            panel.add(new JLabel("Subject: " + subject));
            panel.add(Box.createVerticalStrut(10));

            JTextArea textArea = new JTextArea(body);
            textArea.setEditable(false);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            JScrollPane scrollPane = new JScrollPane(textArea);

            panel.add(scrollPane);
            panel.add(Box.createVerticalStrut(10));

            // For password reset emails, extract and show the token
            if (subject.contains("Password Reset")) {
                String token = extractTokenFromBody(body);
                if (token != null) {
                    JPanel tokenPanel = new JPanel(new FlowLayout());
                    tokenPanel.add(new JLabel("Reset Token: "));
                    JTextField tokenField = new JTextField(token, 30);
                    tokenField.setEditable(false);
                    tokenPanel.add(tokenField);

                    JButton copyTokenButton = new JButton("Copy Token");
                    copyTokenButton.addActionListener(e -> {
                        Toolkit.getDefaultToolkit().getSystemClipboard()
                            .setContents(new StringSelection(token), null);
                        JOptionPane.showMessageDialog(emailDialog, "Token copied to clipboard!");
                    });
                    tokenPanel.add(copyTokenButton);

                    JButton openResetButton = new JButton("Open Reset Form");
                    openResetButton.addActionListener(e -> {
                        emailDialog.dispose();
                        SwingUtilities.invokeLater(() -> {
                            try {
                                Class<?> resetFrameClass = Class.forName("com.clubmanagement.gui.ResetPasswordFrame");
                                Object resetFrame = resetFrameClass.getConstructor(String.class).newInstance(token);
                                resetFrameClass.getMethod("setVisible", boolean.class).invoke(resetFrame, true);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null,
                                    "Error opening reset form: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    });
                    tokenPanel.add(openResetButton);

                    panel.add(tokenPanel);
                    panel.add(Box.createVerticalStrut(10));
                }
            }

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> emailDialog.dispose());
            panel.add(closeButton);

            emailDialog.add(panel);
            emailDialog.setVisible(true);
        });
    }

    /**
     * Extract token from password reset email body
     */
    private static String extractTokenFromBody(String body) {
        try {
            // Look for the token in the reset URL
            String pattern = "token=([^&'\"\\s<>]+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(body);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            // If extraction fails, return null
        }
        return null;
    }

    /**
     * Build password reset email body
     */
    private static String buildPasswordResetEmailBody(String username, String resetToken) {
        String resetUrl = "http://localhost:8080/reset-password?token=" + resetToken;

        return String.format(
            "<html><body style='font-family: Arial, sans-serif;'>" +
            "<h2>Password Reset Request</h2>" +
            "<p>Hello %s,</p>" +
            "<p>You have requested to reset your password for the Club Management System.</p>" +
            "<p>Please click the link below to reset your password:</p>" +
            "<p><a href='%s' style='background-color: #2B579A; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Reset Password</a></p>" +
            "<p>Or copy and paste this link in your browser:</p>" +
            "<p>%s</p>" +
            "<p><strong>Important:</strong></p>" +
            "<ul>" +
            "<li>This link will expire in 30 minutes</li>" +
            "<li>If you did not request this reset, please ignore this email</li>" +
            "<li>For security, do not share this link with anyone</li>" +
            "</ul>" +
            "<p>Best regards,<br>Club Management System</p>" +
            "<hr>" +
            "<p style='font-size: 12px; color: #666;'>This email was sent on %s</p>" +
            "</body></html>",
            username, resetUrl, resetUrl,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * Build welcome email body
     */
    private static String buildWelcomeEmailBody(String username, String role) {
        return String.format(
            "<html><body style='font-family: Arial, sans-serif;'>" +
            "<h2>Welcome to Club Management System!</h2>" +
            "<p>Hello %s,</p>" +
            "<p>Your account has been successfully created with the role: <strong>%s</strong></p>" +
            "<p>You can now log in to the system and start using the available features.</p>" +
            "<h3>Getting Started:</h3>" +
            "<ul>" +
            "<li>Log in with your username and password</li>" +
            "<li>Complete your profile information</li>" +
            "<li>Explore the features available to your role</li>" +
            "</ul>" +
            "<p>If you have any questions or need assistance, please contact the system administrator.</p>" +
            "<p>Best regards,<br>Club Management System</p>" +
            "</body></html>",
            username, role
        );
    }

    /**
     * Build club assignment email body
     */
    private static String buildClubAssignmentEmailBody(String username, String clubName) {
        return String.format(
            "<html><body style='font-family: Arial, sans-serif;'>" +
            "<h2>Club Assignment Confirmation</h2>" +
            "<p>Hello %s,</p>" +
            "<p>You have been successfully assigned to: <strong>%s</strong></p>" +
            "<p>You can now access club-specific features and participate in club activities.</p>" +
            "<p>To view your club information, log in to the system and navigate to 'My Club Info'.</p>" +
            "<p>Best regards,<br>Club Management System</p>" +
            "</body></html>",
            username, clubName
        );
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                           "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}