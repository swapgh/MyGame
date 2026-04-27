package com.game.server.auth.sessions;

import java.util.UUID;

/**
 * Generates session tokens for authenticated clients.
 *
 * @since 0.1.0
 */
public final class SessionTokenService {
    /**
     * Generates a random session token.
     *
     * @return a new opaque session token
     */
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
