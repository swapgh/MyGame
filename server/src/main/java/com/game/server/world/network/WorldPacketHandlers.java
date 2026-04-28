package com.game.server.world.network;

import com.game.server.world.WorldServerMain.WorldApplication;
import com.game.shared.protocol.world.ChatMessagePacket;
import com.game.shared.protocol.world.EnterWorldPacket;
import com.game.shared.protocol.error.ErrorPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;

import java.io.IOException;
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
                handleEnterWorld(connection, application));
        router.register(ChatMessagePacket.class, (connection, packet) ->
                handleChat(connection, application));
    }

    private static void handleEnterWorld(
            WorldConnection connection,
            WorldApplication application
            //EnterWorldPacket packet
    ) throws IOException {
        SocketAddress remote = connection.socket().getRemoteSocketAddress();
        int entityCount = application.worldContext().entityManager().count();
        int zoneCount = application.worldContext().zoneLoader().count();
        int connectionCount = application.connectionManager().count();
        System.out.printf(
                "ENTER_WORLD from %s — %d entities alive, %d zones loaded, %d connection(s), tick=%d%n",
                remote,
                entityCount,
                zoneCount,
                connectionCount,
                application.gameLoop().clock().tick()
        );
        connection.send(new WorldSnapshotPacket());
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
}
