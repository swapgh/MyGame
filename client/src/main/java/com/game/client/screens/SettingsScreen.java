package com.game.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;

/**
 * Placeholder settings screen for the Phase 4 client skeleton.
 *
 * @since 0.1.0
 */
public final class SettingsScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenManager screenManager;

    /**
     * Creates the settings screen.
     *
     * @param gameClient the owning game client
     * @param screenManager the screen manager
     */
    public SettingsScreen(GameClient gameClient, ScreenManager screenManager) {
        this.gameClient = gameClient;
        this.screenManager = screenManager;
    }

    /**
     * Renders the placeholder settings view.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.showLogin();
            return;
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        gameClient.font().draw(gameClient.spriteBatch(), "Settings Screen", 80f, 640f);
        gameClient.font().draw(gameClient.spriteBatch(), "Placeholder only for Phase 4.", 80f, 590f);
        gameClient.font().draw(gameClient.spriteBatch(), "Press B or ESC to return.", 80f, 550f);
        gameClient.spriteBatch().end();
    }
}
