package com.game.server.auth.database;

import com.game.server.shared.database.DatabasePool;
import com.game.server.shared.database.TransactionManager;
/**
 * Groups the auth persistence collaborators for early auth server development.
 * @since 0.1.0
 */
public final class AuthDatabase {
    private final DatabasePool databasePool;
    private final TransactionManager transactionManager;
    private final AccountDao accountDao;
    private final CharacterDao characterDao;
    private final BanDao banDao;
    /**
     * Creates an auth database bundle from its collaborators.
     * @param databasePool the database pool placeholder
     * @param transactionManager the transaction manager placeholder
     * @param accountDao the account dao
     * @param characterDao the character dao
     * @param banDao the ban dao
     */
    public AuthDatabase(
            DatabasePool databasePool,
            TransactionManager transactionManager,
            AccountDao accountDao,
            CharacterDao characterDao,
            BanDao banDao
    ) {
        this.databasePool = databasePool;
        this.transactionManager = transactionManager;
        this.accountDao = accountDao;
        this.characterDao = characterDao;
        this.banDao = banDao;
    }
    /**
     * Returns the database pool placeholder.
     * @return the database pool
     */
    public DatabasePool databasePool() {
        return databasePool;
    }
    /**
     * Returns the transaction manager placeholder.
     * @return the transaction manager
     */
    public TransactionManager transactionManager() {
        return transactionManager;
    }
    /**
     * Returns the account dao.
     * @return the account dao
     */
    public AccountDao accountDao() {
        return accountDao;
    }
    /**
     * Returns the character dao.
     * @return the character dao
     */
    public CharacterDao characterDao() {
        return characterDao;
    }
    /**
     * Returns the ban dao.
     * @return the ban dao
     */
    public BanDao banDao() {
        return banDao;
    }
}
