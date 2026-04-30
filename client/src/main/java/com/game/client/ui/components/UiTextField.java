package com.game.client.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.game.client.app.GameClient;
import com.game.client.ui.render.UiRenderState;
import com.game.client.ui.theme.UiPalette;
import com.game.client.ui.theme.UiFont;

/**
 * Minimalistic text field widget with focus state and optional masking.
 *
 * @since 0.1.0
 */
public class UiTextField implements UiWidget {

    private final String label;
    private final StringBuilder value = new StringBuilder();
    private boolean maskValue;
    private boolean visible = true;
    private boolean enabled = true;

    private float x, y, width, height;
    private boolean hovered;
    private boolean focused;

    private float cursorBlinkTimer;
    private static final float CURSOR_BLINK_INTERVAL = 0.5f;

    public UiTextField(String label) {
        this(label, false);
    }

    public UiTextField(String label, boolean maskValue) {
        this.label = label;
        this.maskValue = maskValue;
    }

    public void setValue(String value) {
        this.value.setLength(0);
        this.value.append(value);
    }

    public String getValue() {
        return value.toString();
    }

    public void setMaskValue(boolean maskValue) {
        this.maskValue = maskValue;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            focused = false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void append(char character) {
        if (!enabled) {
            return;
        }
        value.append(character);
        cursorBlinkTimer = 0f;
    }

    public void backspace() {
        if (!enabled || value.length() == 0) {
            return;
        }
        value.deleteCharAt(value.length() - 1);
        cursorBlinkTimer = 0f;
    }

    public void clear() {
        value.setLength(0);
    }

    private String displayValue() {
        if (maskValue) {
            return "*".repeat(Math.max(0, value.length()));
        }
        return value.toString();
    }

    @Override
    public void render(GameClient gameClient, SpriteBatch batch) {
        if (!visible) {
            return;
        }

        UiFont uiFont = gameClient.uiFont();
        SpriteBatch spriteBatch = gameClient.spriteBatch();
        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();

        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Filled);

        Color fillColor;
        Color borderColor;

        if (focused) {
            fillColor = withAlpha(UiPalette.PANEL_ALT, 0.95f);
            borderColor = withAlpha(UiPalette.GLOW_BLUE, 0.3f);
        } else if (hovered) {
            fillColor = withAlpha(UiPalette.PANEL_ALT, 0.92f);
            borderColor = withAlpha(UiPalette.PANEL_BORDER, 0.3f);
        } else {
            fillColor = withAlpha(UiPalette.PANEL_ALT, 0.85f);
            borderColor = withAlpha(UiPalette.PANEL_BORDER, 0.18f);
        }

        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(x, y, width, height);
        UiRenderState.endShapes(gameClient);

        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor);
        shapeRenderer.rect(x, y, width, height);

        if (focused && cursorBlinkTimer < CURSOR_BLINK_INTERVAL) {
            BitmapFont bodyFontForCursor = uiFont.body;
            String displayForCursor = displayValue().isEmpty() ? "..." : displayValue();
            GlyphLayout cursorLayout = new GlyphLayout(bodyFontForCursor, displayForCursor);
            float cursorX = x + 4f + cursorLayout.width + 2f;
            float cursorTop = y + height - 6f;
            float cursorBottom = y + 6f;
            shapeRenderer.setColor(withAlpha(UiPalette.TEXT_ACCENT, 0.7f));
            shapeRenderer.line(cursorX, cursorBottom, cursorX, cursorTop);
        }
        UiRenderState.endShapes(gameClient);

        UiRenderState.beginText(gameClient);

        float labelY = y + height + 14f;
        BitmapFont smallFont = uiFont.small;
        Color previousSmall = smallFont.getColor().cpy();
        smallFont.setColor(UiPalette.TEXT_MUTED);
        smallFont.draw(spriteBatch, label.toUpperCase(), x + 4f, labelY);
        smallFont.setColor(previousSmall);

        BitmapFont bodyFont = uiFont.body;
        String display = displayValue().isEmpty() ? "..." : displayValue();
        Color fieldColor = focused ? UiPalette.TEXT_PRIMARY : UiPalette.TEXT_MUTED;
        Color previousBody = bodyFont.getColor().cpy();
        bodyFont.setColor(fieldColor);
        bodyFont.draw(spriteBatch, display, x + 4f, y + (height * 0.5f) + (bodyFont.getData().lineHeight * 0.3f));
        bodyFont.setColor(previousBody);
    }

    @Override
    public boolean handleMouseMove(float mouseX, float mouseY) {
        boolean wasHovered = hovered;
        hovered = enabled && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        return hovered != wasHovered;
    }

    @Override
    public boolean handleMousePressed(float mouseX, float mouseY, int button) {
        if (!enabled || !visible) {
            return false;
        }
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            focused = true;
            return true;
        }
        focused = false;
        return false;
    }

    @Override
    public boolean handleMouseReleased(float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean handleKeyTyped(char character) {
        if (!focused || !enabled) {
            return false;
        }
        if (character == '\b') {
            backspace();
            return true;
        }
        if (character >= 32 && character <= 126) {
            append(character);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleKeyPressed(int keyCode) {
        return false;
    }

    @Override
    public void update(float delta) {
        cursorBlinkTimer += delta;
        if (cursorBlinkTimer >= CURSOR_BLINK_INTERVAL * 2f) {
            cursorBlinkTimer = 0f;
        }
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
