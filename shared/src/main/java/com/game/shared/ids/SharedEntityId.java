package com.game.shared.ids;

/**
 * Shared entity identifier used when client and server refer to the same entity.
 * @param value the unique entity identifier
 * @since 0.1.0
 */
public record SharedEntityId(long value) {
    /**
     * Creates a shared entity id and validates that it is non-negative.
     */
    public SharedEntityId {
        if (value < 0L) {
            throw new IllegalArgumentException("value cannot be negative");
        }
    }
}
