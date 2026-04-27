package com.game.shared.math;

/**
 * Immutable 3D vector shared by client and server code.
 * @param x the horizontal component
 * @param y the vertical component
 * @param z the depth component
 * @since 0.1.0
 */
public record Vec3(float x, float y, float z) {
    public static final Vec3 ZERO = new Vec3(0.0f, 0.0f, 0.0f);
    /**
     * Returns the component-wise sum of this vector and another vector.
     * @param other the vector to add
     * @return a new vector containing the sum
     */
    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }
    /**
     * Returns the component-wise difference between this vector and another vector.
     * @param other the vector to subtract
     * @return a new vector containing the difference
     */
    public Vec3 subtract(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }
    /**
     * Returns a new vector scaled by the provided scalar.
     * @param scalar the factor applied to each component
     * @return a new scaled vector
     */
    public Vec3 scale(float scalar) {
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }
    /**
     * Returns the squared magnitude, useful when avoiding a square root.
     * @return the squared vector length
     */
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }
    /**
     * Returns the Euclidean magnitude of the vector.
     * @return the vector length
     */
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
}
