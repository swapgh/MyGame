package com.game.client.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.app.GameClient;

/**
 * Shared screen chrome for the desktop client.
 *
 * @since 0.1.0
 */
public final class ClientUiRenderer {
    /**
     * Draws the shared background and framing panels.
     *
     * @param gameClient the owning client
     * @param accentPhase a small animation phase value
     */
    public void renderBackdrop(GameClient gameClient, float accentPhase) {
        float width = gameClient.uiCamera().viewportWidth;
        float height = gameClient.uiCamera().viewportHeight;

        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        shapeRenderer.setProjectionMatrix(gameClient.uiCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(ClientUiPalette.BACKGROUND);
        shapeRenderer.rect(0f, 0f, width, height);

        shapeRenderer.setColor(withAlpha(ClientUiPalette.BACKGROUND_GLOW, 0.6f));
        shapeRenderer.rect(0f, height - 180f, width, 180f);

        float ribbonOffset = (float) Math.sin(accentPhase * 0.8f) * 22f;
        shapeRenderer.setColor(withAlpha(ClientUiPalette.PANEL_ALT, 0.85f));
        shapeRenderer.rect(width - 280f, 0f, 280f, height);
        shapeRenderer.rect(-60f + ribbonOffset, 120f, 220f, height - 240f);

        shapeRenderer.setColor(ClientUiPalette.PANEL);
        shapeRenderer.rect(64f, 84f, width - 128f, height - 168f);

        shapeRenderer.setColor(ClientUiPalette.PANEL_BORDER);
        shapeRenderer.rect(64f, height - 92f, width - 128f, 8f);
        shapeRenderer.rect(64f, 84f, 8f, height - 168f);
        shapeRenderer.end();
    }

    /**
     * Draws a framed content panel.
     *
     * @param gameClient the owning client
     * @param x panel x
     * @param y panel y
     * @param width panel width
     * @param height panel height
     */
    public void renderPanel(GameClient gameClient, float x, float y, float width, float height) {
        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        shapeRenderer.setProjectionMatrix(gameClient.uiCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(withAlpha(ClientUiPalette.PANEL_ALT, 0.92f));
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.setColor(ClientUiPalette.PANEL_BORDER);
        shapeRenderer.rect(x, y + height - 4f, width, 4f);
        shapeRenderer.rect(x, y, width, 2f);
        shapeRenderer.end();
    }

    /**
     * Draws a section heading block.
     *
     * @param gameClient the owning client
     * @param eyebrow small top label
     * @param title main heading
     * @param subtitle optional subtitle
     */
    public void renderHero(GameClient gameClient, String eyebrow, String title, String subtitle) {
        SpriteBatch batch = gameClient.spriteBatch();
        drawText(gameClient.font(), batch, eyebrow.toUpperCase(), 96f, 642f, 0.85f, ClientUiPalette.TEXT_ACCENT);
        drawText(gameClient.font(), batch, title, 96f, 596f, 1.7f, ClientUiPalette.TEXT_PRIMARY);
        drawText(gameClient.font(), batch, subtitle, 96f, 552f, 0.95f, ClientUiPalette.TEXT_MUTED);
    }

    /**
     * Draws a field line with focus styling.
     *
     * @param gameClient the owning client
     * @param label field label
     * @param value field value
     * @param x content x
     * @param y content y
     * @param focused whether the field is focused
     */
    public void renderField(GameClient gameClient, String label, String value, float x, float y, boolean focused) {
        renderPanel(gameClient, x - 16f, y - 34f, 500f, 52f);
        drawText(gameClient.font(), gameClient.spriteBatch(), label, x, y + 10f, 0.8f, ClientUiPalette.TEXT_MUTED);
        drawText(
                gameClient.font(),
                gameClient.spriteBatch(),
                value.isBlank() ? " " : value,
                x,
                y - 10f,
                1.05f,
                focused ? ClientUiPalette.TEXT_PRIMARY : ClientUiPalette.TEXT_ACCENT
        );
        if (focused) {
            drawText(gameClient.font(), gameClient.spriteBatch(), "ACTIVE", x + 360f, y + 10f, 0.75f, ClientUiPalette.TEXT_WARNING);
        }
    }

    /**
     * Draws a muted information line.
     *
     * @param gameClient the owning client
     * @param text the text
     * @param x position x
     * @param y position y
     */
    public void renderInfo(GameClient gameClient, String text, float x, float y) {
        drawText(gameClient.font(), gameClient.spriteBatch(), text, x, y, 0.9f, ClientUiPalette.TEXT_MUTED);
    }

    /**
     * Draws a stronger status line.
     *
     * @param gameClient the owning client
     * @param text the text
     * @param x position x
     * @param y position y
     * @param color status color
     */
    public void renderStatus(GameClient gameClient, String text, float x, float y, Color color) {
        drawText(gameClient.font(), gameClient.spriteBatch(), text, x, y, 0.95f, color);
    }

    /**
     * Draws a list row.
     *
     * @param gameClient the owning client
     * @param text row text
     * @param x row x
     * @param y row y
     * @param selected whether selected
     */
    public void renderListRow(GameClient gameClient, String text, float x, float y, boolean selected) {
        if (selected) {
            renderPanel(gameClient, x - 16f, y - 26f, 520f, 44f);
        }
        drawText(
                gameClient.font(),
                gameClient.spriteBatch(),
                text,
                x,
                y,
                1.0f,
                selected ? ClientUiPalette.TEXT_PRIMARY : ClientUiPalette.TEXT_MUTED
        );
    }

    private static void drawText(
            BitmapFont font,
            SpriteBatch batch,
            String text,
            float x,
            float y,
            float scale,
            Color color
    ) {
        float previousScaleX = font.getData().scaleX;
        float previousScaleY = font.getData().scaleY;
        Color previousColor = font.getColor().cpy();
        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, text, x, y);
        font.getData().setScale(previousScaleX, previousScaleY);
        font.setColor(previousColor);
    }

    private static Color withAlpha(Color color, float alpha) {
        return new Color(color.r, color.g, color.b, alpha);
    }
}
