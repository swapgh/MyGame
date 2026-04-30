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
 * @param cycleTarget cycle target key
 * @param attack the attack key
 * @param pickupLoot the loot pickup key
 * @param equipSlot1 inventory slot 1 equip key
 * @param equipSlot2 inventory slot 2 equip key
 * @param equipSlot3 inventory slot 3 equip key
 * @param equipSlot4 inventory slot 4 equip key
 * @param equipSlot5 inventory slot 5 equip key
 * @param equipSlot6 inventory slot 6 equip key
 * @param equipSlot7 inventory slot 7 equip key
 * @param equipSlot8 inventory slot 8 equip key
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
        int cycleTarget,
        int attack,
        int pickupLoot,
        int equipSlot1,
        int equipSlot2,
        int equipSlot3,
        int equipSlot4,
        int equipSlot5,
        int equipSlot6,
        int equipSlot7,
        int equipSlot8,
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
                Input.Keys.TAB,
                Input.Keys.E,
                Input.Keys.F,
                Input.Keys.NUM_1,
                Input.Keys.NUM_2,
                Input.Keys.NUM_3,
                Input.Keys.NUM_4,
                Input.Keys.NUM_5,
                Input.Keys.NUM_6,
                Input.Keys.NUM_7,
                Input.Keys.NUM_8,
                Input.Keys.ESCAPE
        );
    }
}
