package com.game.server.auth.database;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory account dao used while auth persistence is being scaffolded.
 * @since 0.1.0
 */
public final class InMemoryAccountDao implements AccountDao {
    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, AccountRecord> accountsById = new ConcurrentHashMap<>();
    private final Map<String, Long> accountIdsByUsername = new ConcurrentHashMap<>();

    /**
     * Finds an account by username.
     * @param username the account username
     * @return the matching account, if present
     */
    @Override
    public Optional<AccountRecord> findByUsername(String username) {
        Long accountId = accountIdsByUsername.get(username);
        if (accountId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(accountsById.get(accountId));
    }
    /**
     * Finds an account by id.
     * @param accountId the account id
     * @return the matching account, if present
     */
    @Override
    public Optional<AccountRecord> findById(long accountId) {
        return Optional.ofNullable(accountsById.get(accountId));
    }
    /**
     * Saves an account record.
     * @param account the account to persist
     * @return the persisted account
     */
    @Override
    public AccountRecord save(AccountRecord account) {
        long accountId = account.id() > 0L ? account.id() : sequence.getAndIncrement();
        AccountRecord persisted = new AccountRecord(
                accountId,
                account.username(),
                account.passwordHash(),
                account.locked()
        );
        accountsById.put(accountId, persisted);
        accountIdsByUsername.put(persisted.username(), accountId);
        return persisted;
    }
}
