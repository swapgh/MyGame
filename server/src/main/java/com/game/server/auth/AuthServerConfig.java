package com.game.server.auth;

/**
 * Basic configuration required to boot the authentication server.
 *
 * @param name the logical server name
 * @param host the bind host
 * @param port the bind port
 * @since 0.1.0
 */
public record AuthServerConfig(String name, String host, int port) {
    /**
     * Creates an auth server config and validates core fields.
     */
    public AuthServerConfig {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("host cannot be blank");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 1 and 65535");
        }
    }
}
