package com.game.server.world.network;

import com.game.server.world.WorldServerMain.WorldApplication;
import com.game.server.world.components.PlayerComponent;
import com.game.server.world.components.TransformComponent;
import com.game.server.world.components.VelocityComponent;
import com.game.server.world.ecs.EntityId;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.ChatMessagePacket;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.EnterWorldPacket;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;

import java.io.IOException;
import java.util.List;
import java.net.SocketAddress;

/**
 * Registers and executes world packet handlers.
 * <p>Mirrors {@code AuthPacketHandlers} from the auth server.</p>
 * @since 0.1.0
 */
public final class WorldPacketHandlers {
    private WorldPacketHandlers() {
    }
    /**
     * Registers the currently supported world packet handlers.
     * @param router      the world packet router
     * @param application the world application collaborators
     */
    public static void register(WorldPacketRouter router, WorldApplication application) {
        router.register(EnterWorldPacket.class, (connection, packet) ->
                handleEnterWorld(connection, application, (EnterWorldPacket) packet));
        router.register(EntityMovePacket.class, (connection, packet) ->
                handleMove(connection, application, (EntityMovePacket) packet));
        router.register(ChatMessagePacket.class, (connection, packet) ->
                handleChat(connection, application));
    }

    private static void handleEnterWorld(
            WorldConnection connection,
            WorldApplication application,
            EnterWorldPacket packet
    ) throws IOException {
        SocketAddress remote = connection.socket().getRemoteSocketAddress();
        EntityId entityId = application.connectionManager().findPlayerEntityId(connection.id())
                .orElseGet(() -> spawnPlayer(connection, application, packet));
        int entityCount = application.worldContext().entityManager().count();
        int zoneCount = application.worldContext().zoneLoader().count();
        int connectionCount = application.connectionManager().count();
        System.out.printf(
                "ENTER_WORLD from %s as %s — %d entities alive, %d zones loaded, %d connection(s), tick=%d%n",
                remote,
                packet.characterName(),
                entityCount,
                zoneCount,
                connectionCount,
                application.gameLoop().clock().tick()
        );
        connection.send(buildSnapshot(application, entityId));
    }

    private static void handleMove(
            WorldConnection connection,
            WorldApplication application,
            EntityMovePacket packet
    ) {
        EntityId entityId = application.connectionManager().findPlayerEntityId(connection.id()).orElse(null);
        if (entityId == null || entityId.value() != packet.entityId().value()) {
            return;
        }

        Vec2 direction = packet.velocity();
        if (direction.lengthSquared() > 1.0f) {
            direction = direction.normalized();
        }
        Vec2 movementVelocity = direction.scale(180.0f);
        application.worldContext().entityManager().put(entityId, new VelocityComponent(movementVelocity));
    }
    private static void handleChat(
            WorldConnection connection,
            WorldApplication application
//          ChatMessagePacket packet
    ) throws IOException {
        SocketAddress remote = connection.socket().getRemoteSocketAddress();
        System.out.printf("CHAT_MESSAGE from %s (fields added in Phase 5)%n", remote);
        // Phase 5 will broadcast to nearby players.
    }

    private static EntityId spawnPlayer(
            WorldConnection connection,
            WorldApplication application,
            EnterWorldPacket packet
    ) {
        EntityId entityId = application.worldContext().entityManager().create();
        float spawnX = 200.0f + (application.connectionManager().count() * 80.0f);
        float spawnY = 200.0f + (application.connectionManager().count() * 40.0f);
        application.worldContext().entityManager().put(entityId, new TransformComponent(new Vec2(spawnX, spawnY)));
        application.worldContext().entityManager().put(entityId, new VelocityComponent(Vec2.ZERO));
        application.worldContext().entityManager().put(entityId, new PlayerComponent(packet.characterName()));
        application.connectionManager().bindPlayerEntity(connection.id(), entityId);
        return entityId;
    }

    private static WorldSnapshotPacket buildSnapshot(WorldApplication application, EntityId playerEntityId) {
        List<EntitySpawnPacket> entities = application.worldContext().entityManager()
                .storeOf(TransformComponent.class)
                .all()
                .stream()
                .map(entry -> new EntitySpawnPacket(
                        new com.game.shared.ecs.SharedEntityId(entry.getKey().value()),
                        entry.getValue().position(),
                        application.worldContext().entityManager()
                                .get(entry.getKey(), VelocityComponent.class)
                                .map(VelocityComponent::velocity)
                                .orElse(Vec2.ZERO)
                ))
                .toList();
        return new WorldSnapshotPacket(
                application.gameLoop().clock().tick(),
                new com.game.shared.ecs.SharedEntityId(playerEntityId.value()),
                entities
        );
    }
}
