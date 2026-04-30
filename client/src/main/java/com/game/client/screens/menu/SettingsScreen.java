package com.game.client.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.ui.theme.UiPalette;
import com.game.client.ui.render.UiRenderState;
import com.game.client.ui.render.UiRenderer;
import com.game.client.screens.Screen;
import com.game.client.screens.ScreenController;

/**
 * Placeholder settings screen for the Phase 4 client skeleton.
 *
 * @since 0.1.0
 */
public final class SettingsScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenController screenController;
    private final UiRenderer uiRenderer = new UiRenderer();

    /**
     * Creates the settings screen.
     *
     * @param gameClient the owning game client
     * @param screenController the screen controller
     */
    public SettingsScreen(GameClient gameClient, ScreenController screenController) {
        this.gameClient = gameClient;
        this.screenController = screenController;
    }

    /**
     * Renders the placeholder settings view.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenController.showLogin();
            return;
        }

        Gdx.gl.glClearColor(0.03f, 0.06f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        uiRenderer.renderBackdrop(gameClient, delta + (System.currentTimeMillis() / 1000f));
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        UiRenderState.beginText(gameClient);
        uiRenderer.renderHero(gameClient, "Client", "Settings", "Configuration surface reserved for future polish and accessibility options.");
        uiRenderer.renderStatus(gameClient, "Current build keeps settings read-only for now.", 96f, 438f, UiPalette.TEXT_WARNING);
        uiRenderer.renderInfo(gameClient, "Planned next: audio levels, keybind remapping, UI scale, and fullscreen.", 96f, 382f);
        uiRenderer.renderInfo(gameClient, "Press B or ESC to return.", 96f, 326f);
        UiRenderState.endText(gameClient);
    }
}
