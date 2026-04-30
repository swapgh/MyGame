package com.game.client.ui.core;

import com.badlogic.gdx.graphics.Color;

/**
 * Lightweight text node used by screen documents.
 *
 * @param style text presentation style
 * @param text displayed content
 * @param x left anchor
 * @param y baseline position
 * @param color optional explicit color
 * @param selected whether a list row is selected
 * @since 0.1.0
 */
public record UiTextLine(
        UiTextStyle style,
        String text,
        float x,
        float y,
        Color color,
        boolean selected
) {

    /**
     * Creates an info line.
     *
     * @param text displayed content
     * @param x left anchor
     * @param y baseline position
     * @return the line node
     */
    public static UiTextLine info(String text, float x, float y) {
        return new UiTextLine(UiTextStyle.INFO, text, x, y, null, false);
    }

    /**
     * Creates a status line.
     *
     * @param text displayed content
     * @param x left anchor
     * @param y baseline position
     * @param color line color
     * @return the line node
     */
    public static UiTextLine status(String text, float x, float y, Color color) {
        return new UiTextLine(UiTextStyle.STATUS, text, x, y, color, false);
    }

    /**
     * Creates a list row line.
     *
     * @param text displayed content
     * @param x left anchor
     * @param y baseline position
     * @param selected whether selected
     * @return the line node
     */
    public static UiTextLine listRow(String text, float x, float y, boolean selected) {
        return new UiTextLine(UiTextStyle.LIST_ROW, text, x, y, null, selected);
    }
}
