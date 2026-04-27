package com.game.server.auth.sessions;

import java.time.Instant;

/**
 * Represents an authenticated session issued by the auth server.
 * @param token the session token
 * @param accountId the authenticated account identifier
 * @param createdAt the creation timestamp
 * @since 0.1.0
 */
public record AuthSession(String token, long accountId, Instant createdAt) {
}
