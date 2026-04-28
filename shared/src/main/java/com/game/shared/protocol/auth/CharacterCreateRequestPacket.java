package com.game.shared.protocol.auth;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Character creation request packet.
 *
 * @param accountId the owning account id
 * @param characterName the requested character name
 * @since 0.1.0
 */
public record CharacterCreateRequestPacket(long accountId, String characterName) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the character create request opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.CHARACTER_CREATE_REQUEST;
    }
}
