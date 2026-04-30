package com.game.client.model;

/**
 * Short-lived world feedback shown to the player.
 *
 * @param text the message text
 * @param expiresAtMillis when the message should stop being shown
 * @since 0.1.0
 */
public record WorldFeedbackMessage(String text, long expiresAtMillis) {
    /**
     * Returns whether the message is still active.
     *
     * @param nowMillis current wall-clock time
     * @return {@code true} when the message should still be visible
     */
    public boolean activeAt(long nowMillis) {
        return text != null && !text.isBlank() && nowMillis < expiresAtMillis;
    }
}
