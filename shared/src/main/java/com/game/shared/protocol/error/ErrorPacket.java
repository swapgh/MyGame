package com.game.shared.protocol.error;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Packet for reporting protocol or gameplay errors.
 * @param code the machine-readable error code
 * @param message the human-readable error message
 * @since 0.1.0
 */
public record ErrorPacket(String code, String message) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the error opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ERROR;
    }
}
