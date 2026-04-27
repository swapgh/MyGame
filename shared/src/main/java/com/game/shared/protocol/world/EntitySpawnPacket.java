package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder packet describing an entity spawn event.
 *
 * @since 0.1.0
 */
public record EntitySpawnPacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the entity spawn opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ENTITY_SPAWN;
    }
}
