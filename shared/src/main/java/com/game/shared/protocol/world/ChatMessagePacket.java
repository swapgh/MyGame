package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder chat packet shared between clients through the world server.
 *
 * @since 0.1.0
 */
public record ChatMessagePacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the chat message opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.CHAT_MESSAGE;
    }
}
