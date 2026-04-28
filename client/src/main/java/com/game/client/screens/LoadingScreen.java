package com.game.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.network.WorldClient;

import java.io.IOException;

/**
 * Temporary loading screen used while entering the world server.
 *
 * @since 0.1.0
 */
public final class LoadingScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenManager screenManager;
    private final String characterName;

    private volatile boolean started;
    private volatile String status = "Connecting to world server...";

    /**
     * Creates the loading screen.
     *
     * @param gameClient the owning game client
     * @param screenManager the screen manager
     * @param characterName the selected character name
     */
    public LoadingScreen(GameClient gameClient, ScreenManager screenManager, String characterName) {
        this.gameClient = gameClient;
        this.screenManager = screenManager;
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

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        gameClient.font().draw(gameClient.spriteBatch(), "Loading Screen", 80f, 640f);
        gameClient.font().draw(gameClient.spriteBatch(), "Character: " + characterName, 80f, 590f);
        gameClient.font().draw(gameClient.spriteBatch(), status, 80f, 540f);
        gameClient.spriteBatch().end();
    }

    private void startWorldEntry() {
        Thread worldThread = new Thread(() -> {
            try {
                WorldClient.WorldEntryResult result = gameClient.worldClient().enterWorld(characterName);
                Gdx.app.postRunnable(() -> {
                    if (result.success()) {
                        screenManager.showGame(characterName, result.snapshot().playerEntityId());
                    } else {
                        screenManager.showError("World entry failed: " + result.message());
                    }
                });
            } catch (IOException | IllegalArgumentException exception) {
                Gdx.app.postRunnable(() ->
                        screenManager.showError("World connection failed: " + exception.getMessage()));
            }
        }, "world-entry");
        worldThread.setDaemon(true);
        worldThread.start();
    }
}
