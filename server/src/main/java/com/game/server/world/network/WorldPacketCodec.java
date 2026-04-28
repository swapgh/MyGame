package com.game.server.world.network;

import com.game.server.world.network.codec.InboundPacketCodec;
import com.game.server.world.network.codec.InventoryUpdateCodec;
import com.game.server.world.network.codec.SnapshotPacketCodec;
import com.game.shared.protocol.core.Packet;
import com.game.shared.protocol.error.ErrorPacket;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.InventoryUpdatePacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;
/**
 * Simple line-based codec for the world server, using the same pipe-delimited
 * {@code OPCODE|field1|field2|...} format as {@code AuthPacketCodec}.
 * @since 0.1.0
 */
public final class WorldPacketCodec {
    private final InboundPacketCodec inboundPacketCodec = new InboundPacketCodec();
    private final SnapshotPacketCodec snapshotPacketCodec = new SnapshotPacketCodec();
    private final InventoryUpdateCodec inventoryUpdateCodec = new InventoryUpdateCodec();

    /**
     * Decodes a pipe-delimited line into a world packet.
     * @param line the incoming protocol line
     * @return the decoded packet
     */
    public Packet decode(String line) {
        return inboundPacketCodec.decode(line);
    }
    /**
     * Encodes a world packet into a pipe-delimited line.
     * @param packet the packet to encode
     * @return the encoded protocol line
     */
    public String encode(Packet packet) {
        if (packet instanceof WorldSnapshotPacket snapshot) {
            return snapshotPacketCodec.encode(snapshot);
        }
        if (packet instanceof EntityMovePacket movePacket) {
            return String.join(
                    "|",
                    "ENTITY_MOVE",
                    Long.toString(movePacket.entityId().value()),
                    com.game.server.world.network.codec.CodecSupport.encodeVec(movePacket.position()),
                    com.game.server.world.network.codec.CodecSupport.encodeVec(movePacket.velocity())
            );
        }
        if (packet instanceof ErrorPacket error) {
            return String.join("|", "ERROR", error.code(), error.message());
        }
        if (packet instanceof InventoryUpdatePacket inventoryUpdate) {
            return inventoryUpdateCodec.encode(inventoryUpdate);
        }
        throw new IllegalArgumentException("Unsupported packet type: " + packet.getClass().getName());
    }

    /**
     * Decodes a world snapshot line on the client side.
     * @param line the encoded snapshot line
     * @return the decoded snapshot packet
     */
    public WorldSnapshotPacket decodeSnapshot(String line) {
        return snapshotPacketCodec.decode(line);
    }

    /**
     * Decodes an inventory update line.
     *
     * @param line the encoded inventory update line
     * @return the decoded inventory update packet
     */
    public InventoryUpdatePacket decodeInventoryUpdate(String line) {
        return inventoryUpdateCodec.decode(line);
    }
}
