package com.game.client.controller.world;

import com.game.client.input.WorldInputFrame;
import com.game.client.network.world.WorldClient;
import com.game.client.sync.WorldSyncState;
import com.game.shared.ecs.SharedEntityId;

import java.io.IOException;

/**
 * Executes in-world player actions for the active game screen.
 *
 * @since 0.1.0
 */
public final class WorldScreenController {
    private final WorldClient worldClient;

    /**
     * Creates a world screen controller backed by the world client.
     *
     * @param worldClient the active world client
     */
    public WorldScreenController(WorldClient worldClient) {
        this.worldClient = worldClient;
    }

    /**
     * Sends the current frame's world actions to the server.
     *
     * @param playerEntityId the local player entity id
     * @param inputFrame the current input frame
     * @param worldSyncState the client sync state used for prediction
     * @throws IOException if any action send fails
     */
    public void handleInput(
            SharedEntityId playerEntityId,
            WorldInputFrame inputFrame,
            WorldSyncState worldSyncState
    ) throws IOException {
        worldClient.sendMoveDirection(playerEntityId, inputFrame.movementDirection());
        worldSyncState.setLastInputDirection(inputFrame.movementDirection());

        if (inputFrame.attackRequested()) {
            worldClient.sendAttack(playerEntityId);
        }
        if (inputFrame.pickupRequested()) {
            worldClient.sendPickupLoot(playerEntityId);
        }
        if (inputFrame.equipSlotIndex() >= 0) {
            worldClient.sendEquipItem(playerEntityId, inputFrame.equipSlotIndex());
        }
    }
}
