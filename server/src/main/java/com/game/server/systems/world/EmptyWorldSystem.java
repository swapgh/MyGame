package com.game.server.systems.world;

import com.game.server.ecs.entity.EntityManager;
import com.game.server.ecs.system.GameSystem;
import com.game.shared.time.GameClock;

/**
 * Minimal Phase 3 system that gives the world loop a concrete registered system.
 *
 * @since 0.1.0
 */
public final class EmptyWorldSystem implements GameSystem {
    /**
     * Executes a no-op simulation step.
     *
     * @param entities the world entity manager
     * @param clock the current game clock
     */
    @Override
    public void tick(EntityManager entities, GameClock clock) {
        // Intentionally empty while the world is still a skeleton.
    }
}
