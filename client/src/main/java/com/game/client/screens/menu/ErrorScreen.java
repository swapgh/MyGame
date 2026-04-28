package com.game.client.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.render.ClientUiPalette;
import com.game.client.render.ClientUiRenderer;
import com.game.client.screens.Screen;
import com.game.client.ui.ScreenController;

/**
 * Simple error screen used by the early client flow.
 *
 * @since 0.1.0
 */
public final class ErrorScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenController screenController;
    private final String message;
    private final ClientUiRenderer uiRenderer = new ClientUiRenderer();

    /**
     * Creates an error screen with a message.
     *
     * @param gameClient the owning game client
     * @param screenController the screen controller
     * @param message the displayed error message
     */
    public ErrorScreen(GameClient gameClient, ScreenController screenController, String message) {
        this.gameClient = gameClient;
        this.screenController = screenController;
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
            screenController.showLogin();
            return;
        }

        Gdx.gl.glClearColor(0.08f, 0.03f, 0.03f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        uiRenderer.renderBackdrop(gameClient, delta + (System.currentTimeMillis() / 1000f));
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        uiRenderer.renderHero(gameClient, "Alert", "Connection Error", "Something interrupted the current client flow.");
        uiRenderer.renderStatus(gameClient, message, 96f, 438f, ClientUiPalette.TEXT_DANGER);
        uiRenderer.renderInfo(gameClient, "Press B or ESC to return to login.", 96f, 372f);
        gameClient.spriteBatch().end();
    }
}
