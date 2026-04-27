package com.game.server.world.map;
/**
 * Immutable description of a single map zone.
 * <p>A zone is the basic unit of world geography. Phase 3 tracks only its
 * identifier, name, and tile dimensions. Tile data, collision meshes, and
 * spawn tables are added in later phases.</p>
 * @param id     the unique zone identifier
 * @param name   the human-readable zone name
 * @param width  the zone width in tiles
 * @param height the zone height in tiles
 * @since 0.1.0
 */
public record Zone(int id, String name,int width,int height) {
    public Zone {
        if (id < 0) {
            throw new IllegalArgumentException("Zone ID cannot be negative");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Zone name cannot be blank");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("zone dimensions must be positive");
        }
    }
    /**
     * Returns the total number of tiles in this zone.
     * @return width multiplied by height
     */
    public int tileCount() {
        return width * height;
    }
}
