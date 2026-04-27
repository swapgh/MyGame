package com.game.server.auth.database;

/**
 * Basic character persistence model owned by an account.
 *
 * @param id the character id
 * @param accountId the owning account id
 * @param name the character name
 * @since 0.1.0
 */
public record CharacterRecord(long id, long accountId, String name) {
}
