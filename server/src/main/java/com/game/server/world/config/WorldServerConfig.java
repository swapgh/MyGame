package com.game.server.world.config;

/**
 * Basic configuration required to boot the world server.
 * @param name           the logical server name
 * @param host           the bind host
 * @param port           the bind port
 * @param ticksPerSecond the simulation tick rate
 * @since 0.1.0
 */
public record WorldServerConfig (String name, String host,int port, int ticksPerSecond) {

    public WorldServerConfig {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Server name cannot be null or blank");
        }
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Host cannot be null or blank");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 1 and 65535");
        }
        if (ticksPerSecond <= 0){
            throw new IllegalArgumentException("tickPerSecond must be greater than zero");
        }
    }
}

