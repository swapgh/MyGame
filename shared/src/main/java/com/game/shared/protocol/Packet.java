package com.game.shared.protocol;

/**
 * Base contract for every packet exchanged between client and server.
 * @since 0.1.0
 */
public interface Packet {
    /**
     * Returns the stable opcode used to route and decode this packet.
     * @return the opcode associated with this packet
     */
    Opcode opcode();
}
