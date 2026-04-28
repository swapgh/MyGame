package com.game.server.world.map;

import com.game.shared.math.Bounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Spatial partition of a zone into fixed-size rectangular areas.
 *
 * @since 0.1.0
 */
public final class AreaGrid {
    private final int cellSize;
    private final List<Area> areas;

    /**
     * Creates an area grid for the provided zone.
     *
     * @param zone the source zone
     * @param cellSize the size of one area cell in tiles
     */
    public AreaGrid(Zone zone, int cellSize) {
        if (cellSize <= 0) {
            throw new IllegalArgumentException("cellSize must be greater than zero");
        }
        this.cellSize = cellSize;
        this.areas = buildAreas(zone, cellSize);
    }

    /**
     * Returns the area cell size in tiles.
     *
     * @return the area cell size
     */
    public int cellSize() {
        return cellSize;
    }

    /**
     * Returns all generated areas for the zone.
     *
     * @return the generated areas
     */
    public List<Area> areas() {
        return Collections.unmodifiableList(areas);
    }

    /**
     * Returns the number of generated areas.
     *
     * @return the area count
     */
    public int count() {
        return areas.size();
    }

    private static List<Area> buildAreas(Zone zone, int cellSize) {
        List<Area> builtAreas = new ArrayList<>();
        int areaRows = (int) Math.ceil((double) zone.height() / cellSize);
        int areaColumns = (int) Math.ceil((double) zone.width() / cellSize);

        for (int areaY = 0; areaY < areaRows; areaY++) {
            for (int areaX = 0; areaX < areaColumns; areaX++) {
                float x = areaX * cellSize;
                float y = areaY * cellSize;
                float width = Math.min(cellSize, zone.width() - x);
                float height = Math.min(cellSize, zone.height() - y);
                builtAreas.add(new Area(zone.id(), areaX, areaY, new Bounds(x, y, width, height)));
            }
        }

        return builtAreas;
    }
}
