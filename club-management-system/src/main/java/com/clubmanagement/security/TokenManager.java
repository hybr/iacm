package com.clubmanagement.security;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TokenManager {
    private static final int TOKEN_LENGTH = 32;
    private static final int TOKEN_EXPIRY_MINUTES = 30;
    private static final SecureRandom secureRandom = new SecureRandom();

    // In-memory storage for tokens (in production, use Redis or database)
    private static final Map<String, TokenInfo> activeTokens = new ConcurrentHashMap<>();

    public static class TokenInfo {
        private final String userId;
        private final String email;
        private final LocalDateTime expiryTime;
        private final TokenType type;

        public TokenInfo(String userId, String email, LocalDateTime expiryTime, TokenType type) {
            this.userId = userId;
            this.email = email;
            this.expiryTime = expiryTime;
            this.type = type;
        }

        public String getUserId() { return userId; }
        public String getEmail() { return email; }
        public LocalDateTime getExpiryTime() { return expiryTime; }
        public TokenType getType() { return type; }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }

    public enum TokenType {
        PASSWORD_RESET,
        EMAIL_VERIFICATION,
        TWO_FACTOR_AUTH
    }

    /**
     * Generate a secure token for password reset
     */
    public static String generatePasswordResetToken(String userId, String email) {
        String token = generateSecureToken();
        LocalDateTime expiryTime = LocalDateTime.now().plus(TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES);

        TokenInfo tokenInfo = new TokenInfo(userId, email, expiryTime, TokenType.PASSWORD_RESET);
        activeTokens.put(token, tokenInfo);

        return token;
    }

    /**
     * Validate a password reset token
     */
    public static TokenInfo validatePasswordResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        TokenInfo tokenInfo = activeTokens.get(token);
        if (tokenInfo == null) {
            return null; // Token not found
        }

        if (tokenInfo.isExpired()) {
            activeTokens.remove(token); // Clean up expired token
            return null; // Token expired
        }

        if (tokenInfo.getType() != TokenType.PASSWORD_RESET) {
            return null; // Wrong token type
        }

        return tokenInfo;
    }

    /**
     * Consume (invalidate) a token after successful use
     */
    public static boolean consumeToken(String token) {
        TokenInfo tokenInfo = activeTokens.remove(token);
        return tokenInfo != null && !tokenInfo.isExpired();
    }

    /**
     * Clean up expired tokens
     */
    public static void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        activeTokens.entrySet().removeIf(entry -> entry.getValue().getExpiryTime().isBefore(now));
    }

    /**
     * Generate a cryptographically secure random token
     */
    private static String generateSecureToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Invalidate all tokens for a specific user
     */
    public static void invalidateUserTokens(String userId) {
        activeTokens.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));
    }

    /**
     * Get token expiry time in minutes
     */
    public static int getTokenExpiryMinutes() {
        return TOKEN_EXPIRY_MINUTES;
    }

    /**
     * Check if a token exists and is valid (without consuming it)
     */
    public static boolean isTokenValid(String token) {
        TokenInfo tokenInfo = activeTokens.get(token);
        return tokenInfo != null && !tokenInfo.isExpired();
    }
}