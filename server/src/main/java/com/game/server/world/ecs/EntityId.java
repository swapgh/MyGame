package com.game.server.world.ecs;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Monotonically increasing entity identifier for world server entities.
 * <p>Instances are produced by {@link EntityManager} and are opaque to callers.
 * Zero is reserved and never issued.</p>
 * @param value the raw numeric identifier
 * @since 0.1.0
 */
public record EntityId(long value) {
    private static final AtomicLong SEQUENCE = new AtomicLong(1L);

    /**Reserved sentinel used to represent "no entity".*/
    public static final EntityId NONE = new EntityId(0L);

    public EntityId {
        if (value < 0) {
            throw new IllegalArgumentException("EntityId value cannot be negative");
        }
    }

    static EntityId next(){
        return new EntityId(SEQUENCE.getAndIncrement());
    }

    @Override
    public String toString() {
        return "Entity#" + value;
    }
}
