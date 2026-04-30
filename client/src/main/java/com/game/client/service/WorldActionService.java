package com.game.client.service;

import com.game.client.model.TargetSelection;
import com.game.client.model.WorldActionContext;
import com.game.client.model.WorldActionType;
import com.game.client.world.sync.EntitySyncState;
import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.world.EntityType;

/**
 * Resolves the primary world action for the current player context.
 *
 * @since 0.1.0
 */
public final class WorldActionService {
    private static final float VENDOR_INTERACTION_RANGE = 90.0f;

    /**
     * Resolves the current primary action.
     *
     * @param playerEntityId local player entity id
     * @param entities visible world entities
     * @param currentTarget currently selected target, if any
     * @return the resolved action context
     */
    public WorldActionContext resolve(
            SharedEntityId playerEntityId,
            Iterable<EntitySyncState> entities,
            TargetSelection currentTarget
    ) {
        EntitySyncState player = null;
        EntitySyncState nearestVendor = null;
        float vendorDistanceSquared = Float.MAX_VALUE;
        EntitySyncState selectedHostile = null;

        for (EntitySyncState entity : entities) {
            if (entity.entityId() == playerEntityId.value()) {
                player = entity;
            } else if (entity.entityType() == EntityType.VENDOR && entity.alive()) {
                if (player != null) {
                    float distanceSquared = distanceSquared(player, entity);
                    if (distanceSquared <= (VENDOR_INTERACTION_RANGE * VENDOR_INTERACTION_RANGE)
                            && distanceSquared < vendorDistanceSquared) {
                        nearestVendor = entity;
                        vendorDistanceSquared = distanceSquared;
                    }
                }
            }
            if (currentTarget != null && entity.entityId() == currentTarget.entityId() && isHostile(entity)) {
                selectedHostile = entity;
            }
        }

        if (player == null) {
            return WorldActionContext.none();
        }

        if (nearestVendor == null) {
            for (EntitySyncState entity : entities) {
                if (entity.entityId() == playerEntityId.value()) {
                    continue;
                }
                if (entity.entityType() == EntityType.VENDOR && entity.alive()) {
                    float distanceSquared = distanceSquared(player, entity);
                    if (distanceSquared <= (VENDOR_INTERACTION_RANGE * VENDOR_INTERACTION_RANGE)
                            && distanceSquared < vendorDistanceSquared) {
                        nearestVendor = entity;
                        vendorDistanceSquared = distanceSquared;
                    }
                }
            }
        }

        if (nearestVendor != null) {
            return new WorldActionContext(
                    WorldActionType.INTERACT_VENDOR,
                    nearestVendor.entityId(),
                    "E interact with " + nearestVendor.displayName()
            );
        }
        if (selectedHostile != null) {
            return new WorldActionContext(
                    WorldActionType.ATTACK_TARGET,
                    selectedHostile.entityId(),
                    "E attack " + selectedHostile.displayName()
            );
        }
        return WorldActionContext.none();
    }

    private static boolean isHostile(EntitySyncState entity) {
        return entity.entityType() == EntityType.ENEMY || entity.entityType() == EntityType.NPC;
    }

    private static float distanceSquared(EntitySyncState a, EntitySyncState b) {
        float dx = a.displayPosition().x() - b.displayPosition().x();
        float dy = a.displayPosition().y() - b.displayPosition().y();
        return (dx * dx) + (dy * dy);
    }
}
