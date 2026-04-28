package com.game.server.world.network.codec;

import com.game.shared.ids.SharedEntityId;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.EntityType;
import com.game.shared.protocol.world.WorldSnapshotPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Encodes and decodes world snapshot packets.
 *
 * @since 0.1.0
 */
public final class SnapshotPacketCodec {
    public String encode(WorldSnapshotPacket snapshot) {
        String entities = snapshot.entities().stream()
                .map(entity -> String.join(
                        ",",
                        Long.toString(entity.entityId().value()),
                        Float.toString(entity.position().x()),
                        Float.toString(entity.position().y()),
                        Float.toString(entity.velocity().x()),
                        Float.toString(entity.velocity().y()),
                        entity.entityType().name(),
                        CodecSupport.sanitize(entity.displayName()),
                        Integer.toString(entity.currentHealth()),
                        Integer.toString(entity.maxHealth()),
                        Boolean.toString(entity.alive()),
                        Long.toString(entity.respawnTicksRemaining())
                ))
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
        return String.join(
                "|",
                "WORLD_SNAPSHOT",
                Long.toString(snapshot.serverTick()),
                Long.toString(snapshot.playerEntityId().value()),
                entities
        );
    }

    public WorldSnapshotPacket decode(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4 || !"WORLD_SNAPSHOT".equals(parts[0])) {
            throw new IllegalArgumentException("Unexpected world snapshot: " + line);
        }

        List<EntitySpawnPacket> entities = new ArrayList<>();
        if (!parts[3].isBlank()) {
            String[] encodedEntities = parts[3].split(";", -1);
            for (String encodedEntity : encodedEntities) {
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
