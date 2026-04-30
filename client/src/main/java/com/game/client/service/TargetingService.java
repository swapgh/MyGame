package com.game.client.service;

import com.game.client.model.TargetSelection;
import com.game.client.world.sync.EntitySyncState;
import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.world.EntityType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Owns client-side target selection for the active world session.
 *
 * @since 0.1.0
 */
public final class TargetingService {
    private volatile TargetSelection currentTarget;

    /**
     * Returns the currently selected target, if any.
     *
     * @return the selected target, or {@code null}
     */
    public TargetSelection currentTarget() {
        return currentTarget;
    }

    /**
     * Clears the current target when it is no longer valid.
     *
     * @param entities current visible entities
     */
    public void clearInvalidTarget(Iterable<EntitySyncState> entities) {
        if (currentTarget == null) {
            return;
        }
        for (EntitySyncState entity : entities) {
            if (entity.entityId() == currentTarget.entityId() && isHostile(entity) && entity.alive()) {
                return;
            }
        }
        currentTarget = null;
    }

    /**
     * Cycles to the next visible hostile target.
     *
     * @param playerEntityId local player entity id
     * @param entities current visible entities
     */
    public void cycleHostileTarget(SharedEntityId playerEntityId, Iterable<EntitySyncState> entities) {
        List<EntitySyncState> hostiles = collectHostiles(playerEntityId, entities);
        if (hostiles.isEmpty()) {
            currentTarget = null;
            return;
        }

        hostiles.sort(Comparator.comparingLong(EntitySyncState::entityId));
        if (currentTarget == null) {
            EntitySyncState next = hostiles.getFirst();
            currentTarget = new TargetSelection(next.entityId(), next.displayName());
            return;
        }

        for (int index = 0; index < hostiles.size(); index++) {
            if (hostiles.get(index).entityId() == currentTarget.entityId()) {
                EntitySyncState next = hostiles.get((index + 1) % hostiles.size());
                currentTarget = new TargetSelection(next.entityId(), next.displayName());
                return;
            }
        }

        EntitySyncState next = hostiles.getFirst();
        currentTarget = new TargetSelection(next.entityId(), next.displayName());
    }

    /**
     * Auto-selects the nearest visible hostile when no target is selected.
     *
     * @param playerEntityId local player entity id
     * @param entities current visible entities
     */
    public void autoSelectNearestHostile(SharedEntityId playerEntityId, Iterable<EntitySyncState> entities) {
        if (currentTarget != null) {
            return;
        }

        EntitySyncState player = null;
        List<EntitySyncState> hostiles = new ArrayList<>();
        for (EntitySyncState entity : entities) {
            if (entity.entityId() == playerEntityId.value()) {
                player = entity;
            } else if (isHostile(entity) && entity.alive()) {
                hostiles.add(entity);
            }
        }

        if (player == null || hostiles.isEmpty()) {
            return;
        }

        EntitySyncState nearest = null;
        float nearestDistanceSquared = Float.MAX_VALUE;
        for (EntitySyncState hostile : hostiles) {
            float dx = hostile.displayPosition().x() - player.displayPosition().x();
            float dy = hostile.displayPosition().y() - player.displayPosition().y();
            float distanceSquared = (dx * dx) + (dy * dy);
            if (distanceSquared < nearestDistanceSquared) {
                nearestDistanceSquared = distanceSquared;
                nearest = hostile;
            }
        }

        if (nearest != null) {
            currentTarget = new TargetSelection(nearest.entityId(), nearest.displayName());
        }
    }

    private static List<EntitySyncState> collectHostiles(
            SharedEntityId playerEntityId,
            Iterable<EntitySyncState> entities
    ) {
        List<EntitySyncState> hostiles = new ArrayList<>();
        for (EntitySyncState entity : entities) {
            if (entity.entityId() == playerEntityId.value()) {
                continue;
            }
            if (isHostile(entity) && entity.alive()) {
                hostiles.add(entity);
            }
        }
        return hostiles;
    }

    private static boolean isHostile(EntitySyncState entity) {
        return entity.entityType() == EntityType.ENEMY || entity.entityType() == EntityType.NPC;
    }
}
