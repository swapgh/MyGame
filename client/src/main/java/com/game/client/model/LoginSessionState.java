package com.game.client.model;

import java.util.List;

/**
 * Auth session plus the currently loaded character roster.
 *
 * @param session authenticated session
 * @param characterNames available character names
 * @since 0.1.0
 */
public record LoginSessionState(AuthenticatedSession session, List<String> characterNames) {
}
