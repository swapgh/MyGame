package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder combat packet describing an attack action.
 * @since 0.1.0
 */
public record AttackPacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the attack opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ATTACK;
    }
}
