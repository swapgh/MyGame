package com.game.shared.protocol.auth;

import com.game.shared.protocol.Opcode;
import com.game.shared.protocol.Packet;

/**
 * Authentication response packet returned after a login attempt.
 *
 * @param success whether the login succeeded
 * @param message the human-readable login result
 * @param sessionToken the issued session token, or an empty string on failure
 * @param accountId the authenticated account id, or {@code -1} on failure
 * @since 0.1.0
 */
public record LoginResponsePacket(
        boolean success,
        String message,
        String sessionToken,
        long accountId
) implements Packet {
    /**
     * Returns the opcode associated with this packet type.
     *
     * @return the login response opcode
     */
    @Override
    public Opcode opcode() {
        return Opcode.LOGIN_RESPONSE;
    }
}
