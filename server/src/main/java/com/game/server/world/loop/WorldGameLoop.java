package com.game.server.world.loop;

import com.game.server.ecs.entity.EntityManager;
import com.game.server.ecs.system.SystemRegistry;
import com.game.shared.time.GameClock;
import com.game.shared.time.TickRate;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fixed-timestep game loop that drives the world simulation.
 *
 * @since 0.1.0
 */
public final class WorldGameLoop {
    private final EntityManager entityManager;
    private final SystemRegistry systemRegistry;
    private final TickScheduler tickScheduler;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread loopThread;
    private volatile GameClock clock;
    private volatile WorldTick lastTick;

    /**
     * Creates a game loop for the given tick rate, entity manager, and system registry.
     *
     * @param tickRate the desired tick rate
     * @param entityManager the shared entity manager
     * @param systemRegistry the ordered system registry to tick each iteration
     */
    public WorldGameLoop(TickRate tickRate, EntityManager entityManager, SystemRegistry systemRegistry) {
        this.entityManager = entityManager;
        this.systemRegistry = systemRegistry;
        this.tickScheduler = new TickScheduler(tickRate);
        this.clock = GameClock.start(tickRate);
        this.lastTick = new WorldTick(0L, clock, 0L);
    }

    /**
     * Starts the game loop on a dedicated virtual thread.
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            loopThread = Thread.ofVirtual().name("world-game-loop").start(this::loop);
        }
    }

    /**
     * Signals the game loop to stop and waits for the loop thread to finish.
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public void stop() throws InterruptedException {
        running.set(false);
        if (loopThread != null) {
            loopThread.join();
        }
    }

    /**
     * Returns whether the game loop is currently running.
     *
     * @return {@code true} if the loop is active
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Returns the most recently completed game clock snapshot.
     *
     * @return the current game clock
     */
    public GameClock clock() {
        return clock;
    }

    /**
     * Returns the most recent completed world tick snapshot.
     *
     * @return the last completed world tick
     */
    public WorldTick lastTick() {
        return lastTick;
    }

    private void loop() {
        while (running.get()) {
            long tickStart = System.currentTimeMillis();
            systemRegistry.tickAll(entityManager, clock);
            clock = clock.advance();
            long elapsed = System.currentTimeMillis() - tickStart;
            lastTick = new WorldTick(clock.tick(), clock, elapsed);
            long sleepMillis = tickScheduler.sleepMillisFor(elapsed);
            if (sleepMillis > 0) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    running.set(false);
                }
            }
        }
    }
}
