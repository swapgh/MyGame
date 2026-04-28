package com.game.client.network;

/**
 * Placeholder ping tracker for later client networking phases.
 *
 * @since 0.1.0
 */
public final class PingService {
    private volatile long lastPingAtMillis;

    /**
     * Marks a new ping timestamp.
     */
    public void markPing() {
        lastPingAtMillis = System.currentTimeMillis();
    }

    /**
     * Returns the last recorded ping timestamp.
     *
     * @return the last ping timestamp in milliseconds
     */
    public long lastPingAtMillis() {
        return lastPingAtMillis;
    }
}
