package com.game.shared.protocol.world;

import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;

/**
 * Requests pickup of the nearest loot item for a player.
 *
 * @param playerEntityId the requesting player entity
 * @since 0.1.0
 */
public record PickupLootPacket(SharedEntityId playerEntityId) implements Packet {
    @Override
    public Opcode opcode() {
        return Opcode.PICKUP_LOOT;
    }
}
