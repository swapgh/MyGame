package com.game.client.ui.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.client.app.GameClient;
import com.game.client.ui.layouts.UiRect;

/**
 * Reusable form panel that hosts child components.
 *
 * @since 0.1.0
 */
public final class UiFormPanel implements UiPanel {
    private final UiContainer root;
    private UiRect bounds = new UiRect(0f, 0f, 0f, 0f);

    /**
     * Creates a form panel backed by the given widget container.
     *
     * @param root widget root
     */
    public UiFormPanel(UiContainer root) {
        this.root = root;
    }

    @Override
    public void setBounds(UiRect bounds) {
        this.bounds = bounds;
        root.setBounds(bounds.x(), bounds.y(), bounds.width(), bounds.height());
    }

    @Override
    public UiRect bounds() {
        return bounds;
    }

    /**
     * Returns the widget root used by this panel.
     *
     * @return the root container
     */
    public UiContainer root() {
        return root;
    }

    /**
     * Renders the contained widgets.
     *
     * @param gameClient owning client
     * @param batch active batch
     */
    public void render(GameClient gameClient, SpriteBatch batch) {
        root.render(gameClient, batch);
    }

    /**
     * Updates all child widgets.
     *
     * @param delta frame delta
     */
    public void update(float delta) {
        root.update(delta);
    }

    /**
     * Clears focus from all direct child widgets.
     */
    public void clearFocus() {
        for (UiWidget child : root.getChildren()) {
            child.setFocused(false);
        }
    }
}
