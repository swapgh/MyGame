package com.game.server.auth.database;

import java.util.Optional;

/**
 * Persistence contract for account bans.
 *
 * @since 0.1.0
 */
public interface BanDao {
    /**
     * Finds a ban record for an account.
     *
     * @param accountId the account id
     * @return the ban record, if present
     */
    Optional<BanRecord> findByAccountId(long accountId);
}
