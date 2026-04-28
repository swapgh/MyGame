package com.game.server.world.factories;

import com.game.server.world.components.AiComponent;
import com.game.server.world.components.AiState;
import com.game.server.world.components.AiStateComponent;
import com.game.server.world.components.CombatStateComponent;
import com.game.server.world.components.CombatStatsComponent;
import com.game.server.world.components.HealthComponent;
import com.game.server.world.components.LootComponent;
import com.game.server.world.components.LootDropStateComponent;
import com.game.server.world.components.NpcComponent;
import com.game.server.world.components.RespawnComponent;
import com.game.server.world.components.TransformComponent;
import com.game.server.world.components.VelocityComponent;
import com.game.server.world.definitions.LootTableDefinition;
import com.game.server.world.definitions.NpcDefinition;
import com.game.server.world.definitions.NpcSpawnEntry;
import com.game.server.world.ecs.EntityId;
import com.game.server.world.ecs.EntityManager;
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
