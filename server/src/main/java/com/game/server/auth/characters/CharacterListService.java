package com.game.server.auth.characters;

import com.game.server.auth.database.CharacterDao;
import com.game.server.auth.database.CharacterRecord;

import java.util.List;

/**
 * Returns the characters owned by an account.
 *
 * @since 0.1.0
 */
public final class CharacterListService {
    private final CharacterDao characterDao;

    /**
     * Creates a character list service.
     *
     * @param characterDao the character dao
     */
    public CharacterListService(CharacterDao characterDao) {
        this.characterDao = characterDao;
    }

    /**
     * Returns all characters belonging to an account.
     *
     * @param accountId the owning account id
     * @return the account characters
     */
    public List<CharacterRecord> list(long accountId) {
        return characterDao.findByAccountId(accountId);
    }
}
