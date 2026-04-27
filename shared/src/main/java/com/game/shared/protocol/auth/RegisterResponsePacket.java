package com.game.shared.protocol.auth;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Account registration response packet.
 * @param success whether registration succeeded
 * @param message the human-readable registration result
 * @since 0.1.0
 */
public record RegisterResponsePacket(boolean success, String message) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the register response opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.REGISTER_RESPONSE;
    }
}
