package com.game.client.model;

import com.game.shared.protocol.world.InventoryUpdatePacket;

/**
 * Immutable per-frame world UI state prepared for the active game screen.
 *
 * @param currentTarget current selected target, if any
 * @param actionContext resolved primary action context
 * @param interactionMessage latest active interaction message, if any
 * @param inventoryUpdate latest inventory snapshot, if any
 * @since 0.1.0
 */
public record WorldFrameState(
        TargetSelection currentTarget,
        WorldActionContext actionContext,
        String interactionMessage,
        InventoryUpdatePacket inventoryUpdate
) {
}
