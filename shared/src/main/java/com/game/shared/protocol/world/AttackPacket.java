package com.game.shared.protocol.world;

import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

/**
 * Packet describing a player's attack request.
 *
 * @param attackerEntityId the attacking entity
 * @param targetEntityId the selected target entity, or {@code null} to let the server auto-pick
 * @since 0.1.0
 */
public record AttackPacket(SharedEntityId attackerEntityId, SharedEntityId targetEntityId) implements Packet {
    /**
     * Creates an untargeted attack request that lets the server choose a target.
     *
     * @param attackerEntityId the attacking entity
     */
    public AttackPacket(SharedEntityId attackerEntityId) {
        this(attackerEntityId, null);
    }

    /**
     * Returns the opcode associated with this packet type.
     * @return the attack opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ATTACK;
    }
}
