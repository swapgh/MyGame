package com.game.client.network.world.codec;

import com.game.shared.protocol.world.AttackPacket;
import com.game.shared.protocol.world.EquipItemPacket;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.InteractPacket;
import com.game.shared.protocol.world.PickupLootPacket;

/**
 * Encodes outbound world command packets from the client.
 *
 * @since 0.1.0
 */
public final class CommandPacketCodec {
    public String encode(EntityMovePacket packet) {
        return String.join(
                "|",
                "ENTITY_MOVE",
                Long.toString(packet.entityId().value()),
                CodecSupport.encodeVec(packet.position()),
                CodecSupport.encodeVec(packet.velocity())
        );
    }

    public String encode(AttackPacket packet) {
        return String.join(
                "|",
                "ATTACK",
                Long.toString(packet.attackerEntityId().value()),
                packet.targetEntityId() == null ? "" : Long.toString(packet.targetEntityId().value())
        );
    }

    public String encode(PickupLootPacket packet) {
        return String.join("|", "PICKUP_LOOT", Long.toString(packet.playerEntityId().value()));
    }

    public String encode(InteractPacket packet) {
        return String.join(
                "|",
                "INTERACT",
                Long.toString(packet.playerEntityId().value()),
                Long.toString(packet.targetEntityId().value())
        );
    }

    public String encode(EquipItemPacket packet) {
        return String.join(
                "|",
                "EQUIP_ITEM",
                Long.toString(packet.playerEntityId().value()),
                Integer.toString(packet.inventorySlotIndex())
        );
    }
}
