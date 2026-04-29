package com.game.client.screens.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.controllers.world.WorldEntryController;
import com.game.client.screens.Screen;
import com.game.client.render.ClientUiPalette;
import com.game.client.render.ClientUiRenderer;
import com.game.client.ui.ScreenController;

/**
 * Temporary loading screen used while entering the world server.
 *
 * @since 0.1.0
 */
public final class LoadingScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenController screenController;
    private final WorldEntryController worldEntryController;
    private final String characterName;
    private final ClientUiRenderer uiRenderer = new ClientUiRenderer();

    private volatile boolean started;
    private volatile String status = "Connecting to world server...";

    /**
     * Creates the loading screen.
     *
     * @param gameClient the owning game client
     * @param screenController the screen controller
     * @param characterName the selected character name
     */
    public LoadingScreen(GameClient gameClient, ScreenController screenController, String characterName) {
        this.gameClient = gameClient;
        this.screenController = screenController;
        this.worldEntryController = new WorldEntryController(gameClient.worldClient());
        this.characterName = characterName;
    }

    /**
     * Starts the asynchronous world entry and draws the loading status.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        if (!started) {
            started = true;
            startWorldEntry();
        }

        Gdx.gl.glClearColor(0.03f, 0.06f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        uiRenderer.renderBackdrop(gameClient, delta + (System.currentTimeMillis() / 1000f));
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        uiRenderer.renderHero(gameClient, "Transit", "Entering World", "Establishing the world connection and preparing the first snapshot.");
        uiRenderer.renderInfo(gameClient, "Character: " + characterName, 96f, 456f);
        uiRenderer.renderStatus(gameClient, status, 96f, 396f, ClientUiPalette.TEXT_WARNING);
        uiRenderer.renderInfo(gameClient, "Please wait while the server syncs your session.", 96f, 340f);
        gameClient.spriteBatch().end();
    }

    private void startWorldEntry() {
        worldEntryController.enterWorld(
                characterName,
                result -> {
                    if (result.success()) {
                        screenController.showGame(characterName, result.snapshot().playerEntityId());
                    } else {
                        screenController.showError("World entry failed: " + result.message());
                    }
                },
                message -> screenController.showError("World connection failed: " + message)
        );
    }
}
