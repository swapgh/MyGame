package com.game.server.npc.definition;

import com.game.shared.protocol.world.EntityType;

/**
 * Data-driven NPC definition loaded from JSON.
 *
 * @param id unique NPC id
 * @param name display name
 * @param entityType replicated NPC type
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
        EntityType entityType,
        int maxHealth,
        int baseDamage,
        float attackRange,
        long attackCooldownTicks,
        float moveSpeed,
        float aggroRange,
        String lootTableId
) {
    /**
     * Creates an NPC definition defaulting to a hostile replicated NPC type.
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
     */
    public NpcDefinition(
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
        this(id, name, EntityType.NPC, maxHealth, baseDamage, attackRange, attackCooldownTicks, moveSpeed, aggroRange, lootTableId);
    }
}
