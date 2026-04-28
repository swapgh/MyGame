package com.game.shared.protocol.world;

/**
 * Describes one inventory stack visible to the client.
 *
 * @param slotIndex zero-based inventory slot index
 * @param itemId stable item id
 * @param displayName user-facing item name
 * @param quantity stack size
 * @param equippable whether this item can be equipped
 * @param equipmentSlot the supported equipment slot, or {@code null}
 * @since 0.1.0
 */
public record InventoryItemPacket(
        int slotIndex,
        String itemId,
        String displayName,
        int quantity,
        boolean equippable,
        EquipmentSlot equipmentSlot
) {
}
