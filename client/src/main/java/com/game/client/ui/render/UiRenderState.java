package com.game.client.ui.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.app.GameClient;

/**
 * Centralizes UI render-state transitions between shape and text passes.
 *
 * @since 0.1.0
 */
public final class UiRenderState {

    private UiRenderState() {
    }

    /**
     * Ensures the sprite batch is active for text drawing.
     *
     * @param gameClient owning client
     */
    public static void beginText(GameClient gameClient) {
        if (gameClient.shapeRenderer().isDrawing()) {
            gameClient.shapeRenderer().end();
        }
        if (!gameClient.spriteBatch().isDrawing()) {
            gameClient.spriteBatch().begin();
        }
    }

    /**
     * Ends the text pass if active.
     *
     * @param gameClient owning client
     */
    public static void endText(GameClient gameClient) {
        if (gameClient.spriteBatch().isDrawing()) {
            gameClient.spriteBatch().end();
        }
    }

    /**
     * Ensures the shape renderer is active with the requested type.
     *
     * @param gameClient owning client
     * @param shapeType desired shape pass
     */
    public static void beginShapes(GameClient gameClient, ShapeRenderer.ShapeType shapeType) {
        if (gameClient.spriteBatch().isDrawing()) {
            gameClient.spriteBatch().end();
        }
        if (gameClient.shapeRenderer().isDrawing()) {
            gameClient.shapeRenderer().end();
        }
        gameClient.shapeRenderer().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.shapeRenderer().begin(shapeType);
    }

    /**
     * Ends the active shape pass if present.
     *
     * @param gameClient owning client
     */
    public static void endShapes(GameClient gameClient) {
        if (gameClient.shapeRenderer().isDrawing()) {
            gameClient.shapeRenderer().end();
        }
    }
}
