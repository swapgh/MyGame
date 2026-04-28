package com.game.server.world.inventory;

import com.game.server.components.combat.BaseCombatStatsComponent;
import com.game.server.components.combat.CombatStatsComponent;
import com.game.server.components.inventory.EquipmentComponent;
import com.game.server.components.inventory.InventoryComponent;
import com.game.server.components.inventory.InventoryEntry;
import com.game.server.components.loot.DroppedLootComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.items.definition.ItemDefinition;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.shared.protocol.world.EquipmentSlot;
import com.game.shared.protocol.world.EquippedItemPacket;
import com.game.shared.protocol.world.InventoryItemPacket;
import com.game.shared.protocol.world.InventoryUpdatePacket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Authoritative inventory and equipment operations for player entities.
 *
 * @since 0.1.0
 */
public final class InventoryService {
    private static final float PICKUP_RANGE = 50.0f;

    private final Map<String, ItemDefinition> itemDefinitions;

    /**
     * Creates a service backed by loaded item definitions.
     *
     * @param itemDefinitions item definitions keyed by id
     */
    public InventoryService(Map<String, ItemDefinition> itemDefinitions) {
        this.itemDefinitions = Map.copyOf(itemDefinitions);
    }

    /**
     * Attempts to pick up the nearest loot entity in range.
     *
     * @param entities the authoritative entity manager
     * @param playerEntityId the player entity
     * @return {@code true} when an item was picked up
     */
    public boolean pickupNearbyLoot(EntityManager entities, EntityId playerEntityId) {
        TransformComponent playerTransform = entities.get(playerEntityId, TransformComponent.class).orElse(null);
        InventoryComponent inventory = entities.get(playerEntityId, InventoryComponent.class).orElse(null);
        if (playerTransform == null || inventory == null) {
            return false;
        }

        EntityId lootEntityId = entities.storeOf(DroppedLootComponent.class).all().stream()
                .filter(entry -> entities.get(entry.getKey(), TransformComponent.class).isPresent())
                .map(entry -> {
                    TransformComponent lootTransform = entities.get(entry.getKey(), TransformComponent.class).orElseThrow();
                    float distanceSquared = playerTransform.position().subtract(lootTransform.position()).lengthSquared();
                    if (distanceSquared > PICKUP_RANGE * PICKUP_RANGE) {
                        return null;
                    }
                    return new LootCandidate(entry.getKey(), distanceSquared);
                })
                .filter(candidate -> candidate != null)
                .min(Comparator.comparing(LootCandidate::distanceSquared))
                .map(LootCandidate::entityId)
                .orElse(null);
        if (lootEntityId == null) {
            return false;
        }

        DroppedLootComponent droppedLoot = entities.get(lootEntityId, DroppedLootComponent.class).orElse(null);
        if (droppedLoot == null || !itemDefinitions.containsKey(droppedLoot.itemId())) {
            return false;
        }

        InventoryComponent updatedInventory = addItem(inventory, droppedLoot.itemId());
        if (updatedInventory == null) {
            return false;
        }

        entities.put(playerEntityId, updatedInventory);
        entities.destroy(lootEntityId);
        return true;
    }

    /**
     * Attempts to equip the inventory entry in the requested slot.
     *
     * @param entities the authoritative entity manager
     * @param playerEntityId the player entity
     * @param inventorySlotIndex zero-based inventory slot index
     * @return {@code true} when equipment changed
     */
    public boolean equipInventorySlot(EntityManager entities, EntityId playerEntityId, int inventorySlotIndex) {
        InventoryComponent inventory = entities.get(playerEntityId, InventoryComponent.class).orElse(null);
        EquipmentComponent equipment = entities.get(playerEntityId, EquipmentComponent.class).orElse(null);
        BaseCombatStatsComponent baseCombatStats = entities.get(playerEntityId, BaseCombatStatsComponent.class).orElse(null);
        if (inventory == null || equipment == null || baseCombatStats == null) {
            return false;
        }
        if (inventorySlotIndex < 0 || inventorySlotIndex >= inventory.items().size()) {
            return false;
        }

        InventoryEntry selectedEntry = inventory.items().get(inventorySlotIndex);
        ItemDefinition definition = itemDefinitions.get(selectedEntry.itemId());
        if (definition == null || !definition.equippable()) {
            return false;
        }

        List<InventoryEntry> items = new ArrayList<>(inventory.items());
        EnumMap<EquipmentSlot, String> equippedItemIds = new EnumMap<>(EquipmentSlot.class);
        equippedItemIds.putAll(equipment.equippedItemIds());
        EquipmentSlot slot = definition.equipmentSlot();
        items.set(inventorySlotIndex, decrementOrRemove(selectedEntry));
        items.removeIf(entry -> entry == null);

        String previouslyEquippedItemId = equippedItemIds.put(slot, selectedEntry.itemId());
        if (previouslyEquippedItemId != null) {
            InventoryComponent swappedInventory = addItem(new InventoryComponent(inventory.capacity(), items), previouslyEquippedItemId);
            if (swappedInventory == null) {
                return false;
            }
            items = new ArrayList<>(swappedInventory.items());
        }

        entities.put(playerEntityId, new InventoryComponent(inventory.capacity(), items));
        entities.put(playerEntityId, new EquipmentComponent(equippedItemIds));
        applyCombatBonuses(entities, playerEntityId, baseCombatStats, equippedItemIds);
        return true;
    }

