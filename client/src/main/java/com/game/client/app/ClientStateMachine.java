package com.game.client.app;

/**
 * Small mutable holder for the current high-level client state.
 *
 * @since 0.1.0
 */
public final class ClientStateMachine {
    private ClientState currentState = ClientState.LOGIN;

    /**
     * Returns the current client state.
     *
     * @return the current state
     */
    public ClientState currentState() {
        return currentState;
    }

    /**
     * Updates the current client state.
     *
     * @param state the next state
     */
    public void transitionTo(ClientState state) {
        this.currentState = state;
    }
}
