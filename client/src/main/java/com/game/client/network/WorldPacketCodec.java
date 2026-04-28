package com.game.client.network;

import com.game.shared.ecs.SharedEntityId;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Line-based world packet codec used by the early client movement flow.
 *
 * @since 0.1.0
 */
public final class WorldPacketCodec {
    /**
     * Encodes a movement packet for the world server.
     *
     * @param packet the movement packet
     * @return the encoded protocol line
     */
    public String encode(EntityMovePacket packet) {
        return String.join(
                "|",
                "ENTITY_MOVE",
                Long.toString(packet.entityId().value()),
                encodeVec(packet.position()),
                encodeVec(packet.velocity())
        );
    }

    /**
     * Decodes a world snapshot line.
     *
     * @param line the encoded protocol line
     * @return the decoded snapshot packet
     */
    public WorldSnapshotPacket decodeSnapshot(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Missing world snapshot.");
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length < 4 || !"WORLD_SNAPSHOT".equals(parts[0])) {
            throw new IllegalArgumentException("Unexpected world response: " + line);
        }

        List<EntitySpawnPacket> entities = new ArrayList<>();
        if (!parts[3].isBlank()) {
            for (String encodedEntity : parts[3].split(";", -1)) {
                String[] entityParts = encodedEntity.split(",", -1);
                if (entityParts.length != 5) {
                    throw new IllegalArgumentException("Malformed entity snapshot: " + encodedEntity);
                }
                entities.add(new EntitySpawnPacket(
                        new SharedEntityId(Long.parseLong(entityParts[0])),
                        new Vec2(Float.parseFloat(entityParts[1]), Float.parseFloat(entityParts[2])),
                        new Vec2(Float.parseFloat(entityParts[3]), Float.parseFloat(entityParts[4]))
                ));
            }
        }

        return new WorldSnapshotPacket(
                Long.parseLong(parts[1]),
                new SharedEntityId(Long.parseLong(parts[2])),
                entities
        );
    }

    private static String encodeVec(Vec2 vec2) {
        return vec2.x() + "," + vec2.y();
    }
}
