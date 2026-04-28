package com.game.client.input;

import com.badlogic.gdx.Gdx;
import com.game.shared.math.Vec2;

/**
 * Reads keyboard state for movement and action input.
 *
 * @since 0.1.0
 */
public final class KeyboardInput {
    private final KeyBindings keyBindings;

    /**
     * Creates a keyboard reader using the given bindings.
     *
     * @param keyBindings the active key bindings
     */
    public KeyboardInput(KeyBindings keyBindings) {
        this.keyBindings = keyBindings;
    }

    /**
     * Reads the current movement direction.
     *
     * @return normalized movement direction
     */
    public Vec2 movementDirection() {
        float x = 0.0f;
        float y = 0.0f;
        if (pressed(keyBindings.moveLeft()) || pressed(keyBindings.altMoveLeft())) {
            x -= 1.0f;
        }
        if (pressed(keyBindings.moveRight()) || pressed(keyBindings.altMoveRight())) {
            x += 1.0f;
        }
        if (pressed(keyBindings.moveUp()) || pressed(keyBindings.altMoveUp())) {
            y += 1.0f;
        }
        if (pressed(keyBindings.moveDown()) || pressed(keyBindings.altMoveDown())) {
            y -= 1.0f;
        }
        Vec2 direction = new Vec2(x, y);
        return direction.lengthSquared() > 1.0f ? direction.normalized() : direction;
    }

    /**
     * Returns whether an attack was requested this frame.
     *
     * @return {@code true} when attack was just pressed
     */
    public boolean attackRequested() {
        return justPressed(keyBindings.attack());
    }

    /**
     * Returns whether a loot pickup was requested this frame.
     *
     * @return {@code true} when pickup was just pressed
     */
    public boolean pickupRequested() {
        return justPressed(keyBindings.pickupLoot());
    }

    /**
     * Returns the inventory slot requested for equip, or {@code -1}.
     *
     * @return the zero-based equip slot index
     */
    public int equipSlotIndex() {
        if (justPressed(keyBindings.equipSlot1())) {
            return 0;
        }
        if (justPressed(keyBindings.equipSlot2())) {
            return 1;
        }
        if (justPressed(keyBindings.equipSlot3())) {
            return 2;
        }
        if (justPressed(keyBindings.equipSlot4())) {
            return 3;
        }
        if (justPressed(keyBindings.equipSlot5())) {
            return 4;
        }
        if (justPressed(keyBindings.equipSlot6())) {
            return 5;
        }
        if (justPressed(keyBindings.equipSlot7())) {
            return 6;
        }
        if (justPressed(keyBindings.equipSlot8())) {
            return 7;
        }
        return -1;
    }

    /**
     * Returns whether a disconnect was requested this frame.
     *
     * @return {@code true} when the disconnect key was just pressed
     */
    public boolean disconnectRequested() {
        return justPressed(keyBindings.disconnect());
    }

    private static boolean pressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }

    private static boolean justPressed(int keycode) {
        return Gdx.input.isKeyJustPressed(keycode);
    }
}
