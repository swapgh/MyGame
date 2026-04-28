package com.game.server.world.components;

/**
 * Immutable inventory stack entry.
 *
 * @param itemId stable item id
 * @param quantity stack quantity
 * @since 0.1.0
 */
public record InventoryEntry(String itemId, int quantity) {
    public InventoryEntry {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId cannot be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }
}
