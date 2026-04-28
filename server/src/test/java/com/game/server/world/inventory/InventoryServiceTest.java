package com.game.server.world.inventory;

import com.game.server.world.components.BaseCombatStatsComponent;
import com.game.server.world.components.CombatStatsComponent;
import com.game.server.world.components.DroppedLootComponent;
import com.game.server.world.components.EquipmentComponent;
import com.game.server.world.components.InventoryComponent;
import com.game.server.world.components.InventoryEntry;
import com.game.server.world.components.TransformComponent;
import com.game.server.world.definitions.ItemDefinition;
import com.game.server.world.ecs.EntityId;
import com.game.server.world.ecs.EntityManager;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.EquipmentSlot;
import com.game.shared.protocol.world.InventoryUpdatePacket;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryServiceTest {
    @Test
    void picksUpNearbyLootAndStacksItIntoInventory() {
        EntityManager entities = new EntityManager();
        EntityId playerId = entities.create();
        EntityId lootId = entities.create();

        entities.put(playerId, new TransformComponent(new Vec2(100.0f, 100.0f)));
        entities.put(playerId, new InventoryComponent(8, List.of(new InventoryEntry("slime_gel", 1))));
        entities.put(playerId, new EquipmentComponent(new EnumMap<>(EquipmentSlot.class)));
        entities.put(playerId, new BaseCombatStatsComponent(18, 90.0f, 10L));
        entities.put(playerId, new CombatStatsComponent(18, 90.0f, 10L));

        entities.put(lootId, new TransformComponent(new Vec2(120.0f, 100.0f)));
        entities.put(lootId, new DroppedLootComponent("slime_gel", "Slime Gel", "training-slime"));

        InventoryService service = new InventoryService(itemDefinitions());

        assertTrue(service.pickupNearbyLoot(entities, playerId));
        assertEquals(2, entities.get(playerId, InventoryComponent.class).orElseThrow().items().getFirst().quantity());
        assertEquals(0, entities.storeOf(DroppedLootComponent.class).size());
    }

    @Test
    void equipsInventoryItemAndAppliesCombatBonuses() {
        EntityManager entities = new EntityManager();
        EntityId playerId = entities.create();

        entities.put(playerId, new InventoryComponent(8, List.of(
                new InventoryEntry("sharp_fang", 1),
                new InventoryEntry("slime_gel", 2)
        )));
        entities.put(playerId, new EquipmentComponent(new EnumMap<>(EquipmentSlot.class)));
        entities.put(playerId, new BaseCombatStatsComponent(18, 90.0f, 10L));
        entities.put(playerId, new CombatStatsComponent(18, 90.0f, 10L));

        InventoryService service = new InventoryService(itemDefinitions());

        assertTrue(service.equipInventorySlot(entities, playerId, 0));
        assertEquals(1, entities.get(playerId, InventoryComponent.class).orElseThrow().items().size());
        assertEquals("sharp_fang", entities.get(playerId, EquipmentComponent.class).orElseThrow()
                .equippedItemIds().get(EquipmentSlot.WEAPON));
        assertEquals(22, entities.get(playerId, CombatStatsComponent.class).orElseThrow().baseDamage());
        assertEquals(102.0f, entities.get(playerId, CombatStatsComponent.class).orElseThrow().attackRange());

        InventoryUpdatePacket updatePacket = service.buildInventoryUpdate(entities, playerId);
        assertEquals(1, updatePacket.inventoryItems().size());
        assertEquals(1, updatePacket.equippedItems().size());
        assertEquals("Sharp Fang", updatePacket.equippedItems().getFirst().displayName());
    }

    private static Map<String, ItemDefinition> itemDefinitions() {
        return Map.of(
                "slime_gel", new ItemDefinition("slime_gel", "Slime Gel", null, 0, 0.0f),
                "sharp_fang", new ItemDefinition("sharp_fang", "Sharp Fang", EquipmentSlot.WEAPON, 4, 12.0f)
        );
    }
}
