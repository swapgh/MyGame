package com.game.server.world.components;

import java.util.List;

/**
 * Loot data attached to an NPC.
 *
 * @param lootTableId the loot table identifier
 * @param drops the possible item ids this NPC may drop
 * @since 0.1.0
 */
public record LootComponent(String lootTableId, List<String> drops) {
    public LootComponent {
        if (lootTableId == null || lootTableId.isBlank()) {
            throw new IllegalArgumentException("lootTableId cannot be blank");
        }
        drops = List.copyOf(drops);
    }
}
