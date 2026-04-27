package com.game.shared.protocol.auth;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Authentication request packet carrying login credentials.
 *
 * @param username the account username
 * @param password the raw password supplied by the client
 * @since 0.1.0
 */
public record LoginRequestPacket(String username, String password) implements Packet {
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
