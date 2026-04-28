package com.game.client.input;

/**
 * Backward-compatible adapter around {@link InputManager}.
 *
 * @since 0.1.0
 */
public final class WorldInputReader {
    private final InputManager inputManager = new InputManager();

    /**
     * Reads the current keyboard state for the world screen.
     *
     * @return the captured input frame
     */
    public WorldInputFrame read() {
        return inputManager.readWorldInput();
    }
}
