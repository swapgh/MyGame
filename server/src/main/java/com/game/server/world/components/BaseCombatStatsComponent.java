package com.game.server.world.components;

/**
 * Stores a player's pre-equipment combat baseline.
 *
 * @param baseDamage base outgoing damage
 * @param attackRange base attack range
 * @param attackCooldownTicks base attack cooldown
 * @since 0.1.0
 */
public record BaseCombatStatsComponent(
        int baseDamage,
        float attackRange,
        long attackCooldownTicks
) {
}
