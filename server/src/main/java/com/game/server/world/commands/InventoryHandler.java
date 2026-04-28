package com.game.server.world.commands;

import com.game.server.world.app.WorldServerMain.WorldApplication;
import com.game.server.components.combat.HealthComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.ecs.entity.EntityId;
import com.game.server.world.network.WorldConnection;
import com.game.server.world.network.WorldPacketHandler;
import com.game.shared.protocol.core.Packet;
import com.game.shared.protocol.world.EquipItemPacket;
import com.game.shared.protocol.world.InventoryUpdatePacket;
import com.game.shared.protocol.world.PickupLootPacket;

import java.io.IOException;

/**
 * Handles inventory interaction packets.
 *
 * @since 0.1.0
 */
public final class InventoryHandler implements WorldPacketHandler {
    private final WorldApplication application;

    public InventoryHandler(WorldApplication application) {
        this.application = application;
    }

    @Override
    public void handle(WorldConnection connection, Packet packet) throws IOException {
        if (packet instanceof PickupLootPacket pickupLootPacket) {
            handlePickup(connection, pickupLootPacket);
            return;
        }
        if (packet instanceof EquipItemPacket equipItemPacket) {
            handleEquip(connection, equipItemPacket);
        }
    }

    private void handlePickup(WorldConnection connection, PickupLootPacket packet) throws IOException {
        EntityId entityId = application.connectionManager().findPlayerEntityId(connection.id()).orElse(null);
        if (entityId == null || entityId.value() != packet.playerEntityId().value()) {
            return;
        }
        if (!canInteract(entityId)) {
            return;
        }
        if (application.inventoryService().pickupNearbyLoot(application.worldContext().entityManager(), entityId)) {
            sendInventoryUpdate(connection, entityId);
        }
    }

    private void handleEquip(WorldConnection connection, EquipItemPacket packet) throws IOException {
        EntityId entityId = application.connectionManager().findPlayerEntityId(connection.id()).orElse(null);
        if (entityId == null || entityId.value() != packet.playerEntityId().value()) {
            return;
        }
        if (!canInteract(entityId)) {
            return;
        }
        if (application.inventoryService().equipInventorySlot(
                application.worldContext().entityManager(),
                entityId,
                packet.inventorySlotIndex()
        )) {
            sendInventoryUpdate(connection, entityId);
        }
    }

    private boolean canInteract(EntityId entityId) {
        HealthComponent health = application.worldContext().entityManager().get(entityId, HealthComponent.class).orElse(null);
        RespawnComponent respawn = application.worldContext().entityManager().get(entityId, RespawnComponent.class).orElse(null);
        return health != null && respawn != null && health.alive() && !respawn.waitingForRespawn();
    }

    private void sendInventoryUpdate(WorldConnection connection, EntityId entityId) throws IOException {
        InventoryUpdatePacket updatePacket = application.inventoryService()
                .buildInventoryUpdate(application.worldContext().entityManager(), entityId);
        if (updatePacket != null) {
            connection.send(updatePacket);
        }
    }
}
