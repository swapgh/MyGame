package com.game.server.world.components;

import com.game.shared.math.Vec2;

/**
 * Velocity component for server-side movement simulation.
 *
 * @param velocity the current world velocity
 * @since 0.1.0
 */
public record VelocityComponent(Vec2 velocity) {
}
