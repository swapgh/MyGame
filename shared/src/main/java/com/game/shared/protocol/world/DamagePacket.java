package com.game.shared.protocol.world;

import com.game.shared.ecs.SharedEntityId;
import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

/**
 * Packet describing authoritative damage application.
 *
 * @param attackerEntityId the attacking entity
 * @param targetEntityId the damaged entity
 * @param damage the damage dealt
 * @since 0.1.0
 */
public record DamagePacket(
        SharedEntityId attackerEntityId,
        SharedEntityId targetEntityId,
        int damage
) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the damage opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.DAMAGE;
    }
}
