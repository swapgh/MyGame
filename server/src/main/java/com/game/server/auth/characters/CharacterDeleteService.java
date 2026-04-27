package com.game.server.auth.characters;

import com.game.server.auth.database.CharacterDao;
import com.game.server.auth.database.CharacterRecord;
import com.game.shared.util.Result;

import java.util.Optional;

/**
 * Deletes characters owned by an account.
 * @since 0.1.0
 */
public final class CharacterDeleteService {
    private final CharacterDao characterDao;

    /**
     * Creates a character deletion service.
     * @param characterDao the character dao
     */
    public CharacterDeleteService(CharacterDao characterDao) {
        this.characterDao = characterDao;
    }

    /**
     * Deletes a character when it belongs to the provided account.
     * @param accountId the owning account id
     * @param characterId the character id to delete
     * @return a success result or a human-readable failure
     */
    public Result<Boolean, String> delete(long accountId, long characterId) {
        Optional<CharacterRecord> existingCharacter = characterDao.findById(characterId);
        if (existingCharacter.isEmpty()) {
            return Result.failure("Character not found");
        }
        if (existingCharacter.get().accountId() != accountId) {
            return Result.failure("Character does not belong to the account");
        }
        return Result.success(characterDao.deleteById(characterId));
    }
}
