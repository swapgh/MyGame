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
