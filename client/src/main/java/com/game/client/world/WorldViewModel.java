package com.game.client.world;

import com.game.client.components.WorldEntityRenderState;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holds client-side world rendering state derived from authoritative snapshots.
 *
 * @since 0.1.0
 */
public final class WorldViewModel {
    private final SharedEntityId playerEntityId;
    private final Map<Long, WorldEntityRenderState> renderStates = new HashMap<>();

    private Vec2 lastInputDirection = Vec2.ZERO;
    private Vec2 predictedPlayerPosition;
    private long lastProcessedSnapshotTick = -1L;

    /**
     * Creates a view model for the local player.
     *
     * @param playerEntityId the local player entity id
     */
    public WorldViewModel(SharedEntityId playerEntityId) {
        this.playerEntityId = playerEntityId;
    }

    /**
     * Stores the latest local movement direction for client prediction.
     *
     * @param direction the most recently sent movement direction
     */
    public void setLastInputDirection(Vec2 direction) {
        lastInputDirection = direction;
    }

    /**
     * Applies a new snapshot if it has not already been processed.
     *
     * @param snapshot the snapshot to apply
     * @return {@code true} when the snapshot changed the model
     */
    public boolean applySnapshot(WorldSnapshotPacket snapshot) {
        if (snapshot == null || snapshot.serverTick() == lastProcessedSnapshotTick) {
            return false;
        }

        Set<Long> visibleEntityIds = new HashSet<>();
        for (EntitySpawnPacket entity : snapshot.entities()) {
            long entityId = entity.entityId().value();
            visibleEntityIds.add(entityId);

            WorldEntityRenderState state = renderStates.get(entityId);
            if (entityId == playerEntityId.value()) {
                reconcileLocalPlayer(entity, state);
            } else if (state == null) {
                renderStates.put(entityId, new WorldEntityRenderState(
                        entityId,
                        entity.position(),
                        entity.position(),
                        entity.velocity(),
                        entity.currentHealth(),
                        entity.maxHealth(),
                        entity.alive(),
                        entity.respawnTicksRemaining()
                ));
            } else {
                state.syncFromSnapshot(
                        entity.position(),
                        entity.velocity(),
                        entity.currentHealth(),
                        entity.maxHealth(),
                        entity.alive(),
                        entity.respawnTicksRemaining()
                );
            }
        }

        renderStates.keySet().removeIf(entityId -> !visibleEntityIds.contains(entityId));
        lastProcessedSnapshotTick = snapshot.serverTick();
        return true;
    }

    /**
     * Advances all entity render states for the current frame.
     *
     * @param delta frame delta in seconds
     */
    public void advance(float delta) {
        for (WorldEntityRenderState renderState : renderStates.values()) {
            renderState.advance(delta);
        }
    }

    /**
     * Returns the currently visible render states.
     *
     * @return visible entity render states
     */
    public Collection<WorldEntityRenderState> renderStates() {
        return renderStates.values();
    }

    private void reconcileLocalPlayer(EntitySpawnPacket entity, WorldEntityRenderState state) {
        Vec2 authoritativePosition = entity.position();
        if (predictedPlayerPosition == null) {
            predictedPlayerPosition = authoritativePosition;
        }

        float error = predictedPlayerPosition.subtract(authoritativePosition).length();
        if (error > 60.0f) {
            predictedPlayerPosition = authoritativePosition;
        } else {
            predictedPlayerPosition = predictedPlayerPosition.lerp(authoritativePosition, 0.35f);
        }

        if (entity.alive() && lastInputDirection.lengthSquared() > 0.0f) {
            predictedPlayerPosition = predictedPlayerPosition.add(lastInputDirection.scale(12.0f));
        } else if (!entity.alive()) {
            predictedPlayerPosition = authoritativePosition;
        }

        if (state == null) {
            renderStates.put(entity.entityId().value(), new WorldEntityRenderState(
                    entity.entityId().value(),
                    predictedPlayerPosition,
                    authoritativePosition,
                    entity.velocity(),
                    entity.currentHealth(),
                    entity.maxHealth(),
                    entity.alive(),
                    entity.respawnTicksRemaining()
            ));
            return;
        }

        state.syncFromSnapshot(
                authoritativePosition,
                entity.velocity(),
                entity.currentHealth(),
                entity.maxHealth(),
                entity.alive(),
                entity.respawnTicksRemaining()
        );
        state.reconcileDisplayPosition(predictedPlayerPosition);
    }
}
