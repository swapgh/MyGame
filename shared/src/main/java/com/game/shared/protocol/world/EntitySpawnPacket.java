package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.math.Vec2;

/**
 * Packet describing an entity spawn or snapshot entry.
 * @param entityId the shared entity id
 * @param position the current world position
 * @param velocity the current world velocity
 * @since 0.1.0
 */
public record EntitySpawnPacket(
        SharedEntityId entityId,
        Vec2 position,
        Vec2 velocity
) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the entity spawn opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ENTITY_SPAWN;
    }
}
