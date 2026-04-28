package com.game.server.database;

/**
 * Basic database connection settings used by server-side persistence code.
 * @param engine the database engine name
 * @param host the database host
 * @param port the database port
 * @param name the database name
 * @param schema the default schema
 * @param username the database username
 * @param password the database password
 * @since 0.1.0
 */
public record DatabaseConfig(
        String engine,
        String host,
        int port,
        String name,
        String schema,
        String username,
        String password
) {
    /**
     * Creates a database config and validates core fields.
     */
    public DatabaseConfig {
        if (engine == null || engine.isBlank()) {
            throw new IllegalArgumentException("engine cannot be blank");
        }
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("host cannot be blank");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 1 and 65535");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (schema == null || schema.isBlank()) {
            throw new IllegalArgumentException("schema cannot be blank");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username cannot be blank");
        }
        if (password == null) {
            throw new IllegalArgumentException("password cannot be null");
        }
    }
}
