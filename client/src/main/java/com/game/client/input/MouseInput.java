package com.game.client.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Small mouse-state helper for future world and UI interactions.
 *
 * @since 0.1.0
 */
public final class MouseInput {
    /**
     * Returns the current mouse x coordinate in screen space.
     *
     * @return the current mouse x coordinate
     */
    public int x() {
        return Gdx.input.getX();
    }

    /**
     * Returns the current mouse y coordinate in screen space.
     *
     * @return the current mouse y coordinate
     */
    public int y() {
        return Gdx.input.getY();
    }

    /**
     * Returns whether the primary mouse button was just pressed.
     *
     * @return {@code true} when the left mouse button was just pressed
     */
    public boolean primaryJustPressed() {
        return Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    /**
     * Returns whether the secondary mouse button was just pressed.
     *
     * @return {@code true} when the right mouse button was just pressed
     */
    public boolean secondaryJustPressed() {
        return Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
    }
}
