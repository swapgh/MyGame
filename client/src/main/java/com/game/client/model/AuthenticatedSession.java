package com.game.client.model;

/**
 * Authenticated client session issued by the auth server.
 *
 * @param accountId authenticated account id
 * @param sessionToken issued session token
 * @since 0.1.0
 */
public record AuthenticatedSession(long accountId, String sessionToken) {
}
