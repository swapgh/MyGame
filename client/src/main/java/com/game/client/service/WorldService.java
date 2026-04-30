package com.game.client.service;

import com.game.client.input.WorldInputFrame;
import com.game.client.model.TargetSelection;
import com.game.client.model.WorldActionContext;
import com.game.client.model.WorldActionType;
import com.game.client.model.WorldSession;
import com.game.client.network.world.WorldClient;
import com.game.client.world.sync.WorldSyncState;
import com.game.shared.ids.SharedEntityId;

import java.io.IOException;

/**
 * Coordinates client world entry and active world session state.
 *
 * @since 0.1.0
 */
public final class WorldService {
    private final WorldClient worldClient;
    private final ClientSessionService clientSessionService;

    /**
     * Creates a world service backed by the world client.
     *
     * @param worldClient world transport client
     * @param clientSessionService shared session state
     */
    public WorldService(WorldClient worldClient, ClientSessionService clientSessionService) {
        this.worldClient = worldClient;
        this.clientSessionService = clientSessionService;
    }

    /**
     * Enters the world with the selected character and stores the active world session.
     *
     * @param characterName selected character name
     * @return the world entry result
     * @throws IOException if the world server cannot be reached
     */
    public WorldClient.WorldEntryResult enterWorld(String characterName) throws IOException {
        WorldClient.WorldEntryResult result = worldClient.enterWorld(characterName);
        if (result.success()) {
            clientSessionService.storeWorldSession(new WorldSession(
                    characterName,
                    result.snapshot().playerEntityId()
            ));
        }
        return result;
    }

    /**
     * Returns the active world session.
     *
     * @return the active world session
     */
    public WorldSession requireWorldSession() {
        WorldSession worldSession = clientSessionService.worldSession();
        if (worldSession == null) {
            throw new IllegalStateException("No active world session is available.");
        }
        return worldSession;
    }

    /**
     * Sends the current world input frame through the active world connection.
     *
     * @param playerEntityId authoritative local player entity id
     * @param inputFrame current input frame
     * @param worldSyncState client sync state used for local prediction
     * @param currentTarget currently selected target, if any
     * @param actionContext resolved current action context
     * @throws IOException if any action send fails
     */
    public void handleInputFrame(
            SharedEntityId playerEntityId,
            WorldInputFrame inputFrame,
            WorldSyncState worldSyncState,
            TargetSelection currentTarget,
            WorldActionContext actionContext
    ) throws IOException {
        worldClient.sendMoveDirection(playerEntityId, inputFrame.movementDirection());
        worldSyncState.setLastInputDirection(inputFrame.movementDirection());

        if (inputFrame.primaryActionRequested()) {
            if (actionContext.actionType() == WorldActionType.INTERACT_VENDOR && actionContext.targetEntityId() >= 0L) {
                worldClient.sendInteract(playerEntityId, new SharedEntityId(actionContext.targetEntityId()));
            } else if (actionContext.actionType() == WorldActionType.ATTACK_TARGET) {
                worldClient.sendAttack(
                        playerEntityId,
                        currentTarget == null ? null : new SharedEntityId(currentTarget.entityId())
                );
            }
        }
        if (inputFrame.pickupRequested()) {
            worldClient.sendPickupLoot(playerEntityId);
        }
        if (inputFrame.equipSlotIndex() >= 0) {
            worldClient.sendEquipItem(playerEntityId, inputFrame.equipSlotIndex());
        }
    }

    /**
     * Leaves the current world session and closes the active connection.
     */
    public void leaveWorld() {
        worldClient.close();
    }
}
