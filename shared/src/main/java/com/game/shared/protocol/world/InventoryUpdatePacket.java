package com.game.shared.protocol.world;

import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

/**
 * Placeholder packet describing inventory updates sent by the world server.
 * @since 0.1.0
 */
public record InventoryUpdatePacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the inventory update opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.INVENTORY_UPDATE;
    }
}
