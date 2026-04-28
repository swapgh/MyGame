package com.game.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.app.GameClient;

/**
 * Minimal in-world screen for the Phase 4 client milestone.
 *
 * @since 0.1.0
 */
public final class GameScreen implements Screen {
    private final GameClient gameClient;
    private final ScreenManager screenManager;
    private final String characterName;

    /**
     * Creates the minimal in-world screen.
     *
     * @param gameClient the owning game client
     * @param screenManager the screen manager
     * @param characterName the active character name
     */
    public GameScreen(GameClient gameClient, ScreenManager screenManager, String characterName) {
        this.gameClient = gameClient;
        this.screenManager = screenManager;
        this.characterName = characterName;
    }

    /**
     * Renders the placeholder in-world view.
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

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();

        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        shapeRenderer.setProjectionMatrix(gameClient.uiCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(620f, 340f, 40f, 40f);
        shapeRenderer.end();

        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        gameClient.font().draw(gameClient.spriteBatch(), "Game Screen", 80f, 640f);
        gameClient.font().draw(gameClient.spriteBatch(), "Connected as " + characterName, 80f, 600f);
        gameClient.font().draw(gameClient.spriteBatch(), "This white square is a temporary placeholder. Movement starts in Phase 5.", 80f, 560f);
        gameClient.font().draw(gameClient.spriteBatch(), "Press ESC to disconnect back to login.", 80f, 520f);
        gameClient.spriteBatch().end();
    }
}
