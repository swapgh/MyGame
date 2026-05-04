package com.game.client.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Small helper for submit-style keys shared across screens and widgets.
 *
 * @since 0.1.0
 */
public final class SubmitKey {

    private SubmitKey() {
    }

    /**
     * Returns whether either main Enter or numpad Enter is currently pressed.
     *
     * @return {@code true} when a submit key is pressed
     */
    public static boolean isPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_ENTER);
    }

    /**
     * Returns whether either main Enter or numpad Enter was just pressed.
     *
     * @return {@code true} when a submit key was just pressed
     */
    public static boolean isJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER);
    }

    /**
     * Returns whether the given key code is one of the submit keys.
     *
     * @param keyCode key code to test
     * @return {@code true} when the key code is a submit key
     */
    public static boolean matches(int keyCode) {
        return keyCode == Input.Keys.ENTER || keyCode == Input.Keys.NUMPAD_ENTER;
    }
}
