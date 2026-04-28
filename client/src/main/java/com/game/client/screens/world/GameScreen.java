package com.game.client.screens.world;

import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.controllers.world.WorldScreenController;
import com.game.client.input.InputManager;
import com.game.client.input.WorldInputFrame;
import com.game.client.render.ClientUiRenderer;
import com.game.client.render.world.HudRenderer;
import com.game.client.render.world.SceneRenderer;
import com.game.client.screens.Screen;
import com.game.client.ui.ScreenController;
import com.game.client.world.sync.WorldSyncState;
import com.game.shared.ids.SharedEntityId;

/**
 * Screen shell for the active world session.
 *
 * @since 0.1.0
 */
public final class GameScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenController screenController;
    private final String characterName;
    private final SharedEntityId playerEntityId;
    private final ClientUiRenderer uiRenderer = new ClientUiRenderer();
    private final InputManager inputManager = new InputManager();
    private final HudRenderer hudRenderer = new HudRenderer();
    private final SceneRenderer sceneRenderer = new SceneRenderer();
    private final WorldScreenController worldScreenController;
    private final WorldSyncState worldSyncState;

    /**
     * Creates the minimal in-world screen.
     *
     * @param gameClient the owning game client
     * @param screenController the screen controller
     * @param characterName the active character name
     * @param playerEntityId the authoritative local player entity id
     */
    public GameScreen(
            GameClient gameClient,
            ScreenController screenController,
            String characterName,
            SharedEntityId playerEntityId
    ) {
        this.gameClient = gameClient;
        this.screenController = screenController;
        this.characterName = characterName;
        this.playerEntityId = playerEntityId;
        this.worldScreenController = new WorldScreenController(gameClient.worldClient());
        this.worldSyncState = new WorldSyncState(playerEntityId);
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
            screenController.showLogin();
            return;
        }
        if (!gameClient.worldClient().isConnected()) {
            screenController.showError("World connection closed.");
            return;
        }

        try {
            worldScreenController.handleInput(playerEntityId, inputFrame, worldSyncState);
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
                playerEntityId,
                delta
        );

        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        hudRenderer.renderHeader(gameClient, characterName);
        hudRenderer.renderInventory(gameClient, gameClient.worldClient().latestInventoryUpdate());
        hudRenderer.renderEntityLabels(gameClient, worldSyncState.entityStates(), playerEntityId.value());
        gameClient.spriteBatch().end();
    }
}
