package com.game.shared.protocol.auth;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder account registration packet.
 *
 * @since 0.1.0
 */
public record RegisterRequestPacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the register request opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.REGISTER_REQUEST;
    }
}
