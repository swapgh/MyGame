package com.game.client.screens.world;

import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.controllers.world.WorldScreenController;
import com.game.client.input.InputManager;
import com.game.client.input.WorldInputFrame;
import com.game.client.model.WorldFrameState;
import com.game.client.model.WorldSession;
import com.game.client.render.overlay.WorldHudRenderer;
import com.game.client.render.world.SceneRenderer;
import com.game.client.screens.Screen;
import com.game.client.screens.ScreenController;
import com.game.client.ui.render.UiRenderer;
import com.game.client.world.sync.WorldSyncState;

/**
 * Screen shell for the active world session.
 *
 * @since 0.1.0
 */
public final class GameScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenController screenController;
    private final WorldSession worldSession;
    private final UiRenderer uiRenderer = new UiRenderer();
    private final InputManager inputManager = new InputManager();
    private final WorldHudRenderer hudRenderer = new WorldHudRenderer();
    private final SceneRenderer sceneRenderer = new SceneRenderer();
    private final WorldScreenController worldScreenController;
    private final WorldSyncState worldSyncState;

    /**
     * Creates the minimal in-world screen.
     *
     * @param gameClient the owning game client
     * @param screenController the screen controller
     */
    public GameScreen(GameClient gameClient, ScreenController screenController) {
        this.gameClient = gameClient;
        this.screenController = screenController;
        this.worldSession = gameClient.worldService().requireWorldSession();
        this.worldScreenController = new WorldScreenController(
                gameClient.worldService(),
                gameClient.targetingService(),
                gameClient.worldActionService(),
                gameClient.worldFeedbackService()
        );
        this.worldSyncState = new WorldSyncState(worldSession.playerEntityId());
        gameClient.worldFeedbackService().reset();
    }

    /**
     * Renders the authoritative world view.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        WorldInputFrame inputFrame = inputManager.readWorldInput();
        long nowMillis = System.currentTimeMillis();
        WorldFrameState frameState = worldScreenController.prepareFrame(
                worldSession.playerEntityId(),
                worldSyncState,
                inputFrame,
                gameClient.worldClient().latestInteractionMessage(),
                gameClient.worldClient().latestInteractionMessageVersion(),
                gameClient.worldClient().latestInventoryUpdate(),
                nowMillis
        );

        if (inputFrame.disconnectRequested()) {
            gameClient.worldService().leaveWorld();
            screenController.showLogin();
            return;
        }
        if (!gameClient.worldClient().isConnected()) {
            screenController.showError("World connection closed.");
            return;
        }

        try {
            worldScreenController.handleInput(
                    worldSession.playerEntityId(),
                    inputFrame,
                    worldSyncState,
                    frameState
            );
        } catch (java.io.IOException exception) {
            screenController.showError("World send failed: " + exception.getMessage());
            return;
        }

        gameClient.uiCamera().update();

        com.badlogic.gdx.Gdx.gl.glClearColor(0.03f, 0.06f, 0.1f, 1f);
        com.badlogic.gdx.Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        uiRenderer.renderBackdrop(gameClient, delta + (System.currentTimeMillis() / 1000f));

        sceneRenderer.render(
                gameClient,
                gameClient.worldClient().latestSnapshot(),
                worldSyncState,
                worldSession.playerEntityId(),
                delta
        );

        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        hudRenderer.render(
                gameClient,
                worldSession.characterName(),
                worldSyncState.entityStates(),
                worldSession.playerEntityId().value(),
                frameState
        );
    }
}
