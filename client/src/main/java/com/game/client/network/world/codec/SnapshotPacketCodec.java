package com.game.client.network.world.codec;

import com.game.shared.ids.SharedEntityId;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.EntityType;
import com.game.shared.protocol.world.WorldSnapshotPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes world snapshot packets on the client.
 *
 * @since 0.1.0
 */
public final class SnapshotPacketCodec {
    public WorldSnapshotPacket decode(String line) {
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
                if (entityParts.length != 11) {
                    throw new IllegalArgumentException("Malformed entity snapshot: " + encodedEntity);
                }
                entities.add(new EntitySpawnPacket(
                        new SharedEntityId(Long.parseLong(entityParts[0])),
                        new Vec2(Float.parseFloat(entityParts[1]), Float.parseFloat(entityParts[2])),
                        new Vec2(Float.parseFloat(entityParts[3]), Float.parseFloat(entityParts[4])),
                        EntityType.valueOf(entityParts[5]),
                        entityParts[6],
                        Integer.parseInt(entityParts[7]),
                        Integer.parseInt(entityParts[8]),
                        Boolean.parseBoolean(entityParts[9]),
                        Long.parseLong(entityParts[10])
                ));
            }
        }

        return new WorldSnapshotPacket(
                Long.parseLong(parts[1]),
                new SharedEntityId(Long.parseLong(parts[2])),
                entities
        );
    }
}
