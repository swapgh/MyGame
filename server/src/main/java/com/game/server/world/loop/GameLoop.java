package com.game.server.world.loop;

import com.game.server.world.ecs.EntityManager;
import com.game.server.world.ecs.SystemRegistry;
import com.game.shared.time.GameClock;
import com.game.shared.time.TickRate;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fixed-timestep game loop that drives the world simulation.
 * <p>The loop sleeps between ticks to maintain the configured {@link TickRate}.
 * Each iteration advances the {@link GameClock} by one tick and invokes all
 * registered systems via {@link SystemRegistry#tickAll}.</p>
 * <p>Start the loop with {@link #start()} on any thread; stop it safely with
 * {@link #stop()}. The loop runs on a dedicated virtual thread named
 * {@code world-game-loop}.</p>
 * @since 0.1.0
 */
public final class GameLoop {
    private final TickRate tickRate;
    private final EntityManager entityManager;
    private final SystemRegistry systemRegistry;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread loopThread;
    private volatile GameClock clock;

    /**
     * Creates a game loop for the given tick rate, entity manager, and system registry.
     * @param tickRate       the desired tick rate
     * @param entityManager  the shared entity manager
     * @param systemRegistry the ordered system registry to tick each iteration
     */
    public GameLoop(TickRate tickRate,EntityManager entityManager,SystemRegistry systemRegistry) {
        this.tickRate= tickRate;
        this.entityManager= entityManager;
        this.systemRegistry = systemRegistry;
        this.clock = GameClock.start(tickRate);
    }
    /**
     * Starts the game loop on a dedicated virtual thread.
     * <p>Does nothing if the loop is already running.</p>
     */
    public void start() {
        if (running.compareAndSet(false, true)){
            loopThread = Thread.ofVirtual().name("world-game-loop").start(this::loop);
        }
    }
    /**
     * Signals the game loop to stop and waits for the loop thread to finish.
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
     * @return {@code true} if the loop is active
     */
    public boolean isRunning() {
        return running.get();
    }
    /**
     * Returns the most recently completed game clock snapshot.
     * @return the current game clock
     */
    public GameClock clock() {
        return clock;
    }
    private void loop() {
        long tickDurationMillis = tickRate.tickDurationMillis();
        while (running.get()) {
            long tickStart = System.currentTimeMillis();
            systemRegistry.tickAll(entityManager, clock);
            clock = clock.advance();
            long elapsed = System.currentTimeMillis() - tickStart;
            long sleepMillis = tickDurationMillis - elapsed;
            if (sleepMillis > 0) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running.set(false);
                }
            }
        }
    }
}
