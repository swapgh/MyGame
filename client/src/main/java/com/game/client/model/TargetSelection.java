package com.game.client.model;

/**
 * Client-side selected world target state.
 *
 * @param entityId selected entity id
 * @param displayName selected entity display name
 * @since 0.1.0
 */
public record TargetSelection(long entityId, String displayName) {
}
