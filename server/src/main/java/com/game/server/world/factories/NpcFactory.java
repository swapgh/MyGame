package com.game.server.world.factories;

import com.game.server.components.combat.CombatStateComponent;
import com.game.server.components.combat.CombatStatsComponent;
import com.game.server.components.combat.HealthComponent;
import com.game.server.components.loot.LootComponent;
import com.game.server.components.loot.LootDropStateComponent;
import com.game.server.components.npc.AiComponent;
import com.game.server.components.npc.AiState;
import com.game.server.components.npc.AiStateComponent;
import com.game.server.components.npc.NpcComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.components.world.VelocityComponent;
import com.game.server.loot.definition.LootTableDefinition;
import com.game.server.npc.definition.NpcDefinition;
import com.game.server.npc.definition.NpcSpawnEntry;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.shared.math.Vec2;

/**
 * Builds NPC entities from loaded definitions and spawn data.
 *
 * @since 0.1.0
 */
public final class NpcFactory {
    /**
     * Creates one NPC entity from the provided definition data.
     *
     * @param entities the world entity manager
     * @param definition the NPC definition
     * @param spawnEntry the spawn-table entry
     * @param spawnPosition the resolved spawn position
     * @param lootTable the resolved loot table
     * @return the created NPC entity id
     */
    public EntityId createNpc(
            EntityManager entities,
            NpcDefinition definition,
            NpcSpawnEntry spawnEntry,
            Vec2 spawnPosition,
            LootTableDefinition lootTable
    ) {
        EntityId entityId = entities.create();
        entities.put(entityId, new TransformComponent(spawnPosition));
        entities.put(entityId, new VelocityComponent(Vec2.ZERO));
        entities.put(entityId, new NpcComponent(definition.id(), definition.name()));
        entities.put(entityId, new HealthComponent(definition.maxHealth(), definition.maxHealth()));
        entities.put(
                entityId,
                new CombatStatsComponent(
                        definition.baseDamage(),
                        definition.attackRange(),
                        definition.attackCooldownTicks()
                )
        );
        entities.put(entityId, new CombatStateComponent(-100L));
        entities.put(
                entityId,
                new RespawnComponent(spawnPosition, spawnEntry.respawnDelayTicks(), -1L)
        );
        entities.put(
                entityId,
                new AiComponent(definition.moveSpeed(), definition.aggroRange(), spawnEntry.roamRadius())
        );
        entities.put(entityId, new AiStateComponent(AiState.IDLE));
        entities.put(
                entityId,
                new LootComponent(
                        lootTable.id(),
                        lootTable.drops()
                )
        );
        entities.put(entityId, new LootDropStateComponent(false));
        return entityId;
    }
}
