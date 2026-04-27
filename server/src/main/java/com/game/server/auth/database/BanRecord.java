package com.game.server.auth.database;

/**
 * Basic ban persistence model for blocked accounts.
 *
 * @param accountId the banned account id
 * @param reason the ban reason
 * @since 0.1.0
 */
public record BanRecord(long accountId, String reason) {
}
