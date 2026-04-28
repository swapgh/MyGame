package com.game.server.world.definitions;

/**
 * Data-driven NPC definition loaded from JSON.
 *
 * @param id unique NPC id
 * @param name display name
 * @param maxHealth maximum health
 * @param baseDamage base damage
 * @param attackRange attack range
 * @param attackCooldownTicks attack cooldown in ticks
 * @param moveSpeed movement speed
 * @param aggroRange chase radius
 * @param lootTableId referenced loot table id
 * @since 0.1.0
 */
public record NpcDefinition(
        String id,
        String name,
        int maxHealth,
        int baseDamage,
        float attackRange,
        long attackCooldownTicks,
        float moveSpeed,
        float aggroRange,
        String lootTableId
) {
}
