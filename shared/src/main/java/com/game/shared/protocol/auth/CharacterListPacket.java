package com.game.shared.protocol.auth;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder character list packet sent after authentication flow succeeds.
 *
 * @since 0.1.0
 */
public record CharacterListPacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the character list opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.CHARACTER_LIST;
    }
}
