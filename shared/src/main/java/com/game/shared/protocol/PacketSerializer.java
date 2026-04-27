package com.game.shared.protocol;

/**
 * Serializes and deserializes shared packet payloads.
 *
 * @since 0.1.0
 */
public interface PacketSerializer {
    /**
     * Encodes a packet into a byte payload.
     *
     * @param packet the packet to encode
     * @return the serialized payload
     */
    byte[] serialize(Packet packet);

    /**
     * Decodes a byte payload into a packet type.
     *
     * @param payload the serialized payload
     * @param packetType the expected packet type
     * @param <T> the packet type
     * @return the decoded packet
     */
    <T extends Packet> T deserialize(byte[] payload, Class<T> packetType);
}
