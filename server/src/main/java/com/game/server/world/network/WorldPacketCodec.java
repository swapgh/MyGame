package com.game.server.world.network;

import com.game.shared.protocol.Packet;
import com.game.shared.protocol.error.ErrorPacket;
import com.game.shared.protocol.world.ChatMessagePacket;
import com.game.shared.protocol.world.EnterWorldPacket;
/**
 * Simple line-based codec for the world server, using the same pipe-delimited
 * {@code OPCODE|field1|field2|...} format as {@code AuthPacketCodec}.
 * @since 0.1.0
 */
public final class WorldPacketCodec {
    /**
     * Decodes a pipe-delimited line into a world packet.
     * @param line the incoming protocol line
     * @return the decoded packet
     */
    public Packet decode(String line) {
        String[] parts = line.split("\\|", -1);
        String opcode = parts[0];
        return switch (opcode) {
            case "ENTER_WORLD"  -> new EnterWorldPacket();
            case "CHAT_MESSAGE" -> new ChatMessagePacket();
            default -> new ErrorPacket("UNKNOWN_OPCODE", "Unsupported opcode: " + opcode);
        };
    }
    /**
     * Encodes a world packet into a pipe-delimited line.
     * @param packet the packet to encode
     * @return the encoded protocol line
     */
    public String encode(Packet packet) {
        if (packet instanceof ErrorPacket error) {
            return String.join("|", "ERROR", error.code(), error.message());
        }
        throw new IllegalArgumentException("Unsupported packet type: " + packet.getClass().getName());
    }
}