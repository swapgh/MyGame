package com.game.client.controllers.world;

import com.game.client.input.WorldInputFrame;
import com.game.client.model.WorldFrameState;
import com.game.client.service.TargetingService;
import com.game.client.service.WorldActionService;
import com.game.client.service.WorldFeedbackService;
import com.game.client.service.WorldService;
import com.game.client.world.sync.WorldSyncState;
import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.world.InventoryUpdatePacket;

import java.io.IOException;

/**
 * Executes in-world player actions for the active game screen.
 *
 * @since 0.1.0
 */
public final class WorldScreenController {
    private final WorldService worldService;
    private final TargetingService targetingService;
    private final WorldActionService worldActionService;
    private final WorldFeedbackService worldFeedbackService;

    /**
     * Creates a world screen controller backed by the world client.
     *
     * @param worldService the active world service
     * @param targetingService client-side targeting state
     * @param worldActionService action resolution service
     * @param worldFeedbackService short-lived interaction feedback store
     */
    public WorldScreenController(
            WorldService worldService,
            TargetingService targetingService,
            WorldActionService worldActionService,
            WorldFeedbackService worldFeedbackService
    ) {
        this.worldService = worldService;
        this.targetingService = targetingService;
        this.worldActionService = worldActionService;
        this.worldFeedbackService = worldFeedbackService;
    }

    /**
     * Prepares the current frame state for the active world screen.
     *
     * @param playerEntityId the local player entity id
     * @param worldSyncState the current client world sync state
     * @param inputFrame the current input frame
     * @param latestInteractionMessage latest message from the network layer
     * @param latestInteractionMessageVersion latest interaction message version
     * @param inventoryUpdate latest inventory update, if any
     * @param nowMillis current wall-clock time
     * @return the prepared frame state
     */
    public WorldFrameState prepareFrame(
            SharedEntityId playerEntityId,
            WorldSyncState worldSyncState,
            WorldInputFrame inputFrame,
            String latestInteractionMessage,
            long latestInteractionMessageVersion,
            InventoryUpdatePacket inventoryUpdate,
            long nowMillis
    ) {
        targetingService.clearInvalidTarget(worldSyncState.entityStates());
        if (inputFrame.cycleTargetRequested()) {
            targetingService.cycleHostileTarget(playerEntityId, worldSyncState.entityStates());
        }

        com.game.client.model.WorldActionContext actionContext = worldActionService.resolve(
                playerEntityId,
                worldSyncState.entityStates(),
                targetingService.currentTarget()
        );
        if (inputFrame.primaryActionRequested()
                && actionContext.actionType() != com.game.client.model.WorldActionType.INTERACT_VENDOR) {
            targetingService.autoSelectNearestHostile(playerEntityId, worldSyncState.entityStates());
            actionContext = worldActionService.resolve(
                    playerEntityId,
                    worldSyncState.entityStates(),
                    targetingService.currentTarget()
            );
        }

        worldFeedbackService.captureInteractionMessage(
                latestInteractionMessage,
                latestInteractionMessageVersion,
                nowMillis
        );

        return new WorldFrameState(
                targetingService.currentTarget(),
                actionContext,
                worldFeedbackService.currentInteractionMessage(nowMillis),
                inventoryUpdate
        );
    }

    /**
     * Sends the current frame's world actions to the server.
     *
     * @param playerEntityId the local player entity id
     * @param inputFrame the current input frame
     * @param worldSyncState the client sync state used for prediction
     * @param frameState the prepared current frame state
     * @throws IOException if any action send fails
     */
    public void handleInput(
            SharedEntityId playerEntityId,
            WorldInputFrame inputFrame,
            WorldSyncState worldSyncState,
            WorldFrameState frameState
    ) throws IOException {
        worldService.handleInputFrame(
                playerEntityId,
                inputFrame,
                worldSyncState,
                frameState.currentTarget(),
                frameState.actionContext()
        );
    }
}
