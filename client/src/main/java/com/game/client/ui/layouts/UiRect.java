package com.game.client.ui.layouts;

/**
 * Immutable rectangle used by UI layout helpers.
 *
 * @param x left edge
 * @param y bottom edge
 * @param width rectangle width
 * @param height rectangle height
 * @since 0.1.0
 */
public record UiRect(float x, float y, float width, float height) {

    /**
     * Returns the horizontal center.
     *
     * @return the center x coordinate
     */
    public float centerX() {
        return x + (width * 0.5f);
    }

    /**
     * Returns the top edge.
     *
     * @return the top y coordinate
     */
    public float top() {
        return y + height;
    }
}
