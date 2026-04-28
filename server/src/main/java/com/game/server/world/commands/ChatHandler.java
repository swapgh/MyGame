package com.game.server.world.commands;

import com.game.server.world.app.WorldServerMain.WorldApplication;
import com.game.server.world.network.WorldConnection;
import com.game.server.world.network.WorldPacketHandler;
import com.game.shared.protocol.core.Packet;

import java.net.SocketAddress;

/**
 * Handles world chat packets.
 *
 * @since 0.1.0
 */
public final class ChatHandler implements WorldPacketHandler {
    @SuppressWarnings("unused")
    private final WorldApplication application;

    public ChatHandler(WorldApplication application) {
        this.application = application;
    }

    @Override
    public void handle(WorldConnection connection, Packet packet) {
        SocketAddress remote = connection.socket().getRemoteSocketAddress();
        System.out.printf("CHAT_MESSAGE from %s (fields added in Phase 5)%n", remote);
        // Phase 5 will broadcast to nearby players.
    }
}
