package com.game.server.world.network.codec;

import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.core.Packet;
import com.game.shared.protocol.error.ErrorPacket;
import com.game.shared.protocol.world.AttackPacket;
import com.game.shared.protocol.world.ChatMessagePacket;
import com.game.shared.protocol.world.EquipItemPacket;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.EnterWorldPacket;
import com.game.shared.protocol.world.PickupLootPacket;

/**
 * Decodes inbound world command packets.
 *
 * @since 0.1.0
 */
public final class InboundPacketCodec {
    public Packet decode(String line) {
        String[] parts = line.split("\\|", -1);
        String opcode = parts[0];
        return switch (opcode) {
            case "ENTER_WORLD" -> new EnterWorldPacket(CodecSupport.require(parts, 1));
            case "ENTITY_MOVE" -> new EntityMovePacket(
                    new SharedEntityId(Long.parseLong(CodecSupport.require(parts, 1))),
                    CodecSupport.decodeVec(CodecSupport.require(parts, 2)),
                    CodecSupport.decodeVec(CodecSupport.require(parts, 3))
            );
            case "ATTACK" -> new AttackPacket(
                    new SharedEntityId(Long.parseLong(CodecSupport.require(parts, 1)))
            );
            case "PICKUP_LOOT" -> new PickupLootPacket(
                    new SharedEntityId(Long.parseLong(CodecSupport.require(parts, 1)))
            );
            case "EQUIP_ITEM" -> new EquipItemPacket(
                    new SharedEntityId(Long.parseLong(CodecSupport.require(parts, 1))),
                    Integer.parseInt(CodecSupport.require(parts, 2))
            );
            case "CHAT_MESSAGE" -> new ChatMessagePacket();
            default -> new ErrorPacket("UNKNOWN_OPCODE", "Unsupported opcode: " + opcode);
        };
    }
}
