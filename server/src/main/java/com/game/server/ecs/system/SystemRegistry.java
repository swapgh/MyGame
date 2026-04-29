package com.game.server.ecs.system;

import com.game.server.ecs.entity.EntityManager;
import com.game.shared.time.GameClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Ordered registry of {@link GameSystem} instances ticked each simulation step.
 *
 * <p>Systems are ticked in registration order. Register systems once at startup;
 * dynamic registration mid-tick is not supported.</p>
 *
 * @since 0.1.0
 */
public final class SystemRegistry {
    private final List<GameSystem> systems = new ArrayList<>();

    /**
     * Registers a system to be ticked each game loop iteration.
     * @param system the system to register
     */
    public void register(GameSystem system){
        systems.add(system);
    }
    /**
     * Returns an unmodifiable view of the registered systems in tick order.
     *
     * @return the registered systems
     */
    public List<GameSystem> systems(){
        return Collections.unmodifiableList(systems);
    }
    /**
     * Ticks all registered systems in registration order.
     * @param entities the shared entity manager
     * @param clock    the current game clock snapshot
     */
    public void tickAll(EntityManager entities, GameClock clock){
        for (GameSystem system : systems) {
            system.tick(entities, clock);
        }
    }

    /**
     * Returns the number of registered systems.
     * @return the system count
     */
    public int size(){
        return systems.size();
    }

}
