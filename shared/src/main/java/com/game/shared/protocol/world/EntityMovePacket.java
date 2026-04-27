package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder packet describing entity movement updates.
 *
 * @since 0.1.0
 */
public record EntityMovePacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the entity move opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ENTITY_MOVE;
    }
}
