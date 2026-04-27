package com.game.server.auth.database;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory ban dao used while auth persistence is being scaffolded.
 *
 * @since 0.1.0
 */
public final class InMemoryBanDao implements BanDao {
    private final ConcurrentMap<Long, BanRecord> bansByAccountId = new ConcurrentHashMap<>();

    /**
     * Finds a ban record for an account.
     *
     * @param accountId the account id
     * @return the ban record, if present
     */
    @Override
    public Optional<BanRecord> findByAccountId(long accountId) {
        return Optional.ofNullable(bansByAccountId.get(accountId));
    }

    /**
     * Saves or replaces a ban record.
     *
     * @param banRecord the ban record to persist
     */
    public void save(BanRecord banRecord) {
        bansByAccountId.put(banRecord.accountId(), banRecord);
    }
}
