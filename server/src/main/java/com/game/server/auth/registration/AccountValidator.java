package com.game.server.auth.registration;

import com.game.shared.util.Validation;

/**
 * Performs basic account input validation for registration flows.
 *
 * @since 0.1.0
 */
public final class AccountValidator {
    /**
     * Validates a username for basic presence and length.
     *
     * @param username the username to validate
     * @return the validation result
     */
    public Validation validateUsername(String username) {
        if (username == null || username.isBlank()) {
            return Validation.failure("Username cannot be blank");
        }
        if (username.length() < 3) {
            return Validation.failure("Username must be at least 3 characters");
        }
        return Validation.ok();
    }

    /**
     * Validates a password for basic presence and length.
     *
     * @param password the password to validate
     * @return the validation result
     */
    public Validation validatePassword(String password) {
        if (password == null || password.isBlank()) {
            return Validation.failure("Password cannot be blank");
        }
        if (password.length() < 8) {
            return Validation.failure("Password must be at least 8 characters");
        }
        return Validation.ok();
    }
}
