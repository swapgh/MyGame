package com.game.server.world.ecs;

import com.game.server.world.definitions.LootTableDefinition;
import com.game.server.world.definitions.NpcDefinition;
import com.game.server.world.definitions.NpcSpawnEntry;
import com.game.server.world.map.World;
import com.game.server.world.map.ZoneLoader;

import java.util.List;
import java.util.Map;

/**
 * Shared world bootstrap context.
 *
 * @param entityManager the world entity manager
 * @param systemRegistry the world system registry
 * @param zoneLoader the loaded world zones
 * @param world the built world model
 * @param npcDefinitions loaded NPC definitions
 * @param npcSpawnEntries loaded NPC spawn entries
 * @param lootTables loaded loot tables
 * @since 0.1.0
 */
public record WorldContext(
        EntityManager entityManager,
        SystemRegistry systemRegistry,
        ZoneLoader zoneLoader,
        World world,
        Map<String, NpcDefinition> npcDefinitions,
        List<NpcSpawnEntry> npcSpawnEntries,
        Map<String, LootTableDefinition> lootTables
) {
}
