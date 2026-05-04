package com.game.server.world.commands;

import com.game.server.ecs.entity.EntityId;
import com.game.server.service.VendorInteractionService;
import com.game.server.world.app.WorldServerMain.WorldApplication;
import com.game.server.world.network.WorldConnection;
import com.game.server.world.network.WorldPacketHandler;
import com.game.shared.ids.SharedEntityId;
import com.game.shared.protocol.core.Packet;
import com.game.shared.protocol.world.InteractPacket;
import com.game.shared.protocol.world.InteractionMessagePacket;

import java.io.IOException;

/**
 * Handles world interaction requests such as vendor interaction.
 *
 * @since 0.1.0
 */
public final class InteractHandler implements WorldPacketHandler {
    private final WorldApplication application;

    public InteractHandler(WorldApplication application) {
        this.application = application;
    }

    @Override
    public void handle(WorldConnection connection, Packet packet) throws IOException {
        InteractPacket interactPacket = (InteractPacket) packet;
        EntityId playerEntityId = application.connectionManager().findPlayerEntityId(connection.id()).orElse(null);
        if (playerEntityId == null || playerEntityId.value() != interactPacket.playerEntityId().value()) {
            return;
        }

        String message = application.vendorInteractionService().interact(
                application.worldContext().entityManager(),
                playerEntityId,
                new EntityId(interactPacket.targetEntityId().value())
        );
        if (message != null) {
            connection.send(new InteractionMessagePacket(message));
        }
    }
}
