package com.game.server.auth.network;

import com.game.shared.protocol.Packet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Routes authentication packets to handlers based on packet type.
 *
 * @since 0.1.0
 */
public final class AuthPacketRouter {
    private final Map<Class<? extends Packet>, AuthPacketHandler> handlers = new HashMap<>();

    /**
     * Registers a handler for the provided packet type.
     *
     * @param packetType the packet class to match
     * @param handler the handler invoked for matching packets
     * @param <T> the packet type
     */
    public <T extends Packet> void register(Class<T> packetType, AuthPacketHandler handler) {
        handlers.put(packetType, handler);
    }

    /**
     * Routes a packet to a registered handler when one exists.
     *
     * @param connection the connection that produced the packet
     * @param packet the packet to route
     * @return {@code true} if a handler was found and invoked
     * @throws IOException if the handler fails during I/O work
     */
    public boolean route(AuthConnection connection, Packet packet) throws IOException {
        AuthPacketHandler handler = handlers.get(packet.getClass());
        if (handler == null) {
            return false;
        }
        handler.handle(connection, packet);
        return true;
    }

    /**
     * Returns the registered handler for a packet type, if present.
     *
     * @param packetType the packet class to look up
     * @return the registered handler, if any
     */
    public Optional<AuthPacketHandler> lookup(Class<? extends Packet> packetType) {
        return Optional.ofNullable(handlers.get(packetType));
    }
}
