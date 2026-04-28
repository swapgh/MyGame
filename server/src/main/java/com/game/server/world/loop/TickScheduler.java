package com.game.server.world.loop;

import com.game.shared.time.TickRate;

/**
 * Computes sleep timing for the fixed-step world loop.
 *
 * @since 0.1.0
 */
public final class TickScheduler {
    private final TickRate tickRate;

    /**
     * Creates a scheduler for the provided tick rate.
     *
     * @param tickRate the target tick rate
     */
    public TickScheduler(TickRate tickRate) {
        this.tickRate = tickRate;
    }

    /**
     * Returns how long the loop should sleep after the current tick finishes.
     *
     * @param elapsedMillis the time already spent on the tick
     * @return the remaining sleep time, or zero if the tick already overran
     */
    public long sleepMillisFor(long elapsedMillis) {
        return Math.max(tickRate.tickDurationMillis() - elapsedMillis, 0L);
    }
}
