package com.game.server.auth.database;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for account character data.
 * @since 0.1.0
 */
public interface CharacterDao {
    /**
     * Returns all characters owned by an account.
     * @param accountId the owning account id
     * @return the owned characters
     */
    List<CharacterRecord> findByAccountId(long accountId);
    /**
     * Finds a character by id.
     * @param characterId the character id
     * @return the matching character, if present
     */
    Optional<CharacterRecord> findById(long characterId);
    /**
     * Saves a character record.
     * @param character the character to persist
     * @return the persisted character
     */
    CharacterRecord save(CharacterRecord character);
    /**
     * Deletes a character record by id.
     * @param characterId the character id to delete
     * @return {@code true} if a character was removed
     */
    boolean deleteById(long characterId);
}
