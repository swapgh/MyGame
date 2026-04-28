package com.game.shared.protocol.world;

import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;
import com.game.shared.ecs.SharedEntityId;

import java.util.List;

/**
 * Snapshot packet for world state replication.
 * @param serverTick the authoritative world tick
 * @param playerEntityId the local player's entity id
 * @param entities the visible entity states
 * @since 0.1.0
 */
public record WorldSnapshotPacket(
        long serverTick,
        SharedEntityId playerEntityId,
        List<EntitySpawnPacket> entities
) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the world snapshot opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.WORLD_SNAPSHOT;
    }
}
