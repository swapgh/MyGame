package com.game.shared.protocol;

/**
 * Enumerates the message types currently planned for the shared protocol.
 *
 * @since 0.1.0
 */
public enum Opcode {
    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    REGISTER_REQUEST,
    REGISTER_RESPONSE,
    CHARACTER_LIST,
    ENTER_WORLD,
    WORLD_SNAPSHOT,
    ENTITY_SPAWN,
    ENTITY_DESPAWN,
    ENTITY_MOVE,
    CHAT_MESSAGE,
    ATTACK,
    DAMAGE,
    INVENTORY_UPDATE,
    ERROR
}
