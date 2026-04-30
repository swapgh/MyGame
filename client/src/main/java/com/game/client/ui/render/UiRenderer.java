package com.game.client.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.app.GameClient;
import com.game.client.ui.theme.UiFont;
import com.game.client.ui.theme.UiPalette;

/**
 * Shared screen chrome for the desktop client.
 *
 * @since 0.1.0
 */
public final class UiRenderer {

    /**
     * Draws the shared background and framing panels.
     *
     * @param gameClient  the owning client
     * @param accentPhase a small animation phase value
     */
    public void renderBackdrop(GameClient gameClient, float accentPhase) {
        float width = gameClient.uiCamera().viewportWidth;
        float height = gameClient.uiCamera().viewportHeight;

        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(UiPalette.BACKGROUND);
        shapeRenderer.rect(0f, 0f, width, height);

        float glowX = width * 0.5f;
        float glowY = height * 0.52f + (float) Math.sin(accentPhase * 0.15f) * 8f;
        shapeRenderer.setColor(withAlpha(UiPalette.GLOW_BLUE, 0.14f));
        shapeRenderer.rect(glowX - 260f, glowY - 150f, 520f, 300f);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_ALT, 0.10f));
        shapeRenderer.rect(0f, height - 120f, width, 120f);
        shapeRenderer.rect(0f, 0f, width, 90f);
        UiRenderState.endShapes(gameClient);
    }

    /**
     * Draws a framed content panel.
     *
     * @param gameClient the owning client
     * @param x          panel x
     * @param y          panel y
     * @param width      panel width
     * @param height     panel height
     */
    public void renderPanel(GameClient gameClient, float x, float y, float width, float height) {
        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_SHADOW, 0.42f));
        shapeRenderer.rect(x + 8f, y - 8f, width, height);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_ALT, 0.96f));
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL, 0.94f));
        shapeRenderer.rect(x + 4f, y + 4f, width - 8f, height - 8f);
        UiRenderState.endShapes(gameClient);

        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_BORDER, 0.55f));
        shapeRenderer.rect(x, y, width, height);
        UiRenderState.endShapes(gameClient);
    }

    /**
     * Draws a glass-like launcher panel.
     *
     * @param gameClient the owning client
     * @param x panel x
     * @param y panel y
     * @param width panel width
     * @param height panel height
     */
    public void renderLauncherPanel(GameClient gameClient, float x, float y, float width, float height) {
        renderPanel(gameClient, x, y, width, height);
    }

    /**
     * Draws a section heading block.
     *
     * @param gameClient the owning client
     * @param eyebrow    small top label
     * @param title      main heading
     * @param subtitle   optional subtitle
     */
    public void renderHero(GameClient gameClient, String eyebrow, String title, String subtitle) {
        UiFont f = gameClient.uiFont();
        UiRenderState.beginText(gameClient);
        SpriteBatch batch = gameClient.spriteBatch();
        drawText(f.small, batch, eyebrow.toUpperCase(), 96f, 642f, UiPalette.TEXT_MUTED);
        drawText(f.title, batch, title, 96f, 596f, UiPalette.TEXT_PRIMARY);
        drawText(f.body, batch, subtitle, 96f, 552f, UiPalette.TEXT_MUTED);
    }

    /**
     * Draws a centered heading block.
     *
     * @param gameClient the owning client
     * @param eyebrow small upper label
     * @param title main heading
     * @param subtitle supporting line
     * @param centerX horizontal center anchor
     * @param topY top anchor for the block
     */
    public void renderHeroCentered(GameClient gameClient, String eyebrow, String title, String subtitle,
                                   float centerX, float topY) {
        UiFont f = gameClient.uiFont();
        UiRenderState.beginText(gameClient);
        SpriteBatch batch = gameClient.spriteBatch();
        if (!eyebrow.isBlank()) {
            drawCentered(f.small, batch, eyebrow.toUpperCase(), centerX, topY, UiPalette.TEXT_MUTED);
        }
        drawCentered(f.title, batch, title, centerX, topY - 24f, UiPalette.TEXT_PRIMARY);
        drawCentered(f.body, batch, subtitle, centerX, topY - 68f, UiPalette.TEXT_MUTED);
    }

    /**
     * Draws a field line with focus styling.
     *
     * @param gameClient the owning client
     * @param label      field label
     * @param value      field value
     * @param x          content x
     * @param y          content y
     * @param focused    whether the field is focused
     */
    public void renderField(GameClient gameClient, String label, String value,
                            float x, float y, boolean focused) {
        renderPanel(gameClient, x - 14f, y - 12f, 460f, 52f);
        UiFont f = gameClient.uiFont();
        SpriteBatch batch = gameClient.spriteBatch();
        UiRenderState.beginText(gameClient);
        drawText(f.small, batch, label.toUpperCase(), x, y + 56f, UiPalette.TEXT_MUTED);
        drawText(f.body, batch,
                value.isBlank() ? "..." : value,
                x, y + 20f,
                focused ? UiPalette.TEXT_PRIMARY : UiPalette.TEXT_ACCENT);
        if (focused) {
            drawText(f.small, batch, "ACTIVE", x + 314f, y + 56f, UiPalette.TEXT_SUCCESS);
        }
    }

    /**
     * Draws a launcher-style field with a minimal icon and focus glow.
     *
     * @param gameClient the owning client
     * @param label field label
     * @param value field value
     * @param x field x
     * @param y field y
     * @param focused whether focused
     * @param iconType either "user" or "lock"
     */
    public void renderLauncherField(GameClient gameClient, String label, String value,
                                    float x, float y, boolean focused, String iconType) {
        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_SHADOW, 0.32f));
        shapeRenderer.rect(x + 6f, y - 6f, 556f, 56f);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_ALT, 0.94f));
        shapeRenderer.rect(x, y, 556f, 56f);
        if (focused) {
            shapeRenderer.setColor(withAlpha(UiPalette.GLOW_BLUE, 0.12f));
            shapeRenderer.rect(x + 2f, y + 2f, 552f, 52f);
        }
        UiRenderState.endShapes(gameClient);

        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_BORDER, focused ? 0.42f : 0.22f));
        shapeRenderer.rect(x, y, 556f, 56f);
        UiRenderState.endShapes(gameClient);

        UiFont f = gameClient.uiFont();
        SpriteBatch batch = gameClient.spriteBatch();
        UiRenderState.beginText(gameClient);
        drawText(f.small, batch, label.toUpperCase(), x, y + 78f, UiPalette.TEXT_MUTED);
        drawFieldIcon(gameClient, x + 18f, y + 16f, iconType, focused);
        drawText(f.body, batch, value.isBlank() ? "..." : value, x + 62f, y + 34f,
                focused ? UiPalette.TEXT_PRIMARY : UiPalette.TEXT_MUTED);
    }

    /**
     * Draws a small status badge.
     *
     * @param gameClient the owning client
     * @param x badge x
     * @param y badge y
     * @param text badge text
     */
    public void renderStatusBadge(GameClient gameClient, float x, float y, String text) {
        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(withAlpha(UiPalette.TEXT_SUCCESS, 0.96f));
        shapeRenderer.circle(x + 6f, y - 6f, 6f, 18);
        UiRenderState.endShapes(gameClient);
        UiRenderState.beginText(gameClient);
        drawText(gameClient.uiFont().small, gameClient.spriteBatch(), text, x + 22f, y, UiPalette.TEXT_SUCCESS);
    }

    /**
     * Draws a launcher-style action button.
     *
     * @param gameClient the owning client
     * @param text button text
     * @param x button x
     * @param y button y
     * @param width button width
     * @param height button height
     * @param highlighted whether highlighted
     */
    public void renderActionButton(GameClient gameClient, String text,
                                   float x, float y, float width, float height, boolean highlighted) {
        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_SHADOW, 0.36f));
        shapeRenderer.rect(x + 6f, y - 6f, width, height);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_ALT, 0.98f));
        shapeRenderer.rect(x, y, width, height);
        if (highlighted) {
            shapeRenderer.setColor(withAlpha(UiPalette.GLOW_GOLD, 0.10f));
            shapeRenderer.rect(x + 2f, y + 2f, width - 4f, height - 4f);
        }
        UiRenderState.endShapes(gameClient);

        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(withAlpha(UiPalette.PANEL_BORDER, highlighted ? 0.68f : 0.28f));
        shapeRenderer.rect(x, y, width, height);
        UiRenderState.endShapes(gameClient);
        UiRenderState.beginText(gameClient);

        drawCentered(gameClient.uiFont().body, gameClient.spriteBatch(), text, x + (width * 0.5f), y + (height * 0.62f),
                UiPalette.TEXT_PRIMARY);
    }

    /**
     * Draws a bottom shortcut bar.
     *
     * @param gameClient the owning client
     * @param x bar x
     * @param y bar y
     * @param width bar width
     * @param height bar height
     */
    public void renderShortcutBar(GameClient gameClient, float x, float y, float width, float height) {
        renderLauncherPanel(gameClient, x, y, width, height);
    }

    /**
     * Draws a short centered separator.
     *
     * @param gameClient the owning client
     * @param centerX separator center x
     * @param y separator y
     * @param width separator width
     */
    public void renderDivider(GameClient gameClient, float centerX, float y, float width) {
        // Intentionally empty for the current cleaner login direction.
    }

    /**
     * Draws a muted information line.
     *
     * @param gameClient the owning client
     * @param text       the text
     * @param x          position x
     * @param y          position y
     */
    public void renderInfo(GameClient gameClient, String text, float x, float y) {
        UiRenderState.beginText(gameClient);
        drawText(gameClient.uiFont().small, gameClient.spriteBatch(),
                text, x, y, UiPalette.TEXT_MUTED);
    }

    /**
     * Draws a stronger status line.
     *
     * @param gameClient the owning client
     * @param text       the text
     * @param x          position x
     * @param y          position y
     * @param color      status color
     */
    public void renderStatus(GameClient gameClient, String text, float x, float y, Color color) {
        UiRenderState.beginText(gameClient);
        drawText(gameClient.uiFont().body, gameClient.spriteBatch(), text, x, y, color);
    }

    /**
     * Draws a list row.
     *
     * @param gameClient the owning client
     * @param text       row text
     * @param x          row x
     * @param y          row y
     * @param selected   whether selected
     */
    public void renderListRow(GameClient gameClient, String text, float x, float y, boolean selected) {
        if (selected) {
            renderPanel(gameClient, x - 18f, y - 30f, 520f, 52f);
        }
        UiRenderState.beginText(gameClient);
        drawText(gameClient.uiFont().body, gameClient.spriteBatch(), text, x, y,
                selected ? UiPalette.TEXT_PRIMARY : UiPalette.TEXT_MUTED);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private static void drawText(BitmapFont font, SpriteBatch batch,
                                 String text, float x, float y, Color color) {
        Color previous = font.getColor().cpy();
        font.setColor(color);
        font.draw(batch, text, x, y);
        font.setColor(previous);
    }

    private static void drawCentered(BitmapFont font, SpriteBatch batch,
                                     String text, float centerX, float y, Color color) {
        GlyphLayout layout = new GlyphLayout(font, text);
        drawText(font, batch, text, centerX - (layout.width * 0.5f), y, color);
    }

    private void drawFieldIcon(GameClient gameClient, float x, float y, String iconType, boolean focused) {
        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(withAlpha(focused ? UiPalette.TEXT_PRIMARY : UiPalette.TEXT_MUTED, 0.86f));
        if ("lock".equals(iconType)) {
            shapeRenderer.rect(x + 4f, y + 2f, 18f, 16f);
            shapeRenderer.arc(x + 13f, y + 18f, 7f, 0f, 180f, 16);
        } else {
            shapeRenderer.circle(x + 13f, y + 18f, 7f, 20);
            shapeRenderer.line(x + 5f, y + 4f, x + 21f, y + 4f);
            shapeRenderer.line(x + 5f, y + 4f, x + 8f, y + 12f);
            shapeRenderer.line(x + 21f, y + 4f, x + 18f, y + 12f);
        }
        UiRenderState.endShapes(gameClient);
        UiRenderState.beginText(gameClient);
    }

    private static Color withAlpha(Color color, float alpha) {
        return new Color(color.r, color.g, color.b, alpha);
    }
}
