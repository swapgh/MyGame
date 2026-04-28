package com.game.server.world.loop;

import com.game.server.world.ecs.EntityManager;
import com.game.server.world.ecs.SystemRegistry;
import com.game.shared.time.GameClock;
import com.game.shared.time.TickRate;

/**
 * Backward-compatible wrapper kept while the world loop naming is aligned to {@link WorldGameLoop}.
 *
 * @since 0.1.0
 */
@Deprecated(since = "0.1.0")
public final class GameLoop {
    private final WorldGameLoop delegate;

    /**
     * Creates a game loop for the given tick rate, entity manager, and system registry.
     *
     * @param tickRate the desired tick rate
     * @param entityManager the shared entity manager
     * @param systemRegistry the ordered system registry to tick each iteration
     */
    @Deprecated(since = "0.1.0")
    public GameLoop(TickRate tickRate, EntityManager entityManager, SystemRegistry systemRegistry) {
        this.delegate = new WorldGameLoop(tickRate, entityManager, systemRegistry);
    }

    /**
     * Starts the game loop on a dedicated virtual thread.
     */
    @Deprecated(since = "0.1.0")
    public void start() {
        delegate.start();
    }

    /**
     * Signals the game loop to stop and waits for the loop thread to finish.
     *
     * @throws InterruptedException if interrupted while waiting
     */
    @Deprecated(since = "0.1.0")
    public void stop() throws InterruptedException {
        delegate.stop();
    }

    /**
     * Returns whether the game loop is currently running.
     *
     * @return {@code true} if the loop is active
     */
    @Deprecated(since = "0.1.0")
    public boolean isRunning() {
        return delegate.isRunning();
    }

    /**
     * Returns the most recently completed game clock snapshot.
     *
     * @return the current game clock
     */
    @Deprecated(since = "0.1.0")
    public GameClock clock() {
        return delegate.clock();
    }

    /**
     * Returns the most recent completed world tick snapshot.
     *
     * @return the last completed world tick
     */
    @Deprecated(since = "0.1.0")
    public WorldTick lastTick() {
        return delegate.lastTick();
    }
}
