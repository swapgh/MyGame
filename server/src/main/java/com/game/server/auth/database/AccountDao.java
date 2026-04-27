package com.game.server.auth.database;

import java.util.Optional;

/**
 * Persistence contract for authentication accounts.
 *
 * @since 0.1.0
 */
public interface AccountDao {
    /**
     * Finds an account by username.
     *
     * @param username the account username
     * @return the matching account, if present
     */
    Optional<AccountRecord> findByUsername(String username);

    /**
     * Finds an account by id.
     *
     * @param accountId the account id
     * @return the matching account, if present
     */
    Optional<AccountRecord> findById(long accountId);

    /**
     * Saves an account record.
     *
     * @param account the account to persist
     * @return the persisted account
     */
    AccountRecord save(AccountRecord account);
}
