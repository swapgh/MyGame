package com.game.shared.math;

/**
 * Immutable 2D vector shared by client and server code.
 * @param x the horizontal component
 * @param y the vertical component
 * @since 0.1.0
 */
public record Vec2(float x, float y) {
    public static final Vec2 ZERO = new Vec2(0.0f, 0.0f);
    /**
     * Returns the component-wise sum of this vector and another vector.
     * @param other the vector to add
     * @return a new vector containing the sum
     */
    public Vec2 add(Vec2 other) {
        return new Vec2(x + other.x, y + other.y);
    }
    /**
     * Returns the component-wise difference between this vector and another vector.
     * @param other the vector to subtract
     * @return a new vector containing the difference
     */
    public Vec2 subtract(Vec2 other) {
        return new Vec2(x - other.x, y - other.y);
    }
    /**
     * Returns a new vector scaled by the provided scalar.
     * @param scalar the factor applied to each component
     * @return a new scaled vector
     */
    public Vec2 scale(float scalar) {
        return new Vec2(x * scalar, y * scalar);
    }
    /**
     * Returns the squared magnitude, useful when avoiding a square root.
     * @return the squared vector length
     */
    public float lengthSquared() {
        return x * x + y * y;
    }
    /**
     * Returns the Euclidean magnitude of the vector.
     * @return the vector length
     */
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    /**
     * Returns a unit-length version of this vector, or {@link #ZERO} when the vector is zero.
     * @return the normalised vector
     */
    public Vec2 normalized() {
        float length = length();
        if (length == 0.0f) {
            return ZERO;
        }
        return scale(1.0f / length);
    }

    /**
     * Returns a vector linearly interpolated toward the target vector.
     * @param target the destination vector
     * @param alpha the interpolation factor in the range {@code [0, 1]}
     * @return the interpolated vector
     */
    public Vec2 lerp(Vec2 target, float alpha) {
        return new Vec2(
                x + (target.x - x) * alpha,
                y + (target.y - y) * alpha
        );
    }
}
