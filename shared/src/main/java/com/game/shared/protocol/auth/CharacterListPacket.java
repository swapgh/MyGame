package com.game.shared.protocol.auth;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

import java.util.List;

/**
 * Character list packet sent after authentication flow succeeds.
 *
 * @param accountId the owning account id
 * @param characterNames the names of characters owned by the account
 * @since 0.1.0
 */
public record CharacterListPacket(long accountId, List<String> characterNames) implements Packet {
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
