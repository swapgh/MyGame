package com.game.shared.protocol.world;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Placeholder snapshot packet for world state replication.
 *
 * @since 0.1.0
 */
public record WorldSnapshotPacket() implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the world snapshot opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.WORLD_SNAPSHOT;
    }
}
