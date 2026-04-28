package com.game.client.world.ecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordered scheduler for client systems.
 *
 * @since 0.1.0
 */
public final class ClientSystemScheduler {
    private final List<ClientSystem> systems = new ArrayList<>();

    public void register(ClientSystem system) {
        systems.add(system);
    }

    public void tickAll(ClientWorldContext context, float deltaSeconds) {
        for (ClientSystem system : systems) {
            system.tick(context, deltaSeconds);
        }
    }

    public List<ClientSystem> systems() {
        return Collections.unmodifiableList(systems);
    }
}
