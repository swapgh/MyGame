package com.game.server.world.map;

import com.game.shared.math.Bounds;

/**
 * Immutable rectangular area inside a world zone.
 *
 * @param zoneId the owning zone id
 * @param areaX the horizontal area index
 * @param areaY the vertical area index
 * @param bounds the covered zone bounds
 * @since 0.1.0
 */
public record Area(int zoneId, int areaX, int areaY, Bounds bounds) {
}
