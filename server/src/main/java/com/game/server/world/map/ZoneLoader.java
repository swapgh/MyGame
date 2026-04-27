package com.game.server.world.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Loads and caches {@link Zone} definitions for the world server.
 * <p>Phase 3 hard-codes a single starter zone so the server has something to
 * tick against. Zone definitions will be loaded from JSON/YAML files in a later
 * phase once the map format is finalised.</p>
 * @since 0.1.0
 */
public final class ZoneLoader {
    private final List<Zone> zones = new ArrayList<>();

    /**
     * Loads zone definitions.
     * <p>In Phase 3 this always produces the hard-coded starter zone.
     * Future phases will read from {@code data/world/zones/}.</p>
     */
    public void load(){
        zones.clear();
        zones.add(new Zone(1,"Starter Zone",64,64));
    }
    /**
     * Returns an unmodifiable view of all loaded zones.
     * @return the loaded zones
     */
    public List<Zone> getZones() {
        return Collections.unmodifiableList(zones);
    }
    /**
     * Finds a zone by its identifier.
     * @param id the zone id to search for
     * @return the zone, or empty if not loaded
     */
    public Optional<Zone> findById(int id) {
        return zones.stream().filter(z -> z.id() == id).findFirst();
    }
    /**
     * Returns the number of zones currently loaded.
     * @return the zone count
     */
    public int count() {
        return zones.size();
    }
}
