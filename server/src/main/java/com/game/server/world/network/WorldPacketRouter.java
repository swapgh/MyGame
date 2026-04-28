package com.game.server.world.network;

import com.game.shared.protocol.core.Packet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Routes incoming world packets to registered handlers by packet class.
 * <p>Mirrors {@code AuthPacketRouter} from the auth server.</p>
 * @since 0.1.0
 */
public final class WorldPacketRouter {
    private final Map<Class<? extends Packet>, WorldPacketHandler> handlers = new HashMap<>();
    /**
     * Registers a handler for the given packet class.
     * @param packetClass the packet class to handle
     * @param handler     the handler to invoke
     */
    public void register(Class<? extends Packet> packetClass, WorldPacketHandler handler) {
        handlers.put(packetClass, handler);
    }
    /**
     * Routes the packet to its registered handler.
     * @param connection the connection that sent the packet
     * @param packet     the decoded packet
     * @return {@code true} if a handler was found and invoked
     * @throws IOException if the handler throws while sending a response
     */
    public boolean route(WorldConnection connection, Packet packet) throws IOException {
        WorldPacketHandler handler = handlers.get(packet.getClass());
        if (handler == null) {
            return false;
        }
        handler.handle(connection, packet);
        return true;
    }
}
