package com.game.client.input;

import com.game.shared.math.Vec2;

/**
 * Input snapshot for one world frame.
 *
 * @param movementDirection normalized movement intent
 * @param attackRequested whether an attack was triggered this frame
 * @param disconnectRequested whether the user wants to leave the world
 * @since 0.1.0
 */
public record WorldInputFrame(
        Vec2 movementDirection,
        boolean attackRequested,
        boolean disconnectRequested
) {
}
