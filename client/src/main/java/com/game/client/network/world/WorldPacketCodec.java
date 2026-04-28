package com.game.client.network.world;

import com.game.client.network.world.codec.CommandPacketCodec;
import com.game.client.network.world.codec.InventoryUpdateCodec;
import com.game.client.network.world.codec.SnapshotPacketCodec;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.protocol.world.AttackPacket;
import com.game.shared.protocol.world.EquipItemPacket;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.InventoryUpdatePacket;
import com.game.shared.protocol.world.PickupLootPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;
import com.game.shared.protocol.core.Packet;

/**
 * Line-based world packet codec used by the early client movement flow.
 *
 * @since 0.1.0
 */
public final class WorldPacketCodec {
    private final CommandPacketCodec commandPacketCodec = new CommandPacketCodec();
    private final SnapshotPacketCodec snapshotPacketCodec = new SnapshotPacketCodec();
    private final InventoryUpdateCodec inventoryUpdateCodec = new InventoryUpdateCodec();

    /**
     * Encodes a movement packet for the world server.
     *
     * @param packet the movement packet
     * @return the encoded protocol line
     */
    public String encode(EntityMovePacket packet) {
        return commandPacketCodec.encode(packet);
    }

    /**
     * Encodes an attack packet for the world server.
     *
     * @param packet the attack packet
     * @return the encoded protocol line
     */
    public String encode(AttackPacket packet) {
        return commandPacketCodec.encode(packet);
    }

    /**
     * Encodes a loot pickup request for the world server.
     *
     * @param packet the pickup request
     * @return the encoded protocol line
     */
    public String encode(PickupLootPacket packet) {
        return commandPacketCodec.encode(packet);
    }

    /**
     * Encodes an equip request for the world server.
     *
     * @param packet the equip request
     * @return the encoded protocol line
     */
    public String encode(EquipItemPacket packet) {
        return commandPacketCodec.encode(packet);
    }

    /**
     * Decodes a world snapshot line.
     *
     * @param line the encoded protocol line
     * @return the decoded snapshot packet
     */
    public WorldSnapshotPacket decodeSnapshot(String line) {
        return snapshotPacketCodec.decode(line);
    }

    /**
     * Decodes an inventory update line.
     *
     * @param line the encoded protocol line
     * @return the decoded inventory update packet
     */
    public InventoryUpdatePacket decodeInventoryUpdate(String line) {
        return inventoryUpdateCodec.decode(line);
    }

    /**
     * Decodes a mixed world packet line for the background reader.
     *
     * @param line the encoded protocol line
     * @return the decoded packet
     */
    public Packet decode(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Missing world packet.");
        }
        if (line.startsWith("WORLD_SNAPSHOT|")) {
            return decodeSnapshot(line);
        }
        if (line.startsWith("INVENTORY_UPDATE|")) {
            return decodeInventoryUpdate(line);
        }
        throw new IllegalArgumentException("Unexpected world response: " + line);
    }

}
