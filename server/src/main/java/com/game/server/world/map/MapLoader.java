package com.game.server.world.map;

/**
 * Builds the in-memory world model from loaded zone definitions.
 *
 * <p>Phase 3 keeps this intentionally simple by converting the loaded starter zones into a
 * {@link World} with generated area grids. Future phases can move file-based map parsing here
 * without changing the world-server bootstrap shape.</p>
 *
 * @since 0.1.0
 */
public final class MapLoader {
    private final int areaCellSize;

    /**
     * Creates a map loader using the provided area cell size.
     *
     * @param areaCellSize the area grid cell size in tiles
     */
    public MapLoader(int areaCellSize) {
        if (areaCellSize <= 0) {
            throw new IllegalArgumentException("areaCellSize must be greater than zero");
        }
        this.areaCellSize = areaCellSize;
    }

    /**
     * Builds a world model from the currently loaded zones.
     *
     * @param zoneLoader the loaded zone registry
     * @return the built world model
     */
    public World loadWorld(ZoneLoader zoneLoader) {
        World world = new World();
        zoneLoader.getZones().forEach(zone -> world.addZone(zone, areaCellSize));
        return world;
    }
}
