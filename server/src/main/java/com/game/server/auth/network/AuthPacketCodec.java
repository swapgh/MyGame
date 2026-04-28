package com.game.server.auth.network;

import com.game.shared.protocol.Packet;
import com.game.shared.protocol.auth.CharacterCreateRequestPacket;
import com.game.shared.protocol.auth.CharacterCreateResponsePacket;
import com.game.shared.protocol.auth.CharacterListPacket;
import com.game.shared.protocol.auth.LoginRequestPacket;
import com.game.shared.protocol.auth.LoginResponsePacket;
import com.game.shared.protocol.auth.RegisterRequestPacket;
import com.game.shared.protocol.auth.RegisterResponsePacket;
import com.game.shared.protocol.error.ErrorPacket;

/**
 * Simple line-based codec for early auth server packet exchange.
 * <p>The format is intentionally minimal and dependency-free:
 * {@code OPCODE|field1|field2|...}. Escaping is not supported yet, so this codec is suitable only
 * for the current early-phase test flow.</p>
 * @since 0.1.0
 */
public final class AuthPacketCodec {
    /**
     * Decodes a line of input into a shared packet.
     * @param line the incoming protocol line
     * @return the decoded packet
     */
    public Packet decode(String line) {
        String[] parts = line.split("\\|", -1);
        String opcode = parts[0];
        return switch (opcode) {
            case "LOGIN_REQUEST" -> new LoginRequestPacket(require(parts, 1), require(parts, 2));
            case "REGISTER_REQUEST" -> new RegisterRequestPacket(require(parts, 1), require(parts, 2));
            case "CHARACTER_CREATE_REQUEST" -> new CharacterCreateRequestPacket(
                    Long.parseLong(require(parts, 1)),
                    require(parts, 2)
            );
            default -> new ErrorPacket("UNKNOWN_OPCODE", "Unsupported opcode: " + opcode);
        };
    }
    /**
     * Encodes a shared packet into a line of output.
     * @param packet the packet to encode
     * @return the encoded protocol line
     */
    public String encode(Packet packet) {
        if (packet instanceof LoginResponsePacket response) {
            return String.join(
                    "|",
                    "LOGIN_RESPONSE",
                    Boolean.toString(response.success()),
                    response.message(),
                    response.sessionToken(),
                    Long.toString(response.accountId())
            );
        }
        if (packet instanceof RegisterResponsePacket response) {
            return String.join(
                    "|",
                    "REGISTER_RESPONSE",
                    Boolean.toString(response.success()),
                    response.message()
            );
        }
        if (packet instanceof CharacterCreateResponsePacket response) {
            return String.join(
                    "|",
                    "CHARACTER_CREATE_RESPONSE",
                    Boolean.toString(response.success()),
                    response.message(),
                    Long.toString(response.accountId()),
                    response.characterName()
            );
        }
        if (packet instanceof CharacterListPacket response) {
            return String.join(
                    "|",
                    "CHARACTER_LIST",
                    Long.toString(response.accountId()),
                    String.join(",", response.characterNames())
            );
        }
        if (packet instanceof ErrorPacket error) {
            return String.join("|", "ERROR", error.code(), error.message());
        }

        throw new IllegalArgumentException("Unsupported packet type: " + packet.getClass().getName());
    }

    private static String require(String[] parts, int index) {
        if (index >= parts.length) {
            throw new IllegalArgumentException("Missing field at index " + index);
        }
        return parts[index];
    }
}
