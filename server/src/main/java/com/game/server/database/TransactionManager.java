package com.game.server.database;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Placeholder transaction coordinator for early database integration.
 * @since 0.1.0
 */
public final class TransactionManager {
    /**
     * Executes work inside a placeholder transaction boundary.
     * @param supplier the work to execute
     * @param <T> the result type
     * @return the work result
     */
    public <T> T execute(Supplier<T> supplier) {
        return Objects.requireNonNull(supplier, "supplier").get();
    }
}
