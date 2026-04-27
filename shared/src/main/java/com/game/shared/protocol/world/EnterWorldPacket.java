package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder packet for entering the world server.
 * @since 0.1.0
 */
public record EnterWorldPacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the enter world opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ENTER_WORLD;
    }
}
