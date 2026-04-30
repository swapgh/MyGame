package com.game.client.model;

/**
 * Resolved primary action context for the world action key.
 *
 * @param actionType resolved action type
 * @param targetEntityId resolved target entity id, or {@code -1}
 * @param label user-facing action label
 * @since 0.1.0
 */
public record WorldActionContext(WorldActionType actionType, long targetEntityId, String label) {
    /**
     * Returns an empty action context.
     *
     * @return the empty action context
     */
    public static WorldActionContext none() {
        return new WorldActionContext(WorldActionType.NONE, -1L, "No action");
    }
}
