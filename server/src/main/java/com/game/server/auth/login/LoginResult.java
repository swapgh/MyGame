package com.game.server.auth.login;

/**
 * Describes the outcome of a login attempt.
 *
 * @since 0.1.0
 */
public enum LoginResult {
    SUCCESS,
    INVALID_CREDENTIALS,
    ACCOUNT_LOCKED,
    RATE_LIMITED,
    ERROR
}
