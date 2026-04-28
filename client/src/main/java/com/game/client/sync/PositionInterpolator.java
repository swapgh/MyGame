package com.game.client.sync;

/**
 * Advances interpolated client sync positions.
 *
 * @since 0.1.0
 */
public final class PositionInterpolator {
    /**
     * Advances the client world view one frame.
     *
     * @param worldSyncState the client world sync state
     * @param deltaSeconds frame delta in seconds
     */
    public void advance(WorldSyncState worldSyncState, float deltaSeconds) {
        worldSyncState.advance(deltaSeconds);
    }
}
