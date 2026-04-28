package com.game.server.world.components;

import com.game.shared.math.Vec2;

/**
 * World position component for server-side entities.
 *
 * @param position the current world position
 * @since 0.1.0
 */
public record TransformComponent(Vec2 position) {
}
