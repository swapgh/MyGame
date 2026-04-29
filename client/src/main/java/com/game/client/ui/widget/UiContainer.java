package com.game.client.ui.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.game.client.app.GameClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Container widget that holds and lays out child widgets.
 *
 * @since 0.1.0
 */
public class UiContainer implements UiWidget {

    private final List<UiWidget> children = new ArrayList<>();
    private float x, y, width, height;
    private boolean visible = true;
    private LayoutType layoutType = LayoutType.VERTICAL;
    private float padding = 0f;
    private float spacing = 0f;

    public enum LayoutType {
        VERTICAL,
        HORIZONTAL,
        ABSOLUTE
    }

    public UiContainer() {
    }

    public UiContainer(LayoutType layoutType, float padding, float spacing) {
        this.layoutType = layoutType;
        this.padding = padding;
        this.spacing = spacing;
    }

    public void addChild(UiWidget widget) {
        children.add(widget);
        if (layoutType != LayoutType.ABSOLUTE) {
            layoutChildren();
        }
    }

    public void removeChild(UiWidget widget) {
        children.remove(widget);
        if (layoutType != LayoutType.ABSOLUTE) {
            layoutChildren();
        }
    }

    public void clearChildren() {
        children.clear();
    }

    public List<UiWidget> getChildren() {
        return children;
    }

    public UiWidget focusedChild() {
        for (UiWidget child : children) {
            if (child.isFocused()) {
                return child;
            }
        }
        return null;
    }

    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
        layoutChildren();
    }

    public void setPadding(float padding) {
        this.padding = padding;
        layoutChildren();
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
        layoutChildren();
    }

    private void layoutChildren() {
        if (children.isEmpty()) {
            return;
        }

        float currentPos = padding;
        if (layoutType == LayoutType.VERTICAL) {
            for (UiWidget child : children) {
                float childY = y + height - currentPos - child.getHeight();
                child.setBounds(x + padding, childY, width - 2 * padding, child.getHeight());
                currentPos += child.getHeight() + spacing;
            }
        } else if (layoutType == LayoutType.HORIZONTAL) {
            for (UiWidget child : children) {
                child.setBounds(x + currentPos, y + height - padding - child.getHeight(),
                        child.getWidth(), child.getHeight());
                currentPos += child.getWidth() + spacing;
            }
        }
    }

    @Override
    public void render(GameClient gameClient, SpriteBatch batch) {
        if (!visible) {
            return;
        }
        for (UiWidget child : children) {
            if (child.isVisible()) {
                child.render(gameClient, batch);
            }
        }
    }

    @Override
    public boolean handleMouseMove(float mouseX, float mouseY) {
        if (!visible) {
            return false;
        }
        boolean handled = false;
        for (UiWidget child : children) {
            if (child.isVisible() && child.handleMouseMove(mouseX, mouseY)) {
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public boolean handleMousePressed(float mouseX, float mouseY, int button) {
        if (!visible) {
            return false;
        }
        for (UiWidget child : children) {
            if (child.isVisible() && child.handleMousePressed(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleMouseReleased(float mouseX, float mouseY, int button) {
        if (!visible) {
            return false;
        }
        for (UiWidget child : children) {
            if (child.isVisible() && child.handleMouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleKeyTyped(char character) {
        if (!visible) {
            return false;
        }
        for (UiWidget child : children) {
            if (child.isFocused() && child.handleKeyTyped(character)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleKeyPressed(int keyCode) {
        if (!visible) {
            return false;
        }
        for (UiWidget child : children) {
            if (child.isFocused() && child.handleKeyPressed(keyCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(float delta) {
        if (!visible) {
            return;
        }
        for (UiWidget child : children) {
            child.update(delta);
        }
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (layoutType != LayoutType.ABSOLUTE) {
            layoutChildren();
        }
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
