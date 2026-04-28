package com.game.client.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.app.GameClient;
import com.game.client.components.WorldEntityRenderState;
import com.game.client.input.InputManager;
import com.game.client.input.WorldInputFrame;
import com.game.client.systems.InterpolationSystem;
import com.game.client.systems.SnapshotApplySystem;
import com.game.client.ui.ScreenManager;
import com.game.client.ui.WorldHudRenderer;
import com.game.client.world.WorldViewModel;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.protocol.world.EntityType;

import java.io.IOException;

/**
 * Early in-world screen for the current client world slice.
 *
 * @since 0.1.0
 */
public final class GameScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenManager screenManager;
    private final String characterName;
    private final SharedEntityId playerEntityId;
    private final InputManager inputManager = new InputManager();
    private final WorldHudRenderer hudRenderer = new WorldHudRenderer();
    private final SnapshotApplySystem snapshotApplySystem = new SnapshotApplySystem();
    private final InterpolationSystem interpolationSystem = new InterpolationSystem();
    private final WorldViewModel viewModel;

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
        this.viewModel = new WorldViewModel(playerEntityId);
    }

    /**
     * Renders the authoritative world view.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        WorldInputFrame inputFrame = inputManager.readWorldInput();
        if (inputFrame.disconnectRequested()) {
            gameClient.worldClient().close();
            screenManager.showLogin();
            return;
        }
        if (!gameClient.worldClient().isConnected()) {
            screenManager.showError("World connection closed.");
            return;
        }

        try {
            gameClient.worldClient().sendMoveDirection(playerEntityId, inputFrame.movementDirection());
            viewModel.setLastInputDirection(inputFrame.movementDirection());
            if (inputFrame.attackRequested()) {
                gameClient.worldClient().sendAttack(playerEntityId);
            }
        } catch (IOException exception) {
            screenManager.showError("World send failed: " + exception.getMessage());
            return;
        }

        gameClient.uiCamera().update();

        com.badlogic.gdx.Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        com.badlogic.gdx.Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        shapeRenderer.setProjectionMatrix(gameClient.uiCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderEntities(shapeRenderer, delta);
        shapeRenderer.end();

        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        hudRenderer.renderHeader(gameClient, characterName);
        hudRenderer.renderEntityLabels(gameClient, viewModel.renderStates(), playerEntityId.value());
        gameClient.spriteBatch().end();
    }

    private void renderEntities(ShapeRenderer shapeRenderer, float delta) {
        if (gameClient.worldClient().latestSnapshot() == null) {
            return;
        }

        snapshotApplySystem.apply(gameClient.worldClient().latestSnapshot(), viewModel);
        interpolationSystem.advance(viewModel, delta);

        for (WorldEntityRenderState renderState : viewModel.renderStates()) {
            if (renderState.entityId() == playerEntityId.value()) {
                shapeRenderer.setColor(Color.WHITE);
            } else if (renderState.entityType() == EntityType.LOOT) {
                shapeRenderer.setColor(Color.GOLD);
            } else if (renderState.entityType() == EntityType.NPC) {
                shapeRenderer.setColor(renderState.alive() ? Color.SCARLET : Color.GRAY);
            } else if (!renderState.alive()) {
                shapeRenderer.setColor(Color.GRAY);
            } else {
                shapeRenderer.setColor(Color.FOREST);
            }
            shapeRenderer.rect(
                    renderState.displayPosition().x() - 20.0f,
                    renderState.displayPosition().y() - 20.0f,
                    40.0f,
                    40.0f
            );
        }
    }
}
