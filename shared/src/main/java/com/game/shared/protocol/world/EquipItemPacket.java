package com.game.shared.protocol.world;

import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

/**
 * Requests equipping an inventory slot for a player.
 *
 * @param playerEntityId the requesting player entity
 * @param inventorySlotIndex zero-based inventory slot index
 * @since 0.1.0
 */
public record EquipItemPacket(
        SharedEntityId playerEntityId,
        int inventorySlotIndex
) implements Packet {
    @Override
    public Opcode opcode() {
        return Opcode.EQUIP_ITEM;
    }
}
