package com.game.server.loot.definition;

import java.util.List;

/**
 * Data-driven loot table definition.
 *
 * @param id unique loot table id
 * @param drops possible item ids dropped by an NPC
 * @since 0.1.0
 */
public record LootTableDefinition(String id, List<String> drops) {
    public LootTableDefinition {
        drops = List.copyOf(drops);
    }
}
