package com.game.shared.protocol.world;

import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

/**
 * Packet requesting interaction with a world entity such as a vendor.
 *
 * @param playerEntityId the interacting player
 * @param targetEntityId the interaction target entity
 * @since 0.1.0
 */
public record InteractPacket(SharedEntityId playerEntityId, SharedEntityId targetEntityId) implements Packet {
    @Override
    public Opcode opcode() {
        return Opcode.INTERACT;
    }
}
