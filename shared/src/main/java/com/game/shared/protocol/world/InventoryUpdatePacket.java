package com.game.shared.protocol.world;

import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

import java.util.List;

/**
 * Describes the authoritative player inventory and equipment state.
 *
 * @param capacity total inventory capacity
 * @param inventoryItems inventory stacks in slot order
 * @param equippedItems currently equipped items
 * @since 0.1.0
 */
public record InventoryUpdatePacket(
        int capacity,
        List<InventoryItemPacket> inventoryItems,
        List<EquippedItemPacket> equippedItems
) implements Packet {
    public InventoryUpdatePacket {
        inventoryItems = List.copyOf(inventoryItems);
        equippedItems = List.copyOf(equippedItems);
    }

    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the inventory update opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.INVENTORY_UPDATE;
    }
}
