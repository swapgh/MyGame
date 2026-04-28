package com.game.server.world.components;

import com.game.shared.math.Vec2;

/**
 * Tracks where an entity respawns and whether it is currently waiting to respawn.
 *
 * @param spawnPosition the respawn position
 * @param respawnDelayTicks the respawn delay in ticks
 * @param respawnTick the scheduled respawn tick, or {@code -1} when alive
 * @since 0.1.0
 */
public record RespawnComponent(
        Vec2 spawnPosition,
        long respawnDelayTicks,
        long respawnTick
) {
    public RespawnComponent {
        if (respawnDelayTicks < 0L) {
            throw new IllegalArgumentException("respawnDelayTicks cannot be negative");
        }
        if (respawnTick < -1L) {
            throw new IllegalArgumentException("respawnTick cannot be less than -1");
        }
    }

    /**
     * Returns whether the entity is currently waiting to respawn.
     *
     * @return {@code true} when the respawn timer is active
     */
    public boolean waitingForRespawn() {
        return respawnTick >= 0L;
    }
}
