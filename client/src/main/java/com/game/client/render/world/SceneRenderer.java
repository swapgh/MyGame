package com.game.client.render.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.app.GameClient;
import com.game.client.ui.theme.UiPalette;
import com.game.client.world.sync.EntitySyncState;
import com.game.client.world.sync.PositionInterpolator;
import com.game.client.world.sync.SnapshotApplier;
import com.game.client.world.sync.WorldSyncState;
import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.world.EntityType;
import com.game.shared.protocol.world.WorldSnapshotPacket;

/**
 * Draws the live world scene from authoritative snapshot data.
 *
 * @since 0.1.0
 */
public final class SceneRenderer {
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
                shapeRenderer.setColor(UiPalette.PLAYER);
            } else if (entityState.entityType() == EntityType.LOOT) {
                shapeRenderer.setColor(UiPalette.LOOT);
            } else if (entityState.entityType() == EntityType.ENEMY || entityState.entityType() == EntityType.NPC) {
                shapeRenderer.setColor(entityState.alive() ? UiPalette.LIVING_ENEMY : UiPalette.DEAD_ENTITY);
            } else if (entityState.entityType() == EntityType.VENDOR) {
                shapeRenderer.setColor(UiPalette.TEXT_SUCCESS);
            } else if (!entityState.alive()) {
                shapeRenderer.setColor(UiPalette.DEAD_ENTITY);
            } else {
                shapeRenderer.setColor(UiPalette.TEXT_SUCCESS);
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
