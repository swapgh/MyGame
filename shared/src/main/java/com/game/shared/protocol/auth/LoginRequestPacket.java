package com.game.shared.protocol.auth;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder authentication request packet.
 *
 * @since 0.1.0
 */
public record LoginRequestPacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the login request opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.LOGIN_REQUEST;
    }
}
