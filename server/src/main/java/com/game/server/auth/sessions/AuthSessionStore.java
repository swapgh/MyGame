package com.game.server.auth.sessions;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory session storage used during early auth server development.
 *
 * @since 0.1.0
 */
public final class AuthSessionStore {
    private final Map<String, AuthSession> sessions = new ConcurrentHashMap<>();

    /**
     * Stores or replaces a session by token.
     *
     * @param session the session to store
     */
    public void put(AuthSession session) {
        sessions.put(session.token(), session);
    }

    /**
     * Finds a session by token.
     *
     * @param token the session token
     * @return the stored session, if present
     */
    public Optional<AuthSession> find(String token) {
        return Optional.ofNullable(sessions.get(token));
    }

    /**
     * Removes a session by token.
     *
     * @param token the session token
     */
    public void remove(String token) {
        sessions.remove(token);
    }
}
