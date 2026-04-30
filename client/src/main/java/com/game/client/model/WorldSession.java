package com.game.client.model;

import com.game.shared.ids.SharedEntityId;

/**
 * Active world session for the entered character.
 *
 * @param characterName selected character name
 * @param playerEntityId authoritative player entity id
 * @since 0.1.0
 */
public record WorldSession(String characterName, SharedEntityId playerEntityId) {
}
