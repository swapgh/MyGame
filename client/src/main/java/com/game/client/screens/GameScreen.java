package com.game.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.app.GameClient;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;

import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Early in-world screen for the Phase 5 movement milestone.
 *
 * @since 0.1.0
 */
public final class GameScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenManager screenManager;
    private final String characterName;
    private final SharedEntityId playerEntityId;
    private final Map<Long, RenderState> renderStates = new HashMap<>();

    private Vec2 lastInputDirection = Vec2.ZERO;
    private Vec2 predictedPlayerPosition;
    private long lastProcessedSnapshotTick = -1L;

    /**
     * Creates the minimal in-world screen.
     *
     * @param gameClient the owning game client
     * @param screenManager the screen manager
     * @param characterName the active character name
     * @param playerEntityId the authoritative local player entity id
     */
    public GameScreen(
            GameClient gameClient,
            ScreenManager screenManager,
            String characterName,
            SharedEntityId playerEntityId
    ) {
        this.gameClient = gameClient;
        this.screenManager = screenManager;
        this.characterName = characterName;
        this.playerEntityId = playerEntityId;
    }

    /**
     * Renders the early authoritative movement view.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameClient.worldClient().close();
            screenManager.showLogin();
            return;
        }
        if (!gameClient.worldClient().isConnected()) {
            screenManager.showError("World connection closed.");
            return;
        }

        Vec2 inputDirection = readInputDirection();
        try {
            gameClient.worldClient().sendMoveDirection(playerEntityId, inputDirection);
            lastInputDirection = inputDirection;
        } catch (IOException exception) {
            screenManager.showError("World send failed: " + exception.getMessage());
            return;
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();

        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        shapeRenderer.setProjectionMatrix(gameClient.uiCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderEntities(shapeRenderer, delta);
        shapeRenderer.end();

        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        gameClient.font().draw(gameClient.spriteBatch(), "Game Screen", 80f, 640f);
        gameClient.font().draw(gameClient.spriteBatch(), "Connected as " + characterName, 80f, 600f);
        gameClient.font().draw(gameClient.spriteBatch(), "Use WASD or arrow keys to move.", 80f, 560f);
        gameClient.font().draw(gameClient.spriteBatch(), "White square is you. Green squares are other entities.", 80f, 520f);
        gameClient.font().draw(gameClient.spriteBatch(), "Press ESC to disconnect back to login.", 80f, 480f);
        gameClient.spriteBatch().end();
    }

    private void renderEntities(ShapeRenderer shapeRenderer, float delta) {
        WorldSnapshotPacket snapshot = gameClient.worldClient().latestSnapshot();
        if (snapshot == null) {
            return;
        }

        if (snapshot.serverTick() != lastProcessedSnapshotTick) {
            applySnapshot(snapshot);
            lastProcessedSnapshotTick = snapshot.serverTick();
        }

        for (RenderState renderState : renderStates.values()) {
            renderState.advance(delta);

            if (renderState.entityId == playerEntityId.value()) {
                shapeRenderer.setColor(Color.WHITE);
            } else {
                shapeRenderer.setColor(Color.FOREST);
            }
            shapeRenderer.rect(
                    renderState.displayPosition.x() - 20.0f,
                    renderState.displayPosition.y() - 20.0f,
                    40.0f,
                    40.0f
            );
        }
    }

    private void applySnapshot(WorldSnapshotPacket snapshot) {
        Set<Long> visibleEntityIds = new HashSet<>();
        for (EntitySpawnPacket entity : snapshot.entities()) {
            long entityId = entity.entityId().value();
            visibleEntityIds.add(entityId);

            RenderState state = renderStates.get(entityId);
            if (entityId == playerEntityId.value()) {
                reconcileLocalPlayer(entity, state);
            } else if (state == null) {
                renderStates.put(entityId, new RenderState(entityId, entity.position(), entity.position(), entity.velocity()));
            } else {
                state.targetPosition = entity.position();
                state.velocity = entity.velocity();
            }
        }

        renderStates.keySet().removeIf(entityId -> !visibleEntityIds.contains(entityId));
    }

    private void reconcileLocalPlayer(EntitySpawnPacket entity, RenderState state) {
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

        if (lastInputDirection.lengthSquared() > 0.0f) {
            predictedPlayerPosition = predictedPlayerPosition.add(lastInputDirection.scale(12.0f));
        }

        if (state == null) {
            renderStates.put(entity.entityId().value(), new RenderState(
                    entity.entityId().value(),
                    predictedPlayerPosition,
                    authoritativePosition,
                    entity.velocity()
            ));
            return;
        }

        state.targetPosition = authoritativePosition;
        state.velocity = entity.velocity();
        state.displayPosition = state.displayPosition.lerp(predictedPlayerPosition, 0.4f);
    }

    private static Vec2 readInputDirection() {
        float x = 0.0f;
        float y = 0.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= 1.0f;
        }
        Vec2 direction = new Vec2(x, y);
        return direction.lengthSquared() > 1.0f ? direction.normalized() : direction;
    }

    private static final class RenderState {
        private final long entityId;
        private Vec2 displayPosition;
        private Vec2 targetPosition;
        private Vec2 velocity;

        private RenderState(long entityId, Vec2 displayPosition, Vec2 targetPosition, Vec2 velocity) {
            this.entityId = entityId;
            this.displayPosition = displayPosition;
            this.targetPosition = targetPosition;
            this.velocity = velocity;
        }

        private void advance(float delta) {
            Vec2 extrapolatedTarget = targetPosition.add(velocity.scale(delta));
            displayPosition = displayPosition.lerp(extrapolatedTarget, 0.22f);
        }
    }
}
