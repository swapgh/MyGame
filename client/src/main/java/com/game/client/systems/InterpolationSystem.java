package com.game.client.systems;

import com.game.client.world.WorldViewModel;

/**
 * Advances interpolated client render state.
 *
 * @since 0.1.0
 */
public final class InterpolationSystem {
    /**
     * Advances the client world view one frame.
     *
     * @param viewModel the client world view
     * @param deltaSeconds frame delta in seconds
     */
    public void advance(WorldViewModel viewModel, float deltaSeconds) {
        viewModel.advance(deltaSeconds);
    }
}
