package com.game.shared.time;

/**
 * Describes how often a simulation updates per second.
 * @param ticksPerSecond the number of simulation ticks per second
 * @since 0.1.0
 */
public record TickRate(int ticksPerSecond) {
    public static final TickRate DEFAULT = new TickRate(20);

    public TickRate {
        if (ticksPerSecond <= 0) {
            throw new IllegalArgumentException("ticksPerSecond must be greater than zero");
        }
    }
    /**
     * Returns the approximate duration of a single tick in milliseconds.
     * @return the approximate duration of one tick in milliseconds
     */
    public long tickDurationMillis() {
        return 1000L / ticksPerSecond;
    }
    /**
     * Returns the exact duration of a single tick in seconds.
     * @return the exact duration of one tick in seconds
     */
    public double tickDurationSeconds() {
        return 1.0d / ticksPerSecond;
    }
}
