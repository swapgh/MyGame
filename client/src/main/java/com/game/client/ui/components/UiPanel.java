package com.game.client.ui.components;

import com.game.client.ui.layouts.UiRect;

/**
 * Common contract for reusable UI panel sections.
 *
 * @since 0.1.0
 */
public interface UiPanel {

    /**
     * Applies the panel bounds.
     *
     * @param bounds panel rectangle
     */
    void setBounds(UiRect bounds);

    /**
     * Returns the current panel bounds.
     *
     * @return the current bounds
     */
    UiRect bounds();
}
