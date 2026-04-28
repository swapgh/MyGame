package com.game.server.components.inventory;

import java.util.List;

/**
 * Player inventory state with a simple ordered list of stacks.
 *
 * @param capacity maximum number of inventory stacks
 * @param items inventory stacks in UI order
 * @since 0.1.0
 */
public record InventoryComponent(int capacity, List<InventoryEntry> items) {
    public InventoryComponent {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }
        items = List.copyOf(items);
        if (items.size() > capacity) {
            throw new IllegalArgumentException("items cannot exceed capacity");
        }
    }
}
