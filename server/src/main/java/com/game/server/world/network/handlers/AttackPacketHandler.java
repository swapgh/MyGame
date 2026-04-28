package com.game.server.world.network.handlers;

import com.game.server.world.app.WorldServerMain.WorldApplication;
import com.game.server.world.components.AttackIntentComponent;
import com.game.server.world.components.HealthComponent;
import com.game.server.world.components.RespawnComponent;
import com.game.server.world.ecs.EntityId;
import com.game.server.world.network.WorldConnection;
import com.game.server.world.network.WorldPacketHandler;
import com.game.shared.protocol.core.Packet;
import com.game.shared.protocol.world.AttackPacket;

/**
 * Handles player attack packets.
 *
 * @since 0.1.0
 */
public final class AttackPacketHandler implements WorldPacketHandler {
    private final WorldApplication application;

    public AttackPacketHandler(WorldApplication application) {
        this.application = application;
    }

    @Override
    public void handle(WorldConnection connection, Packet packet) {
        AttackPacket attackPacket = (AttackPacket) packet;
        EntityId entityId = application.connectionManager().findPlayerEntityId(connection.id()).orElse(null);
        if (entityId == null || entityId.value() != attackPacket.attackerEntityId().value()) {
            return;
        }

        HealthComponent health = application.worldContext().entityManager().get(entityId, HealthComponent.class).orElse(null);
        RespawnComponent respawn = application.worldContext().entityManager().get(entityId, RespawnComponent.class).orElse(null);
        if (health == null || respawn == null || !health.alive() || respawn.waitingForRespawn()) {
            return;
        }

        application.worldContext().entityManager().put(
                entityId,
                new AttackIntentComponent(application.gameLoop().clock().tick())
        );
    }
}
