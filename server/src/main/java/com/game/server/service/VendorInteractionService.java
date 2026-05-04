package com.game.server.service;

import com.game.server.components.npc.NpcComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.shared.protocol.world.EntityType;

/**
 * Resolves simple vendor interaction requests.
 *
 * @since 0.1.0
 */
public final class VendorInteractionService {
    private static final float INTERACTION_RANGE = 90.0f;

    /**
     * Attempts interaction with the requested vendor target.
     *
     * @param entities world entity manager
     * @param playerEntityId player entity id
     * @param targetEntityId requested interaction target
     * @return the interaction message, or {@code null} if interaction is invalid
     */
    public String interact(EntityManager entities, EntityId playerEntityId, EntityId targetEntityId) {
        TransformComponent playerTransform = entities.get(playerEntityId, TransformComponent.class).orElse(null);
        TransformComponent targetTransform = entities.get(targetEntityId, TransformComponent.class).orElse(null);
        NpcComponent npc = entities.get(targetEntityId, NpcComponent.class).orElse(null);
        if (playerTransform == null || targetTransform == null || npc == null || npc.entityType() != EntityType.VENDOR) {
            return null;
        }

        float distanceSquared = playerTransform.position().subtract(targetTransform.position()).lengthSquared();
        if (distanceSquared > (INTERACTION_RANGE * INTERACTION_RANGE)) {
            return null;
        }

        return "Vendor " + npc.displayName() + ": Welcome. Trading will be added next.";
    }
}
