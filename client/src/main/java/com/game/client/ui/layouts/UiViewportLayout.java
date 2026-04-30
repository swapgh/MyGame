package com.game.client.ui.layouts;

/**
 * Shared viewport-relative layout helpers for client screens.
 *
 * @since 0.1.0
 */
public final class UiViewportLayout {

    private UiViewportLayout() {
    }

    /**
     * Builds a centered panel rectangle while respecting a minimum bottom inset.
     *
     * @param viewportWidth current viewport width
     * @param viewportHeight current viewport height
     * @param panelWidth requested panel width
     * @param panelHeight requested panel height
     * @param minimumY minimum bottom offset
     * @return the computed panel rectangle
     */
    public static UiRect centeredPanel(
            float viewportWidth,
            float viewportHeight,
            float panelWidth,
            float panelHeight,
            float minimumY
    ) {
        float x = (viewportWidth - panelWidth) * 0.5f;
        float y = Math.max(minimumY, (viewportHeight * 0.5f) - (panelHeight * 0.5f));
        return new UiRect(x, y, panelWidth, panelHeight);
    }
}
