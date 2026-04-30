package com.game.client.ui.core;

/**
 * Heading block for a screen document.
 *
 * @param eyebrow small top label
 * @param title main title
 * @param subtitle supporting text
 * @param centered whether to use centered rendering
 * @param anchorX left or center anchor depending on {@code centered}
 * @param anchorY top anchor
 * @since 0.1.0
 */
public record UiHero(
        String eyebrow,
        String title,
        String subtitle,
        boolean centered,
        float anchorX,
        float anchorY
) {

    /**
     * Creates a left-aligned hero block.
     *
     * @param eyebrow small top label
     * @param title main title
     * @param subtitle supporting text
     * @param x left anchor
     * @param y top anchor
     * @return the hero block
     */
    public static UiHero left(String eyebrow, String title, String subtitle, float x, float y) {
        return new UiHero(eyebrow, title, subtitle, false, x, y);
    }

    /**
     * Creates a centered hero block.
     *
     * @param eyebrow small top label
     * @param title main title
     * @param subtitle supporting text
     * @param centerX center anchor
     * @param topY top anchor
     * @return the hero block
     */
    public static UiHero centered(String eyebrow, String title, String subtitle, float centerX, float topY) {
        return new UiHero(eyebrow, title, subtitle, true, centerX, topY);
    }
}
