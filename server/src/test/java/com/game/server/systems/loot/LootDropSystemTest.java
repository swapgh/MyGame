package com.game.server.systems.loot;

import com.game.server.components.combat.HealthComponent;
import com.game.server.components.loot.DroppedLootComponent;
import com.game.server.components.loot.LootComponent;
import com.game.server.components.loot.LootDropStateComponent;
import com.game.server.components.npc.NpcComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.items.definition.ItemDefinition;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.EquipmentSlot;
import com.game.shared.time.GameClock;
import com.game.shared.time.TickRate;
import org.junit.jupiter.api.Test;

import java.util.Map;

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

        new LootDropSystem(Map.of(
                "slime_gel", new ItemDefinition("slime_gel", "Slime Gel", null, 0, 0.0f),
                "cloudy_core", new ItemDefinition("cloudy_core", "Cloudy Core", EquipmentSlot.TRINKET, 2, 8.0f)
        )).tick(entities, new GameClock(10L, TickRate.DEFAULT));

        assertTrue(entities.get(npcId, LootDropStateComponent.class).orElseThrow().droppedForCurrentLife());
        long droppedLootCount = entities.storeOf(DroppedLootComponent.class).all().size();
        assertEquals(1, droppedLootCount);
        DroppedLootComponent droppedLoot = entities.storeOf(DroppedLootComponent.class).all().iterator().next().getValue();
        assertEquals("slime_gel", droppedLoot.itemId());
        assertEquals("Slime Gel", droppedLoot.displayName());
    }
}
