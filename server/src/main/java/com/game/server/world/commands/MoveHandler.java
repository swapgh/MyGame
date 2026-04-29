package com.game.server.world.commands;

import com.game.server.world.app.WorldServerMain.WorldApplication;
import com.game.server.components.combat.HealthComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.VelocityComponent;
import com.game.server.ecs.entity.EntityId;
import com.game.server.world.network.WorldConnection;
import com.game.server.world.network.WorldPacketHandler;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.core.Packet;
import com.game.shared.protocol.world.EntityMovePacket;

/**
 * Handles player movement packets.
 *
 * @since 0.1.0
 */
public final class MoveHandler implements WorldPacketHandler {
    private final WorldApplication application;

    public MoveHandler(WorldApplication application) {
        this.application = application;
    }

    @Override
    public void handle(WorldConnection connection, Packet packet) {
        EntityMovePacket movePacket = (EntityMovePacket) packet;
        EntityId entityId = application.connectionManager().findPlayerEntityId(connection.id()).orElse(null);
        if (entityId == null || entityId.value() != movePacket.entityId().value()) {
            return;
        }

        HealthComponent health = application.worldContext().entityManager().get(entityId, HealthComponent.class).orElse(null);
        RespawnComponent respawn = application.worldContext().entityManager().get(entityId, RespawnComponent.class).orElse(null);
        if (health == null || respawn == null || !health.alive() || respawn.waitingForRespawn()) {
            application.worldContext().entityManager().put(entityId, new VelocityComponent(Vec2.ZERO));
            return;
        }

        Vec2 direction = movePacket.velocity();
        if (direction.lengthSquared() > 1.0f) {
            direction = direction.normalized();
        }
        application.worldContext().entityManager().put(entityId, new VelocityComponent(direction.scale(180.0f)));
    }
}
