package com.game.server.world.components;

/**
 * Mutable combat state that changes over time.
 *
 * @param lastAttackTick the world tick when this entity last attacked
 * @since 0.1.0
 */
public record CombatStateComponent(long lastAttackTick) {
}
