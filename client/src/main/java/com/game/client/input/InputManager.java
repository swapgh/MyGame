package com.game.client.input;

/**
 * High-level entry point for client input collection.
 *
 * @since 0.1.0
 */
public final class InputManager {
    private final KeyboardInput keyboardInput;
    private final MouseInput mouseInput;

    /**
     * Creates an input manager with default key bindings.
     */
    public InputManager() {
        this(KeyBindings.defaults(), new MouseInput());
    }

    /**
     * Creates an input manager with explicit collaborators.
     *
     * @param keyBindings the active key bindings
     * @param mouseInput the mouse input helper
     */
    public InputManager(KeyBindings keyBindings, MouseInput mouseInput) {
        this.keyboardInput = new KeyboardInput(keyBindings);
        this.mouseInput = mouseInput;
    }

    /**
     * Reads the current world input frame.
     *
     * @return the current world input frame
     */
    public WorldInputFrame readWorldInput() {
        return new WorldInputFrame(
                keyboardInput.movementDirection(),
                keyboardInput.cycleTargetRequested(),
                keyboardInput.primaryActionRequested(),
                keyboardInput.pickupRequested(),
                keyboardInput.equipSlotIndex(),
                keyboardInput.disconnectRequested()
        );
    }

    /**
     * Returns the keyboard input helper.
     *
     * @return the keyboard input helper
     */
    public KeyboardInput keyboard() {
        return keyboardInput;
    }

    /**
     * Returns the mouse input helper.
     *
     * @return the mouse input helper
     */
    public MouseInput mouse() {
        return mouseInput;
    }
}
