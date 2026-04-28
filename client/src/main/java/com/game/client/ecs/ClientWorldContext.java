package com.game.client.ecs;

import com.game.client.input.InputManager;

/**
 * Shared client-side ECS context.
 *
 * @param entityManager the client entity manager
 * @param systemScheduler the client system scheduler
 * @param inputManager the client input manager
 * @since 0.1.0
 */
public record ClientWorldContext(
        ClientEntityManager entityManager,
        ClientSystemScheduler systemScheduler,
        InputManager inputManager
) {
}
