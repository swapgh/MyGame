package com.game.server.auth.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory character dao used while auth persistence is being scaffolded.
 * @since 0.1.0
 */
public final class InMemoryCharacterDao implements CharacterDao {
    private final AtomicLong sequence = new AtomicLong(1L);
    private final ConcurrentMap<Long, CharacterRecord> charactersById = new ConcurrentHashMap<>();
    /**
     * Returns all characters owned by an account.
     * @param accountId the owning account id
     * @return the owned characters
     */
    @Override
    public List<CharacterRecord> findByAccountId(long accountId) {
        List<CharacterRecord> characters = new ArrayList<>();
        for (CharacterRecord character : charactersById.values()) {
            if (character.accountId() == accountId) {
                characters.add(character);
            }
        }
        return characters;
    }
    /**
     * Finds a character by id.
     * @param characterId the character id
     * @return the matching character, if present
     */
    @Override
    public Optional<CharacterRecord> findById(long characterId) {
        return Optional.ofNullable(charactersById.get(characterId));
    }
    /**
     * Saves a character record.
     * @param character the character to persist
     * @return the persisted character
     */
    @Override
    public CharacterRecord save(CharacterRecord character) {
        long characterId = character.id() > 0L ? character.id() : sequence.getAndIncrement();
        CharacterRecord persisted = new CharacterRecord(characterId, character.accountId(), character.name());
        charactersById.put(characterId, persisted);
        return persisted;
    }
    /**
     * Deletes a character record by id.
     * @param characterId the character id to delete
     * @return {@code true} if a character was removed
     */
    @Override
    public boolean deleteById(long characterId) {
        return charactersById.remove(characterId) != null;
    }
}
