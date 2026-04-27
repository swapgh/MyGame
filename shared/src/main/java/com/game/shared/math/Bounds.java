package com.game.shared.math;

/**
 * Immutable 2D axis-aligned bounds shared by client and server code.
 *
 * @param x the left position
 * @param y the top position
 * @param width the bounds width
 * @param height the bounds height
 * @since 0.1.0
 */
public record Bounds(float x, float y, float width, float height) {
    /**
     * Returns whether the provided point lies inside these bounds.
     *
     * @param point the point to test
     * @return {@code true} if the point is inside these bounds
     */
    public boolean contains(Vec2 point) {
        return point.x() >= x
                && point.x() <= x + width
                && point.y() >= y
                && point.y() <= y + height;
    }

    /**
     * Returns whether these bounds overlap another bounds instance.
     *
     * @param other the other bounds to test
     * @return {@code true} if the bounds intersect
     */
    public boolean intersects(Bounds other) {
        return x < other.x + other.width
                && x + width > other.x
                && y < other.y + other.height
                && y + height > other.y;
    }
}
