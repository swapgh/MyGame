package com.game.client.world.ecs;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Client-side entity identifier for visual ECS state.
 *
 * @param value the raw entity id
 * @since 0.1.0
 */
public record ClientEntityId(long value) {
    private static final AtomicLong NEXT_ID = new AtomicLong(1L);

    /**
     * Creates a new client entity id.
     *
     * @return a fresh client entity id
     */
    public static ClientEntityId next() {
        return new ClientEntityId(NEXT_ID.getAndIncrement());
    }
}
