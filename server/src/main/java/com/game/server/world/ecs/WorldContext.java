package com.game.server.world.ecs;

import com.game.server.world.map.World;
import com.game.server.world.map.ZoneLoader;

/**
 * Shared world bootstrap context.
 *
 * @param entityManager the world entity manager
 * @param systemRegistry the world system registry
 * @param zoneLoader the loaded world zones
 * @param world the built world model
 * @since 0.1.0
 */
public record WorldContext(
        EntityManager entityManager,
        SystemRegistry systemRegistry,
        ZoneLoader zoneLoader,
        World world
) {
}
