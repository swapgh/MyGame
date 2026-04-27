package com.game.server.shared.database;

/**
 * Minimal database pool placeholder for early server development.
 *
 * <p>This class currently provides connection metadata only. Real JDBC datasource or pooling
 * integration is added later in Phase 2 once persistence behavior is implemented.</p>
 *
 * @since 0.1.0
 */
public final class DatabasePool {
    private final DatabaseConfig config;

    /**
     * Creates a database pool placeholder.
     *
     * @param config the database config
     */
    public DatabasePool(DatabaseConfig config) {
        this.config = config;
    }

    /**
     * Returns the database configuration used by this pool.
     *
     * @return the database config
     */
    public DatabaseConfig config() {
        return config;
    }

    /**
     * Builds a JDBC url from the configured database settings.
     *
     * @return the JDBC url for the current config
     */
    public String jdbcUrl() {
        return "jdbc:%s://%s:%d/%s".formatted(
                config.engine(),
                config.host(),
                config.port(),
                config.name()
        );
    }
}
