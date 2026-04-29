package com.game.server.ecs;

import com.game.server.ecs.entity.EntityManager;
import com.game.server.ecs.system.SystemRegistry;
import com.game.server.items.definition.ItemDefinition;
import com.game.server.loot.definition.LootTableDefinition;
import com.game.server.npc.definition.NpcDefinition;
import com.game.server.npc.definition.NpcSpawnEntry;
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
 * @param itemDefinitions loaded item definitions
 * @since 0.1.0
 */
public record WorldContext(
        EntityManager entityManager,
        SystemRegistry systemRegistry,
        ZoneLoader zoneLoader,
        World world,
        Map<String, NpcDefinition> npcDefinitions,
        List<NpcSpawnEntry> npcSpawnEntries,
        Map<String, LootTableDefinition> lootTables,
        Map<String, ItemDefinition> itemDefinitions
) {
}
