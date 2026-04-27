package com.game.shared.time;

/**
 * Immutable game-time snapshot expressed as a tick counter plus tick rate.
 *
 * @param tick the current simulation tick
 * @param tickRate the tick rate used by the simulation
 * @since 0.1.0
 */
public record GameClock(long tick, TickRate tickRate) {
    /**
     * Creates a clock starting at tick zero for the provided rate.
     *
     * @param tickRate the tick rate used by the clock
     * @return a new clock starting at tick zero
     */
    public static GameClock start(TickRate tickRate) {
        return new GameClock(0L, tickRate);
    }

    public GameClock {
        if (tick < 0L) {
            throw new IllegalArgumentException("tick cannot be negative");
        }
    }

    /**
     * Returns the next clock state after one simulation tick.
     *
     * @return a new clock advanced by one tick
     */
    public GameClock advance() {
        return new GameClock(tick + 1L, tickRate);
    }

    /**
     * Returns elapsed time using the tick rate's millisecond approximation.
     *
     * @return the elapsed time in milliseconds
     */
    public long elapsedMillis() {
        return tick * tickRate.tickDurationMillis();
    }

    /**
     * Returns elapsed time using the tick rate's exact seconds value.
     *
     * @return the elapsed time in seconds
     */
    public double elapsedSeconds() {
        return tick * tickRate.tickDurationSeconds();
    }
}
