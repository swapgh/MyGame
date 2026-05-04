package com.game.shared.protocol.world;

import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

/**
 * Server-to-client interaction feedback message.
 *
 * @param message user-facing interaction result text
 * @since 0.1.0
 */
public record InteractionMessagePacket(String message) implements Packet {
    @Override
    public Opcode opcode() {
        return Opcode.INTERACTION_MESSAGE;
    }
}
