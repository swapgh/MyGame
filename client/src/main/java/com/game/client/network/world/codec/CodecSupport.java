package com.game.client.network.world.codec;

import com.game.shared.math.Vec2;

/**
 * Shared helpers for the client-side world protocol codecs.
 *
 * @since 0.1.0
 */
public final class CodecSupport {
    private CodecSupport() {
    }

    public static String encodeVec(Vec2 vec2) {
        return vec2.x() + "," + vec2.y();
    }
}
