package com.game.shared.protocol.world;

import com.game.shared.ecs.SharedEntityId;
import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

/**
 * Packet describing a player's attack request.
 *
 * @param attackerEntityId the attacking entity
 * @since 0.1.0
 */
public record AttackPacket(SharedEntityId attackerEntityId) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the attack opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ATTACK;
    }
}
