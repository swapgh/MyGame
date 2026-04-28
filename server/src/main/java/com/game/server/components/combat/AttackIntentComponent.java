package com.game.server.components.combat;

/**
 * Marker set when a player requests an attack.
 *
 * @param requestedAtTick the last known world tick when the request was queued
 * @since 0.1.0
 */
public record AttackIntentComponent(long requestedAtTick) {
}
