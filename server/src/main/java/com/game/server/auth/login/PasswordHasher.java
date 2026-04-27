package com.game.server.auth.login;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Minimal password hashing utility for early auth server development.
 *
 * <p>This implementation is intentionally temporary. A stronger password hashing strategy such as
 * BCrypt or Argon2 should replace it before the auth server is considered production-ready.</p>
 *
 * @since 0.1.0
 */
public final class PasswordHasher {
    /**
     * Hashes a password string using SHA-256 for placeholder auth flows.
     *
     * @param password the raw password
     * @return the hexadecimal hash string
     */
    public String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    /**
     * Verifies a raw password against a previously hashed value.
     *
     * @param password the raw password
     * @param expectedHash the stored password hash
     * @return {@code true} if the password matches the stored hash
     */
    public boolean matches(String password, String expectedHash) {
        return hash(password).equals(expectedHash);
    }
}
