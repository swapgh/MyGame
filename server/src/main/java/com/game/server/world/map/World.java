package com.game.server.world.map;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory world model composed of loaded zones and their area grids.
 *
 * @since 0.1.0
 */
public final class World {
    private final Map<Integer, Zone> zonesById = new LinkedHashMap<>();
    private final Map<Integer, AreaGrid> areaGridsByZoneId = new LinkedHashMap<>();

    /**
     * Registers a zone and builds its area grid.
     *
     * @param zone the zone to register
     * @param areaCellSize the area cell size used to build the zone grid
     */
    public void addZone(Zone zone, int areaCellSize) {
        zonesById.put(zone.id(), zone);
        areaGridsByZoneId.put(zone.id(), new AreaGrid(zone, areaCellSize));
    }

    /**
     * Returns all registered zones.
     *
     * @return the registered zones
     */
    public Collection<Zone> zones() {
        return Collections.unmodifiableCollection(zonesById.values());
    }

    /**
     * Finds a zone by id.
     *
     * @param zoneId the zone id
     * @return the zone, if present
     */
    public Optional<Zone> findZone(int zoneId) {
        return Optional.ofNullable(zonesById.get(zoneId));
    }

    /**
     * Finds an area grid by zone id.
     *
     * @param zoneId the zone id
     * @return the area grid, if present
     */
    public Optional<AreaGrid> findAreaGrid(int zoneId) {
        return Optional.ofNullable(areaGridsByZoneId.get(zoneId));
    }

    /**
     * Returns the number of registered zones.
     *
     * @return the zone count
     */
    public int zoneCount() {
        return zonesById.size();
    }
}
