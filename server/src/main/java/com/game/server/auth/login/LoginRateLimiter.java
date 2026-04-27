package com.game.server.auth.login;

/**
 * Placeholder rate limiter for authentication attempts.
 * @since 0.1.0
 */
public final class LoginRateLimiter {
    /**
     * Returns whether a login attempt should be allowed.
     * @param clientKey the client identifier, such as an ip address or account name
     * @return {@code true} while rate limiting is not yet enforced
     */
    public boolean allow(String clientKey) {
        return true;
    }
}
