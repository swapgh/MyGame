package com.game.server.world.components;

/**
 * Tracks an entity's current and maximum health.
 *
 * @param currentHealth the entity's current health
 * @param maxHealth the entity's maximum health
 * @since 0.1.0
 */
public record HealthComponent(int currentHealth, int maxHealth) {
    public HealthComponent {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("maxHealth must be greater than zero");
        }
        if (currentHealth < 0 || currentHealth > maxHealth) {
            throw new IllegalArgumentException("currentHealth must be between 0 and maxHealth");
        }
    }

    /**
     * Returns whether the entity is alive.
     *
     * @return {@code true} when health is above zero
     */
    public boolean alive() {
        return currentHealth > 0;
    }
}