    /**
     * Builds the packet sent to clients after inventory changes.
     *
     * @param entities the authoritative entity manager
     * @param playerEntityId the player entity
     * @return the inventory update packet, or {@code null} if the player is missing required components
     */
    public InventoryUpdatePacket buildInventoryUpdate(EntityManager entities, EntityId playerEntityId) {
        InventoryComponent inventory = entities.get(playerEntityId, InventoryComponent.class).orElse(null);
        EquipmentComponent equipment = entities.get(playerEntityId, EquipmentComponent.class).orElse(null);
        if (inventory == null || equipment == null) {
            return null;
        }

        List<InventoryItemPacket> inventoryItems = new ArrayList<>();
        for (int index = 0; index < inventory.items().size(); index++) {
            InventoryEntry entry = inventory.items().get(index);
            ItemDefinition definition = itemDefinitions.get(entry.itemId());
            String displayName = definition != null ? definition.name() : entry.itemId();
            inventoryItems.add(new InventoryItemPacket(
                    index,
                    entry.itemId(),
                    displayName,
                    entry.quantity(),
                    definition != null && definition.equippable(),
                    definition != null ? definition.equipmentSlot() : null
            ));
        }

        List<EquippedItemPacket> equippedItems = equipment.equippedItemIds().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    ItemDefinition definition = itemDefinitions.get(entry.getValue());
                    String displayName = definition != null ? definition.name() : entry.getValue();
                    return new EquippedItemPacket(entry.getKey(), entry.getValue(), displayName);
                })
                .toList();

        return new InventoryUpdatePacket(inventory.capacity(), inventoryItems, equippedItems);
    }

    private InventoryComponent addItem(InventoryComponent inventory, String itemId) {
        List<InventoryEntry> items = new ArrayList<>(inventory.items());
        for (int index = 0; index < items.size(); index++) {
            InventoryEntry entry = items.get(index);
            if (entry.itemId().equals(itemId)) {
                items.set(index, new InventoryEntry(itemId, entry.quantity() + 1));
                return new InventoryComponent(inventory.capacity(), items);
            }
        }
        if (items.size() >= inventory.capacity()) {
            return null;
        }
        items.add(new InventoryEntry(itemId, 1));
        return new InventoryComponent(inventory.capacity(), items);
    }

    private void applyCombatBonuses(
            EntityManager entities,
            EntityId playerEntityId,
            BaseCombatStatsComponent baseCombatStats,
            Map<EquipmentSlot, String> equippedItemIds
    ) {
        int baseDamage = baseCombatStats.baseDamage();
        float attackRange = baseCombatStats.attackRange();
        for (String itemId : equippedItemIds.values()) {
            ItemDefinition definition = itemDefinitions.get(itemId);
            if (definition == null) {
                continue;
            }
            baseDamage += definition.baseDamageBonus();
            attackRange += definition.attackRangeBonus();
        }
        entities.put(playerEntityId, new CombatStatsComponent(
                Math.max(1, baseDamage),
                Math.max(1.0f, attackRange),
                baseCombatStats.attackCooldownTicks()
        ));
    }

    private static InventoryEntry decrementOrRemove(InventoryEntry entry) {
        if (entry.quantity() == 1) {
            return null;
        }
        return new InventoryEntry(entry.itemId(), entry.quantity() - 1);
    }

    private record LootCandidate(EntityId entityId, float distanceSquared) {
    }
}
