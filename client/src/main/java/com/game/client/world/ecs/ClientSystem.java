package com.game.client.world.ecs;

/**
 * Processing step for client-side visual ECS state.
 *
 * @since 0.1.0
 */
public interface ClientSystem {
    /**
     * Advances one client frame.
     *
     * @param context the shared client world context
     * @param deltaSeconds frame delta in seconds
     */
    void tick(ClientWorldContext context, float deltaSeconds);
}
