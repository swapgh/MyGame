package com.game.shared.protocol.core;

import com.game.shared.util.Validation;

/**
 * Validates packets before they are accepted by higher-level systems.
 * @since 0.1.0
 */
public interface PacketValidator {
    /**
     * Validates a packet instance.
     * @param packet the packet to validate
     * @return the validation result
     */
    Validation validate(Packet packet);
}
