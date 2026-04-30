package com.game.client.service;

import com.game.client.model.WorldFeedbackMessage;

/**
 * Stores short-lived in-world feedback for the active session.
 *
 * @since 0.1.0
 */
public final class WorldFeedbackService {
    private static final long INTERACTION_MESSAGE_DURATION_MILLIS = 3_500L;

    private long lastConsumedInteractionVersion = -1L;
    private WorldFeedbackMessage interactionMessage;

    /**
     * Captures a fresh interaction message from the network layer.
     *
     * @param message latest message text
     * @param version monotonically increasing message version
     * @param nowMillis current wall-clock time
     */
    public void captureInteractionMessage(String message, long version, long nowMillis) {
        if (version <= lastConsumedInteractionVersion || message == null || message.isBlank()) {
            return;
        }
        lastConsumedInteractionVersion = version;
        interactionMessage = new WorldFeedbackMessage(
                message,
                nowMillis + INTERACTION_MESSAGE_DURATION_MILLIS
        );
    }

    /**
     * Returns the current interaction message, if it is still active.
     *
     * @param nowMillis current wall-clock time
     * @return the active message text, or {@code null}
     */
    public String currentInteractionMessage(long nowMillis) {
        if (interactionMessage == null || !interactionMessage.activeAt(nowMillis)) {
            return null;
        }
        return interactionMessage.text();
    }

    /**
     * Clears all stored feedback for a new world session.
     */
    public void reset() {
        lastConsumedInteractionVersion = -1L;
        interactionMessage = null;
    }
}
