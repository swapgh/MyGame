package com.game.client.ui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.game.client.app.GameClient;
import com.game.client.render.ClientUiPalette;
import com.game.client.render.UiFont;

/**
 * Minimalistic button widget with subtle border styling.
 *
 * @since 0.1.0
 */
public class UiButton implements UiWidget {

    private String text;
    private Runnable onClick;
    private boolean enabled = true;
    private boolean visible = true;

    private float x, y, width, height;
    private boolean hovered;
    private boolean focused;
    private boolean pressed;

    public UiButton(String text, Runnable onClick) {
        this.text = text;
        this.onClick = onClick;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void render(GameClient gameClient, SpriteBatch batch) {
        if (!visible) {
            return;
        }

        UiFont uiFont = gameClient.uiFont();
        SpriteBatch spriteBatch = gameClient.spriteBatch();
        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();

        if (spriteBatch.isDrawing()) {
            spriteBatch.end();
        }

        shapeRenderer.setProjectionMatrix(gameClient.uiCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Color fillColor;
        Color borderColor;

        if (!enabled) {
            fillColor = withAlpha(ClientUiPalette.PANEL_ALT, 0.6f);
            borderColor = withAlpha(ClientUiPalette.TEXT_MUTED, 0.15f);
        } else if (pressed) {
            fillColor = withAlpha(ClientUiPalette.GLOW_GOLD, 0.12f);
            borderColor = withAlpha(ClientUiPalette.GLOW_GOLD, 0.5f);
        } else if (hovered || focused) {
            fillColor = withAlpha(ClientUiPalette.GLOW_GOLD, 0.06f);
            borderColor = withAlpha(ClientUiPalette.GLOW_GOLD, 0.35f);
        } else {
            fillColor = withAlpha(ClientUiPalette.PANEL_ALT, 0.9f);
            borderColor = withAlpha(ClientUiPalette.PANEL_BORDER, 0.22f);
        }

        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(x, y, width, height);

        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        spriteBatch.begin();

        BitmapFont font = uiFont.body;
        GlyphLayout layout = new GlyphLayout(font, text);
        float textX = x + (width * 0.5f) - (layout.width * 0.5f);
        float textY = y + (height * 0.5f) + (layout.height * 0.35f);

        Color textColor = enabled ? ClientUiPalette.TEXT_PRIMARY : ClientUiPalette.TEXT_MUTED;
        Color previous = font.getColor().cpy();
        font.setColor(textColor);
        font.draw(spriteBatch, text, textX, textY);
        font.setColor(previous);
    }

    @Override
    public boolean handleMouseMove(float mouseX, float mouseY) {
        boolean wasHovered = hovered;
        hovered = enabled && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        return hovered != wasHovered;
    }

    @Override
    public boolean handleMousePressed(float mouseX, float mouseY, int button) {
        if (!enabled || !isVisible()) {
            return false;
        }
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            pressed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMouseReleased(float mouseX, float mouseY, int button) {
        boolean wasPressed = pressed;
        pressed = false;
        if (wasPressed && enabled && onClick != null
                && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            onClick.run();
            return true;
        }
        return false;
    }

    @Override
    public boolean handleKeyTyped(char character) {
        return false;
    }

    @Override
    public boolean handleKeyPressed(int keyCode) {
        if (keyCode == com.badlogic.gdx.Input.Keys.ENTER && enabled && focused && onClick != null) {
            onClick.run();
            return true;
        }
        return false;
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isHovered() {
        return hovered;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    private static Color withAlpha(Color color, float alpha) {
        return new Color(color.r, color.g, color.b, alpha);
    }
}
