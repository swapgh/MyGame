package com.game.shared.protocol.auth;

import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;
/**
 * Account registration request packet.
 * @param username the requested account username
 * @param password the requested raw password
 * @since 0.1.0
 */
public record RegisterRequestPacket(String username, String password) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the register request opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.REGISTER_REQUEST;
    }
}
