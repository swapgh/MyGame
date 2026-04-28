package com.game.server.world.network;

import com.game.shared.protocol.core.Packet;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.protocol.error.ErrorPacket;
import com.game.shared.protocol.world.AttackPacket;
import com.game.shared.protocol.world.ChatMessagePacket;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.EnterWorldPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;
import com.game.shared.math.Vec2;

import java.util.ArrayList;
import java.util.List;
/**
 * Simple line-based codec for the world server, using the same pipe-delimited
 * {@code OPCODE|field1|field2|...} format as {@code AuthPacketCodec}.
 * @since 0.1.0
 */
public final class WorldPacketCodec {
    /**
     * Decodes a pipe-delimited line into a world packet.
     * @param line the incoming protocol line
     * @return the decoded packet
     */
    public Packet decode(String line) {
        String[] parts = line.split("\\|", -1);
        String opcode = parts[0];
        return switch (opcode) {
            case "ENTER_WORLD"  -> new EnterWorldPacket(require(parts, 1));
            case "ENTITY_MOVE" -> new EntityMovePacket(
                    new SharedEntityId(Long.parseLong(require(parts, 1))),
                    decodeVec(require(parts, 2)),
                    decodeVec(require(parts, 3))
            );
            case "ATTACK" -> new AttackPacket(
                    new SharedEntityId(Long.parseLong(require(parts, 1)))
            );
            case "CHAT_MESSAGE" -> new ChatMessagePacket();
            default -> new ErrorPacket("UNKNOWN_OPCODE", "Unsupported opcode: " + opcode);
        };
    }
    /**
     * Encodes a world packet into a pipe-delimited line.
     * @param packet the packet to encode
     * @return the encoded protocol line
     */
    public String encode(Packet packet) {
        if (packet instanceof WorldSnapshotPacket snapshot) {
            String entities = snapshot.entities().stream()
                    .map(entity -> String.join(
                            ",",
                            Long.toString(entity.entityId().value()),
                            Float.toString(entity.position().x()),
                            Float.toString(entity.position().y()),
                            Float.toString(entity.velocity().x()),
                            Float.toString(entity.velocity().y()),
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
        if (packet instanceof EntityMovePacket movePacket) {
            return String.join(
                    "|",
                    "ENTITY_MOVE",
                    Long.toString(movePacket.entityId().value()),
                    encodeVec(movePacket.position()),
                    encodeVec(movePacket.velocity())
            );
        }
        if (packet instanceof ErrorPacket error) {
            return String.join("|", "ERROR", error.code(), error.message());
        }
        throw new IllegalArgumentException("Unsupported packet type: " + packet.getClass().getName());
    }

    private static String require(String[] parts, int index) {
        if (index >= parts.length) {
            throw new IllegalArgumentException("Missing field at index " + index);
        }
        return parts[index];
    }

    private static String encodeVec(Vec2 vec2) {
        return vec2.x() + "," + vec2.y();
    }

    private static Vec2 decodeVec(String encoded) {
        String[] parts = encoded.split(",", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Malformed vector: " + encoded);
        }
        return new Vec2(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
    }

    /**
     * Decodes a world snapshot line on the client side.
     * @param line the encoded snapshot line
     * @return the decoded snapshot packet
     */
    public WorldSnapshotPacket decodeSnapshot(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4 || !"WORLD_SNAPSHOT".equals(parts[0])) {
            throw new IllegalArgumentException("Unexpected world snapshot: " + line);
        }

        List<EntitySpawnPacket> entities = new ArrayList<>();
        if (!parts[3].isBlank()) {
            String[] encodedEntities = parts[3].split(";", -1);
            for (String encodedEntity : encodedEntities) {
                String[] entityParts = encodedEntity.split(",", -1);
                if (entityParts.length != 9) {
                    throw new IllegalArgumentException("Malformed entity snapshot: " + encodedEntity);
                }
                entities.add(new EntitySpawnPacket(
                        new SharedEntityId(Long.parseLong(entityParts[0])),
                        new Vec2(Float.parseFloat(entityParts[1]), Float.parseFloat(entityParts[2])),
                        new Vec2(Float.parseFloat(entityParts[3]), Float.parseFloat(entityParts[4])),
                        Integer.parseInt(entityParts[5]),
                        Integer.parseInt(entityParts[6]),
                        Boolean.parseBoolean(entityParts[7]),
                        Long.parseLong(entityParts[8])
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
