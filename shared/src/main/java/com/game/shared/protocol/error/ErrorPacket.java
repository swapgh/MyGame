package com.game.shared.protocol.error;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder packet for reporting protocol or gameplay errors.
 *
 * @since 0.1.0
 */
public record ErrorPacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the error opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ERROR;
    }
}
