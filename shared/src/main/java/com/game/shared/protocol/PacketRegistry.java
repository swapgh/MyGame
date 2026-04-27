package com.game.shared.protocol;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry that maps packet opcodes to packet types.
 *
 * @since 0.1.0
 */
public final class PacketRegistry {
    private final Map<Opcode, Class<? extends Packet>> packetTypes = new EnumMap<>(Opcode.class);

    /**
     * Registers a packet type for the provided opcode.
     *
     * @param opcode the opcode key
     * @param packetType the packet class associated with the opcode
     */
    public void register(Opcode opcode, Class<? extends Packet> packetType) {
        packetTypes.put(opcode, packetType);
    }

    /**
     * Returns the packet type for the provided opcode if one is registered.
     *
     * @param opcode the opcode to look up
     * @return the registered packet type, if present
     */
    public Optional<Class<? extends Packet>> lookup(Opcode opcode) {
        return Optional.ofNullable(packetTypes.get(opcode));
    }

    /**
     * Returns whether a packet type has been registered for the opcode.
     *
     * @param opcode the opcode to check
     * @return {@code true} if the opcode is registered
     */
    public boolean contains(Opcode opcode) {
        return packetTypes.containsKey(opcode);
    }
}
