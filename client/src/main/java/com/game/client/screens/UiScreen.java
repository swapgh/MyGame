package com.game.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.ui.render.UiRenderer;
import com.game.client.ui.render.UiRenderState;
import com.game.client.ui.components.UiWidget;
import com.game.client.ui.core.UiDocument;
import com.game.client.ui.core.UiDocumentRenderer;

/**
 * Shared base screen for composed client UIs.
 *
 * @since 0.1.0
 */
public abstract class UiScreen extends InputAdapter implements Screen {
    private final GameClient gameClient;
    private final UiRenderer uiRenderer = new UiRenderer();
    private final UiDocumentRenderer documentRenderer = new UiDocumentRenderer();

    protected UiScreen(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public final void render(float delta) {
        update(delta);

        Color clear = clearColor();
        Gdx.gl.glClearColor(clear.r, clear.g, clear.b, clear.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        uiRenderer.renderBackdrop(gameClient, delta + (System.currentTimeMillis() / 1000f));

        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        UiRenderState.beginText(gameClient);
        documentRenderer.render(gameClient, uiRenderer, buildDocument(delta));
        UiRenderState.endText(gameClient);

        afterRender(delta);
        UiRenderState.endText(gameClient);
    }

    /**
     * Returns the owning game client.
     *
     * @return the client
     */
    protected final GameClient gameClient() {
        return gameClient;
    }

    /**
     * Returns the shared UI renderer.
     *
     * @return the renderer
     */
    protected final UiRenderer uiRenderer() {
        return uiRenderer;
    }

    /**
     * Performs per-frame logic before the document is rendered.
     *
     * @param delta frame delta
     */
    protected void update(float delta) {
    }

    /**
     * Performs per-frame logic after the document is rendered.
     *
     * @param delta frame delta
     */
    protected void afterRender(float delta) {
    }

    /**
     * Returns the clear color for the screen.
     *
     * @return the color used when clearing the frame
     */
    protected Color clearColor() {
        return new Color(0.03f, 0.06f, 0.1f, 1f);
    }

    /**
     * Builds the current screen document.
     *
     * @param delta frame delta
     * @return the document to render
     */
    protected abstract UiDocument buildDocument(float delta);

    /**
     * Routes mouse and enter key input to a widget tree.
     *
     * @param root widget root
     * @param clearFocus action when a click misses the root
     */
    protected final void updateWidgetInput(UiWidget root, Runnable clearFocus) {
        float worldMouseX = screenXToWorld(Gdx.input.getX());
        float worldMouseY = screenYToWorld(Gdx.input.getY());

        root.handleMouseMove(worldMouseX, worldMouseY);

        boolean leftJustPressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        if (leftJustPressed) {
            boolean handled = root.handleMousePressed(worldMouseX, worldMouseY, Input.Buttons.LEFT);
            if (!handled) {
                clearFocus.run();
            }
            root.handleMouseReleased(worldMouseX, worldMouseY, Input.Buttons.LEFT);
        }
    }

    private float screenXToWorld(int screenX) {
        float viewportWidth = gameClient.uiCamera().viewportWidth;
        int screenWidth = Gdx.graphics.getWidth();
        return screenX * (viewportWidth / screenWidth);
    }

    private float screenYToWorld(int screenY) {
        float viewportHeight = gameClient.uiCamera().viewportHeight;
        int screenHeight = Gdx.graphics.getHeight();
        return viewportHeight - (screenY * (viewportHeight / screenHeight));
    }
}
