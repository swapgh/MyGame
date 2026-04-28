package com.game.client.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.app.GameClient;
import com.game.client.sync.EntitySyncState;
import com.game.client.sync.PositionInterpolator;
import com.game.client.sync.SnapshotApplier;
import com.game.client.sync.WorldSyncState;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.protocol.world.EntityType;
import com.game.shared.protocol.world.WorldSnapshotPacket;

/**
 * Draws the live world scene from authoritative snapshot data.
 *
 * @since 0.1.0
 */
public final class WorldSceneRenderer {
    private final SnapshotApplier snapshotApplier = new SnapshotApplier();
    private final PositionInterpolator positionInterpolator = new PositionInterpolator();

    /**
     * Renders the current world scene.
     *
     * @param gameClient the owning client
     * @param snapshot the latest authoritative snapshot, if present
     * @param worldSyncState the client sync state
     * @param playerEntityId the local player entity id
     * @param delta frame delta in seconds
     */
    public void render(
            GameClient gameClient,
            WorldSnapshotPacket snapshot,
            WorldSyncState worldSyncState,
            SharedEntityId playerEntityId,
            float delta
    ) {
        if (snapshot == null) {
            return;
        }

        snapshotApplier.apply(snapshot, worldSyncState);
        positionInterpolator.advance(worldSyncState, delta);

        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        shapeRenderer.setProjectionMatrix(gameClient.uiCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (EntitySyncState entityState : worldSyncState.entityStates()) {
            if (entityState.entityId() == playerEntityId.value()) {
                shapeRenderer.setColor(ClientUiPalette.PLAYER);
            } else if (entityState.entityType() == EntityType.LOOT) {
                shapeRenderer.setColor(ClientUiPalette.LOOT);
            } else if (entityState.entityType() == EntityType.NPC) {
                shapeRenderer.setColor(entityState.alive() ? ClientUiPalette.LIVING_ENEMY : ClientUiPalette.DEAD_ENTITY);
            } else if (!entityState.alive()) {
                shapeRenderer.setColor(ClientUiPalette.DEAD_ENTITY);
            } else {
                shapeRenderer.setColor(ClientUiPalette.TEXT_SUCCESS);
            }
            shapeRenderer.rect(
                    entityState.displayPosition().x() - 20.0f,
                    entityState.displayPosition().y() - 20.0f,
                    40.0f,
                    40.0f
            );
        }
        shapeRenderer.end();
    }
}
