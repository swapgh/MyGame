package com.game.server.auth.characters;

import com.game.server.auth.database.CharacterDao;
import com.game.server.auth.database.CharacterRecord;
import com.game.shared.util.Result;

/**
 * Creates new characters for an account.
 *
 * @since 0.1.0
 */
public final class CharacterCreateService {
    private final CharacterDao characterDao;

    /**
     * Creates a character creation service.
     *
     * @param characterDao the character dao
     */
    public CharacterCreateService(CharacterDao characterDao) {
        this.characterDao = characterDao;
    }

    /**
     * Creates a character when the requested name is valid.
     *
     * @param accountId the owning account id
     * @param characterName the requested character name
     * @return the persisted character or a validation error
     */
    public Result<CharacterRecord, String> create(long accountId, String characterName) {
        if (characterName == null || characterName.isBlank()) {
            return Result.failure("Character name cannot be blank");
        }
        if (characterName.length() < 3) {
            return Result.failure("Character name must be at least 3 characters");
        }

        CharacterRecord character = new CharacterRecord(0L, accountId, characterName);
        return Result.success(characterDao.save(character));
    }
}
