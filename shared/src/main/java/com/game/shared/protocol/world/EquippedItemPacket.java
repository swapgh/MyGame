package com.game.shared.protocol.world;

/**
 * Describes one equipped item visible to the client.
 *
 * @param equipmentSlot the occupied equipment slot
 * @param itemId stable item id
 * @param displayName user-facing item name
 * @since 0.1.0
 */
public record EquippedItemPacket(
        EquipmentSlot equipmentSlot,
        String itemId,
        String displayName
) {
}
