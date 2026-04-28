package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.math.Vec2;

/**
 * Packet describing either client movement intent or a server movement update.
 * @param entityId the shared entity id
 * @param position the current or authoritative position
 * @param velocity the desired or authoritative velocity
 * @since 0.1.0
 */
public record EntityMovePacket(
        SharedEntityId entityId,
        Vec2 position,
        Vec2 velocity
) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the entity move opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ENTITY_MOVE;
    }
}
