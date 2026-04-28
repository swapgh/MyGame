package com.game.shared.protocol.auth;

import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

/**
 * Character creation response packet.
 *
 * @param success whether creation succeeded
 * @param message the result message
 * @param accountId the owning account id
 * @param characterName the created character name, or an empty string on failure
 * @since 0.1.0
 */
public record CharacterCreateResponsePacket(
        boolean success,
        String message,
        long accountId,
        String characterName
) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the character create response opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.CHARACTER_CREATE_RESPONSE;
    }
}
