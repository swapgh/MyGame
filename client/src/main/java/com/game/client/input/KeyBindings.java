package com.game.client.input;

import com.badlogic.gdx.Input;

/**
 * Keyboard bindings used by the client input layer.
 *
 * @param moveLeft the move-left key
 * @param moveRight the move-right key
 * @param moveUp the move-up key
 * @param moveDown the move-down key
 * @param altMoveLeft alternate move-left key
 * @param altMoveRight alternate move-right key
 * @param altMoveUp alternate move-up key
 * @param altMoveDown alternate move-down key
 * @param attack the attack key
 * @param disconnect the disconnect key
 * @since 0.1.0
 */
public record KeyBindings(
        int moveLeft,
        int moveRight,
        int moveUp,
        int moveDown,
        int altMoveLeft,
        int altMoveRight,
        int altMoveUp,
        int altMoveDown,
        int attack,
        int disconnect
) {
    /**
     * Returns the current default bindings.
     *
     * @return the default key bindings
     */
    public static KeyBindings defaults() {
        return new KeyBindings(
                Input.Keys.A,
                Input.Keys.D,
                Input.Keys.W,
                Input.Keys.S,
                Input.Keys.LEFT,
                Input.Keys.RIGHT,
                Input.Keys.UP,
                Input.Keys.DOWN,
                Input.Keys.SPACE,
                Input.Keys.ESCAPE
        );
    }
}
