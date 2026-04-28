package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Packet requesting entry into the world server with a selected character.
 * @param characterName the selected character name
 * @since 0.1.0
 */
public record EnterWorldPacket(String characterName) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the enter world opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ENTER_WORLD;
    }
}
