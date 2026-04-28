package com.game.server.world.systems;

import com.game.server.world.components.HealthComponent;
import com.game.server.world.components.RespawnComponent;
import com.game.server.world.components.TransformComponent;
import com.game.server.world.components.VelocityComponent;
import com.game.server.world.ecs.EntityId;
import com.game.server.world.ecs.EntityManager;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;
import com.game.shared.time.TickRate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RespawnSystemTest {
    @Test
    void respawnRestoresHealthPositionAndVelocity() {
        EntityManager entities = new EntityManager();
        EntityId entityId = entities.create();

        entities.put(entityId, new HealthComponent(0, 100));
        entities.put(entityId, new TransformComponent(new Vec2(420.0f, 420.0f)));
        entities.put(entityId, new VelocityComponent(new Vec2(12.0f, -7.0f)));
        entities.put(entityId, new RespawnComponent(new Vec2(64.0f, 96.0f), 60L, 10L));

        new RespawnSystem().tick(entities, new GameClock(10L, TickRate.DEFAULT));

        assertEquals(100, entities.get(entityId, HealthComponent.class).orElseThrow().currentHealth());
        assertEquals(new Vec2(64.0f, 96.0f), entities.get(entityId, TransformComponent.class).orElseThrow().position());
        assertEquals(Vec2.ZERO, entities.get(entityId, VelocityComponent.class).orElseThrow().velocity());
        assertFalse(entities.get(entityId, RespawnComponent.class).orElseThrow().waitingForRespawn());
    }
}
