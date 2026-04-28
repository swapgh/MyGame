package com.game.server.world.systems;

import com.game.server.world.components.DroppedLootComponent;
import com.game.server.world.components.HealthComponent;
import com.game.server.world.components.LootComponent;
import com.game.server.world.components.LootDropStateComponent;
import com.game.server.world.components.NpcComponent;
import com.game.server.world.components.RespawnComponent;
import com.game.server.world.components.TransformComponent;
import com.game.server.world.ecs.EntityId;
import com.game.server.world.ecs.EntityManager;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;
import com.game.shared.time.TickRate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LootDropSystemTest {
    @Test
    void spawnsDroppedLootOnceForDeadNpc() {
        EntityManager entities = new EntityManager();
        EntityId npcId = entities.create();

        entities.put(npcId, new NpcComponent("training-slime", "Training Slime"));
        entities.put(npcId, new HealthComponent(0, 40));
        entities.put(npcId, new RespawnComponent(new Vec2(100.0f, 100.0f), 140L, 20L));
        entities.put(npcId, new TransformComponent(new Vec2(100.0f, 100.0f)));
        entities.put(npcId, new LootComponent("slime-basic", java.util.List.of("slime_gel", "cloudy_core")));
        entities.put(npcId, new LootDropStateComponent(false));

        new LootDropSystem().tick(entities, new GameClock(10L, TickRate.DEFAULT));

        assertTrue(entities.get(npcId, LootDropStateComponent.class).orElseThrow().droppedForCurrentLife());
        long droppedLootCount = entities.storeOf(DroppedLootComponent.class).all().size();
        assertEquals(1, droppedLootCount);
        DroppedLootComponent droppedLoot = entities.storeOf(DroppedLootComponent.class).all().iterator().next().getValue();
        assertEquals("slime_gel", droppedLoot.itemId());
    }
}
