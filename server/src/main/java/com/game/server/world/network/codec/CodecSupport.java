package com.game.server.world.network.codec;

import com.game.shared.math.Vec2;

/**
 * Shared helpers for the line-based world protocol codecs.
 *
 * @since 0.1.0
 */
public final class CodecSupport {
    private CodecSupport() {
    }

    public static String require(String[] parts, int index) {
        if (index >= parts.length) {
            throw new IllegalArgumentException("Missing field at index " + index);
        }
        return parts[index];
    }

    public static String encodeVec(Vec2 vec2) {
        return vec2.x() + "," + vec2.y();
    }

    public static Vec2 decodeVec(String encoded) {
        String[] parts = encoded.split(",", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Malformed vector: " + encoded);
        }
        return new Vec2(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
    }

    public static String sanitize(String value) {
        return value.replace(",", " ").replace(";", " ").replace("|", " ");
    }
}
