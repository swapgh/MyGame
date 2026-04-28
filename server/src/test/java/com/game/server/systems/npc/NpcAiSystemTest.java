package com.game.server.systems.npc;

import com.game.server.components.combat.AttackIntentComponent;
import com.game.server.components.combat.CombatStatsComponent;
import com.game.server.components.combat.HealthComponent;
import com.game.server.components.npc.AiComponent;
import com.game.server.components.npc.AiState;
import com.game.server.components.npc.AiStateComponent;
import com.game.server.components.npc.NpcComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.PlayerComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.components.world.VelocityComponent;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;
import com.game.shared.time.TickRate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NpcAiSystemTest {
    @Test
    void movesTowardNearbyPlayerOutsideAttackRange() {
        EntityManager entities = new EntityManager();
        EntityId npcId = entities.create();
        EntityId playerId = entities.create();

        entities.put(npcId, new NpcComponent("training-slime", "Training Slime"));
        entities.put(npcId, new AiComponent(55.0f, 170.0f, 90.0f));
        entities.put(npcId, new AiStateComponent(AiState.IDLE));
        entities.put(npcId, new CombatStatsComponent(6, 50.0f, 18L));
        entities.put(npcId, new HealthComponent(40, 40));
        entities.put(npcId, new RespawnComponent(new Vec2(100.0f, 100.0f), 140L, -1L));
        entities.put(npcId, new TransformComponent(new Vec2(100.0f, 100.0f)));
        entities.put(npcId, new VelocityComponent(Vec2.ZERO));

        entities.put(playerId, new PlayerComponent("DevKnight"));
        entities.put(playerId, new HealthComponent(100, 100));
        entities.put(playerId, new RespawnComponent(new Vec2(180.0f, 100.0f), 60L, -1L));
        entities.put(playerId, new TransformComponent(new Vec2(180.0f, 100.0f)));

        new NpcAiSystem().tick(entities, new GameClock(10L, TickRate.DEFAULT));

        assertTrue(entities.get(npcId, VelocityComponent.class).orElseThrow().velocity().x() > 0.0f);
        assertEquals(AiState.CHASE, entities.get(npcId, AiStateComponent.class).orElseThrow().state());
    }

    @Test
    void queuesAttackWhenPlayerIsInRange() {
        EntityManager entities = new EntityManager();
        EntityId npcId = entities.create();
        EntityId playerId = entities.create();

        entities.put(npcId, new NpcComponent("training-slime", "Training Slime"));
        entities.put(npcId, new AiComponent(55.0f, 170.0f, 90.0f));
        entities.put(npcId, new AiStateComponent(AiState.IDLE));
        entities.put(npcId, new CombatStatsComponent(6, 50.0f, 18L));
        entities.put(npcId, new HealthComponent(40, 40));
        entities.put(npcId, new RespawnComponent(new Vec2(100.0f, 100.0f), 140L, -1L));
        entities.put(npcId, new TransformComponent(new Vec2(100.0f, 100.0f)));
        entities.put(npcId, new VelocityComponent(Vec2.ZERO));

        entities.put(playerId, new PlayerComponent("DevKnight"));
        entities.put(playerId, new HealthComponent(100, 100));
        entities.put(playerId, new RespawnComponent(new Vec2(130.0f, 100.0f), 60L, -1L));
        entities.put(playerId, new TransformComponent(new Vec2(130.0f, 100.0f)));

        new NpcAiSystem().tick(entities, new GameClock(10L, TickRate.DEFAULT));

        assertEquals(Vec2.ZERO, entities.get(npcId, VelocityComponent.class).orElseThrow().velocity());
        assertTrue(entities.has(npcId, AttackIntentComponent.class));
        assertEquals(AiState.CHASE, entities.get(npcId, AiStateComponent.class).orElseThrow().state());
    }
}
