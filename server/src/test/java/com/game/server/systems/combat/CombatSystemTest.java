package com.game.server.systems.combat;

import com.game.server.components.combat.AttackIntentComponent;
import com.game.server.components.combat.CombatStateComponent;
import com.game.server.components.combat.CombatStatsComponent;
import com.game.server.components.combat.HealthComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.components.world.VelocityComponent;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;
import com.game.shared.time.TickRate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatSystemTest {
    @Test
    void attackIntentDamagesNearestLivingTargetWithinRange() {
        EntityManager entities = new EntityManager();
        EntityId attackerId = entities.create();
        EntityId targetId = entities.create();
        EntityId farTargetId = entities.create();

        entities.put(attackerId, new TransformComponent(new Vec2(0.0f, 0.0f)));
        entities.put(attackerId, new VelocityComponent(Vec2.ZERO));
        entities.put(attackerId, new HealthComponent(100, 100));
        entities.put(attackerId, new CombatStatsComponent(18, 90.0f, 10L));
        entities.put(attackerId, new CombatStateComponent(-100L));
        entities.put(attackerId, new RespawnComponent(new Vec2(0.0f, 0.0f), 60L, -1L));
        entities.put(attackerId, new AttackIntentComponent(100L));

        entities.put(targetId, new TransformComponent(new Vec2(30.0f, 0.0f)));
        entities.put(targetId, new VelocityComponent(Vec2.ZERO));
        entities.put(targetId, new HealthComponent(20, 20));
        entities.put(targetId, new RespawnComponent(new Vec2(30.0f, 0.0f), 60L, -1L));

        entities.put(farTargetId, new TransformComponent(new Vec2(150.0f, 0.0f)));
        entities.put(farTargetId, new VelocityComponent(Vec2.ZERO));
        entities.put(farTargetId, new HealthComponent(50, 50));
        entities.put(farTargetId, new RespawnComponent(new Vec2(150.0f, 0.0f), 60L, -1L));

        new CombatSystem().tick(entities, new GameClock(100L, TickRate.DEFAULT));

        assertEquals(1, entities.get(targetId, HealthComponent.class).orElseThrow().currentHealth());
        assertEquals(50, entities.get(farTargetId, HealthComponent.class).orElseThrow().currentHealth());
        assertEquals(100L, entities.get(attackerId, CombatStateComponent.class).orElseThrow().lastAttackTick());
        assertFalse(entities.has(attackerId, AttackIntentComponent.class));
    }

    @Test
    void lethalAttackStartsRespawnTimerAndStopsTargetMovement() {
        EntityManager entities = new EntityManager();
        EntityId attackerId = entities.create();
        EntityId targetId = entities.create();

        entities.put(attackerId, new TransformComponent(new Vec2(0.0f, 0.0f)));
        entities.put(attackerId, new VelocityComponent(Vec2.ZERO));
        entities.put(attackerId, new HealthComponent(100, 100));
        entities.put(attackerId, new CombatStatsComponent(18, 90.0f, 10L));
        entities.put(attackerId, new CombatStateComponent(-100L));
        entities.put(attackerId, new RespawnComponent(new Vec2(0.0f, 0.0f), 60L, -1L));
        entities.put(attackerId, new AttackIntentComponent(100L));

        entities.put(targetId, new TransformComponent(new Vec2(10.0f, 0.0f)));
        entities.put(targetId, new VelocityComponent(new Vec2(50.0f, 0.0f)));
        entities.put(targetId, new HealthComponent(10, 10));
        entities.put(targetId, new RespawnComponent(new Vec2(200.0f, 200.0f), 60L, -1L));

        new CombatSystem().tick(entities, new GameClock(100L, TickRate.DEFAULT));

        assertEquals(0, entities.get(targetId, HealthComponent.class).orElseThrow().currentHealth());
        assertTrue(entities.get(targetId, RespawnComponent.class).orElseThrow().waitingForRespawn());
        assertEquals(160L, entities.get(targetId, RespawnComponent.class).orElseThrow().respawnTick());
        assertEquals(Vec2.ZERO, entities.get(targetId, VelocityComponent.class).orElseThrow().velocity());
    }
}
