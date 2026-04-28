package com.game.server.world.components;

/**
 * Combat tuning data used when resolving attacks.
 *
 * @param baseDamage the base damage dealt by each attack
 * @param attackRange the maximum attack range in world units
 * @param attackCooldownTicks the minimum ticks between attacks
 * @since 0.1.0
 */
public record CombatStatsComponent(
        int baseDamage,
        float attackRange,
        long attackCooldownTicks
) {
    public CombatStatsComponent {
        if (baseDamage <= 0) {
            throw new IllegalArgumentException("baseDamage must be greater than zero");
        }
        if (attackRange <= 0.0f) {
            throw new IllegalArgumentException("attackRange must be greater than zero");
        }
        if (attackCooldownTicks < 0L) {
            throw new IllegalArgumentException("attackCooldownTicks cannot be negative");
        }
    }
}
