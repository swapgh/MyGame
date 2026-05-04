package com.game.server.components.combat;

import com.game.server.ecs.entity.EntityId;

/**
 * Marker set when a player requests an attack.
 *
 * @param requestedAtTick the last known world tick when the request was queued
 * @param targetEntityId the requested target, or {@code null} for server auto-selection
 * @since 0.1.0
 */
public record AttackIntentComponent(long requestedAtTick, EntityId targetEntityId) {
    /**
     * Creates an untargeted attack intent that lets the server choose a target.
     *
     * @param requestedAtTick the tick when the request was queued
     */
    public AttackIntentComponent(long requestedAtTick) {
        this(requestedAtTick, null);
    }
}
