package com.game.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.ui.ScreenManager;

/**
 * Simple error screen used by the early client flow.
 *
 * @since 0.1.0
 */
public final class ErrorScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenManager screenManager;
    private final String message;

    /**
     * Creates an error screen with a message.
     *
     * @param gameClient the owning game client
     * @param screenManager the screen manager
     * @param message the displayed error message
     */
    public ErrorScreen(GameClient gameClient, ScreenManager screenManager, String message) {
        this.gameClient = gameClient;
        this.screenManager = screenManager;
        this.message = message;
    }

    /**
     * Renders the error message and a return shortcut.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.showLogin();
            return;
        }

        Gdx.gl.glClearColor(0.08f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        gameClient.font().draw(gameClient.spriteBatch(), "Error Screen", 80f, 640f);
        gameClient.font().draw(gameClient.spriteBatch(), message, 80f, 590f);
        gameClient.font().draw(gameClient.spriteBatch(), "Press B or ESC to go back to login.", 80f, 540f);
        gameClient.spriteBatch().end();
    }
}
