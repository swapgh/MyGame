package com.game.client.input;

import com.game.shared.math.Vec2;

/**
 * Input snapshot for one world frame.
 *
 * @param movementDirection normalized movement intent
 * @param attackRequested whether an attack was triggered this frame
 * @param pickupRequested whether loot pickup was triggered this frame
 * @param equipSlotIndex zero-based slot index to equip, or {@code -1}
 * @param disconnectRequested whether the user wants to leave the world
 * @since 0.1.0
 */
public record WorldInputFrame(
        Vec2 movementDirection,
        boolean attackRequested,
        boolean pickupRequested,
        int equipSlotIndex,
        boolean disconnectRequested
) {
}
