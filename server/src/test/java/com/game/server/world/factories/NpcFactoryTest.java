package com.game.server.world.factories;

import com.game.server.world.components.AiComponent;
import com.game.server.world.components.AiState;
import com.game.server.world.components.AiStateComponent;
import com.game.server.world.components.CombatStatsComponent;
import com.game.server.world.components.HealthComponent;
import com.game.server.world.components.LootComponent;
import com.game.server.world.components.NpcComponent;
import com.game.server.world.components.RespawnComponent;
import com.game.server.world.ecs.EntityId;
import com.game.server.world.ecs.EntityManager;
import com.game.server.world.definitions.LootTableDefinition;
import com.game.server.world.definitions.NpcDefinition;
import com.game.server.world.definitions.NpcSpawnEntry;
import com.game.shared.math.Vec2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NpcFactoryTest {
    @Test
    void createsNpcWithCombatAiAndLootComponents() {
        EntityManager entities = new EntityManager();
        NpcFactory factory = new NpcFactory();

        NpcDefinition definition = new NpcDefinition(
                "training-slime",
                "Training Slime",
                40,
                6,
                50.0f,
                18L,
                55.0f,
                170.0f,
                "slime-basic"
        );
        NpcSpawnEntry spawnEntry = new NpcSpawnEntry(1, "training-slime", 2, 520.0f, 300.0f, 60.0f, 140L, 90.0f);
        LootTableDefinition lootTable = new LootTableDefinition("slime-basic", List.of("slime_gel", "cloudy_core"));

        EntityId entityId = factory.createNpc(entities, definition, spawnEntry, new Vec2(520.0f, 300.0f), lootTable);

        assertEquals("training-slime", entities.get(entityId, NpcComponent.class).orElseThrow().definitionId());
        assertEquals(40, entities.get(entityId, HealthComponent.class).orElseThrow().maxHealth());
        assertEquals(6, entities.get(entityId, CombatStatsComponent.class).orElseThrow().baseDamage());
        assertEquals(170.0f, entities.get(entityId, AiComponent.class).orElseThrow().aggroRange());
        assertEquals(AiState.IDLE, entities.get(entityId, AiStateComponent.class).orElseThrow().state());
        assertEquals(140L, entities.get(entityId, RespawnComponent.class).orElseThrow().respawnDelayTicks());
        assertEquals(List.of("slime_gel", "cloudy_core"), entities.get(entityId, LootComponent.class).orElseThrow().drops());
    }
}
