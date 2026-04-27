package com.game.server.world.ecs;

import com.game.shared.time.GameClock;

/**
 * Contract for all ECS systems that run each game tick.
 * <p>Implementations operate directly on the shared {@link EntityManager} and
 * are called in registration order by {@link SystemRegistry} once per tick.
 * Systems should be stateless or hold only tick-local state.</p>
 * @since 0.1.0
 */
public interface GameSystem {
    /**
     * Executes this system's logic for the current tick.
     *
     * @param entities the shared entity manager for this tick
     * @param clock    the current game clock snapshot
     */
    void tick(EntityManager entities, GameClock clock);
}
