package com.game.client.network.packet;

import java.util.HashMap;
import java.util.Map;

/**
 * Small packet router for the early client skeleton.
 *
 * @since 0.1.0
 */
public final class ClientPacketRouter {
    private final Map<Class<?>, ClientPacketHandler<?>> handlers = new HashMap<>();

    /**
     * Registers a packet handler for a type.
     *
     * @param packetType the packet class
     * @param handler the handler
     * @param <T> the packet type
     */
    public <T> void register(Class<T> packetType, ClientPacketHandler<T> handler) {
        handlers.put(packetType, handler);
    }

    /**
     * Routes a decoded packet if a handler exists.
     *
     * @param packet the decoded packet
     */
    @SuppressWarnings("unchecked")
    public void route(Object packet) {
        ClientPacketHandler<Object> handler = (ClientPacketHandler<Object>) handlers.get(packet.getClass());
        if (handler != null) {
            handler.handle(packet);
        }
    }
}
