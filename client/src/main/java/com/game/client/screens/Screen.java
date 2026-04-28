package com.game.client.screens;

/**
 * Local screen abstraction mirroring LibGDX screen lifecycle defaults.
 *
 * @since 0.1.0
 */
public interface Screen extends com.badlogic.gdx.Screen {
    /**
     * Called when the screen becomes active.
     */
    @Override
    default void show() {
    }

    /**
     * Called when the window is resized.
     *
     * @param width the new width
     * @param height the new height
     */
    @Override
    default void resize(int width, int height) {
    }

    /**
     * Called when the game is paused.
     */
    @Override
    default void pause() {
    }

    /**
     * Called when the game resumes.
     */
    @Override
    default void resume() {
    }

    /**
     * Called when the screen is hidden.
     */
    @Override
    default void hide() {
    }

    /**
     * Releases owned screen resources.
     */
    @Override
    default void dispose() {
    }
}
