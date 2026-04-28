package com.game.server.world.loop;

import com.game.shared.time.GameClock;

/**
 * Snapshot of a completed world tick.
 *
 * @param tickNumber the completed tick number
 * @param clock the updated game clock
 * @param durationMillis the tick execution time in milliseconds
 * @since 0.1.0
 */
public record WorldTick(long tickNumber, GameClock clock, long durationMillis) {
}
