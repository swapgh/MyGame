package com.game.client.components;

import com.game.shared.math.Vec2;

/**
 * Client-side render state for one replicated world entity.
 *
 * @since 0.1.0
 */
public final class WorldEntityRenderState {
    private final long entityId;
    private Vec2 displayPosition;
    private Vec2 targetPosition;
    private Vec2 velocity;
    private int currentHealth;
    private int maxHealth;
    private boolean alive;
    private long respawnTicksRemaining;

    /**
     * Creates a render state snapshot for an entity.
     *
     * @param entityId the entity id
     * @param displayPosition the rendered position
     * @param targetPosition the authoritative target position
     * @param velocity the authoritative velocity
     * @param currentHealth the current health
     * @param maxHealth the maximum health
     * @param alive whether the entity is alive
     * @param respawnTicksRemaining remaining respawn ticks
     */
    public WorldEntityRenderState(
            long entityId,
            Vec2 displayPosition,
            Vec2 targetPosition,
            Vec2 velocity,
            int currentHealth,
            int maxHealth,
            boolean alive,
            long respawnTicksRemaining
    ) {
        this.entityId = entityId;
        this.displayPosition = displayPosition;
        this.targetPosition = targetPosition;
        this.velocity = velocity;
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
        this.alive = alive;
        this.respawnTicksRemaining = respawnTicksRemaining;
    }

    public long entityId() {
        return entityId;
    }

    public Vec2 displayPosition() {
        return displayPosition;
    }

    public int currentHealth() {
        return currentHealth;
    }

    public int maxHealth() {
        return maxHealth;
    }

    public boolean alive() {
        return alive;
    }

    public long respawnTicksRemaining() {
        return respawnTicksRemaining;
    }

    /**
     * Advances the displayed position toward the extrapolated target.
     *
     * @param delta frame delta in seconds
     */
    public void advance(float delta) {
        Vec2 extrapolatedTarget = targetPosition.add(velocity.scale(delta));
        displayPosition = displayPosition.lerp(extrapolatedTarget, 0.22f);
    }

    /**
     * Updates authoritative state for this entity.
     *
     * @param targetPosition the latest authoritative position
     * @param velocity the latest authoritative velocity
     * @param currentHealth the current health
     * @param maxHealth the maximum health
     * @param alive whether the entity is alive
     * @param respawnTicksRemaining remaining respawn ticks
     */
    public void syncFromSnapshot(
            Vec2 targetPosition,
            Vec2 velocity,
            int currentHealth,
            int maxHealth,
            boolean alive,
            long respawnTicksRemaining
    ) {
        this.targetPosition = targetPosition;
        this.velocity = velocity;
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
        this.alive = alive;
        this.respawnTicksRemaining = respawnTicksRemaining;
    }

    /**
     * Blends the displayed position toward a predicted local player position.
     *
     * @param predictedPosition the locally predicted player position
     */
    public void reconcileDisplayPosition(Vec2 predictedPosition) {
        displayPosition = displayPosition.lerp(predictedPosition, 0.4f);
    }
}
