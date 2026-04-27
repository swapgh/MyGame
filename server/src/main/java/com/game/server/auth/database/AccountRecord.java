package com.game.server.auth.database;

/**
 * Basic account persistence model for auth storage.
 *
 * @param id the account id
 * @param username the unique account username
 * @param passwordHash the stored password hash
 * @param locked whether the account is locked
 * @since 0.1.0
 */
public record AccountRecord(long id, String username, String passwordHash, boolean locked) {
}
