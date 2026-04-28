package com.game.client.network.packet;

/**
 * Functional handler for decoded client packets.
 *
 * @param <T> the packet type
 * @since 0.1.0
 */
@FunctionalInterface
public interface ClientPacketHandler<T> {
    /**
     * Handles a decoded packet.
     *
     * @param packet the decoded packet
     */
    void handle(T packet);
}
