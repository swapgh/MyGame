package com.game.server.world.network;

import com.game.shared.protocol.core.Packet;

import java.io.IOException;

/**
 * Functional interface for handling a single incoming world packet.
 * <p>Mirrors {@code AuthPacketHandler} from the auth server.</p>
 * @since 0.1.0
 */
public interface WorldPacketHandler {
    /**
     * Handles an incoming packet on behalf of the given connection.
     * @param connection the connection that sent the packet
     * @param packet     the decoded packet
     * @throws IOException if sending a response fails
     */
    void handle(WorldConnection connection, Packet packet) throws IOException;
}
