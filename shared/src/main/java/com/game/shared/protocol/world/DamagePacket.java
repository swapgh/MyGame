package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder combat packet describing damage application.
 * @since 0.1.0
 */
public record DamagePacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the damage opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.DAMAGE;
    }
}
