package com.game.shared.protocol.world;

import com.game.shared.protocol.core.Opcode;
import com.game.shared.protocol.core.Packet;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.math.Vec2;

/**
 * Packet describing an entity spawn or snapshot entry.
 * @param entityId the shared entity id
 * @param position the current world position
 * @param velocity the current world velocity
 * @param entityType the high-level kind of entity
 * @param displayName the display name for UI and debugging
 * @param currentHealth the entity's current health
 * @param maxHealth the entity's maximum health
 * @param alive whether the entity is currently alive
 * @param respawnTicksRemaining ticks remaining until respawn, or {@code 0} when alive
 * @since 0.1.0
 */
public record EntitySpawnPacket(
        SharedEntityId entityId,
        Vec2 position,
        Vec2 velocity,
        EntityType entityType,
        String displayName,
        int currentHealth,
        int maxHealth,
        boolean alive,
        long respawnTicksRemaining
) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     * @return the entity spawn opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.ENTITY_SPAWN;
    }
}
