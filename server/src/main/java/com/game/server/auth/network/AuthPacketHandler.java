package com.game.server.auth.network;

import com.game.shared.protocol.core.Packet;

import java.io.IOException;

/**
 * Handles a shared packet received on an authentication connection.
 * @since 0.1.0
 */
@FunctionalInterface
public interface AuthPacketHandler {
    /**
     * Handles a decoded packet for the provided connection.
     * @param connection the client connection
     * @param packet the decoded packet
     * @throws IOException if packet handling encounters an I/O failure
     */
    void handle(AuthConnection connection, Packet packet) throws IOException;
}
