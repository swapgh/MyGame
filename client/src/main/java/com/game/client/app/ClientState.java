package com.game.client.app;

/**
 * High-level client flow states used by the client screens.
 *
 * @since 0.1.0
 */
public enum ClientState {
    LOGIN,
    REGISTER,
    CHARACTER_SELECT,
    CHARACTER_CREATE,
    LOADING_WORLD,
    IN_WORLD,
    SETTINGS,
    ERROR
}
