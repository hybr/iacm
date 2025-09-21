package com.clubmanagement.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Hash a password with a randomly generated salt
     */
    public static HashedPassword hashPassword(String password) {
        byte[] salt = generateSalt();
        String hashedPassword = hashWithSalt(password, salt);
        String saltString = Base64.getEncoder().encodeToString(salt);

        return new HashedPassword(hashedPassword, saltString);
    }

    /**
     * Verify a password against a stored hash and salt
     */
    public static boolean verifyPassword(String password, String storedHash, String storedSalt) {
        try {
            byte[] salt = Base64.getDecoder().decode(storedSalt);
            String hashedInput = hashWithSalt(password, salt);
            return hashedInput.equals(storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate a random salt
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Hash password with given salt
     */
    private static String hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Check if password meets complexity requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        // Require at least 3 of the 4 criteria
        int criteriaCount = (hasUpper ? 1 : 0) + (hasLower ? 1 : 0) +
                           (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);
        return criteriaCount >= 3;
    }

    /**
     * Get password strength requirements message
     */
    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters long and contain at least 3 of the following:\n" +
               "• Uppercase letters (A-Z)\n" +
               "• Lowercase letters (a-z)\n" +
               "• Numbers (0-9)\n" +
               "• Special characters (!@#$%^&*)";
    }

    /**
     * Container class for hashed password and salt
     */
    public static class HashedPassword {
        private final String hash;
        private final String salt;

        public HashedPassword(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }

        public String getHash() { return hash; }
        public String getSalt() { return salt; }
    }
}