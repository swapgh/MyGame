package com.game.client.ui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.game.client.app.GameClient;
import com.game.client.render.UiFont;

/**
 * Minimalistic text label widget.
 *
 * @since 0.1.0
 */
public class UiLabel implements UiWidget {

    private final String text;
    private FontSize fontSize;
    private Color color;
    private Alignment alignment;

    private float x, y, width, height;
    private boolean visible = true;

    public enum FontSize {
        SMALL, BODY, TITLE
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

    public UiLabel(String text) {
        this(text, FontSize.SMALL, Color.WHITE, Alignment.LEFT);
    }

    public UiLabel(String text, FontSize fontSize) {
        this(text, fontSize, Color.WHITE, Alignment.LEFT);
    }

    public UiLabel(String text, FontSize fontSize, Color color) {
        this(text, fontSize, color, Alignment.LEFT);
    }

    public UiLabel(String text, FontSize fontSize, Color color, Alignment alignment) {
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.alignment = alignment;
    }

    public void setText(String text) {
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    private BitmapFont resolveFont(GameClient gameClient) {
        UiFont uiFont = gameClient.uiFont();
        return switch (fontSize) {
            case SMALL -> uiFont.small;
            case BODY -> uiFont.body;
            case TITLE -> uiFont.title;
        };
    }

    private float measureHeight(GameClient gameClient) {
        BitmapFont font = resolveFont(gameClient);
        return font.getData().lineHeight + 4f;
    }

    @Override
    public void render(GameClient gameClient, SpriteBatch batch) {
        if (!visible || text.isEmpty()) {
            return;
        }
        BitmapFont font = resolveFont(gameClient);
        GlyphLayout layout = new GlyphLayout(font, text);
        float drawX = switch (alignment) {
            case LEFT -> x;
            case CENTER -> x + (width * 0.5f) - (layout.width * 0.5f);
            case RIGHT -> x + width - layout.width;
        };
        float drawY = y + height - ((height - layout.height) * 0.5f);

        Color previous = font.getColor().cpy();
        font.setColor(color);
        font.draw(batch, text, drawX, drawY);
        font.setColor(previous);
    }

    @Override
    public boolean handleMouseMove(float mouseX, float mouseY) {
        return false;
    }

    @Override
    public boolean handleMousePressed(float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean handleMouseReleased(float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean handleKeyTyped(char character) {
        return false;
    }

    @Override
    public boolean handleKeyPressed(int keyCode) {
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
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isHovered() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }
}
