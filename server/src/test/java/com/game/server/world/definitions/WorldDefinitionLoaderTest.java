package com.game.server.world.definitions;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldDefinitionLoaderTest {
    @Test
    void loadsNpcDefinitionsSpawnEntriesAndLootTables() throws Exception {
        WorldDefinitionLoader loader = new WorldDefinitionLoader();
        Path projectRoot = Path.of("").toAbsolutePath().getParent();

        Map<String, NpcDefinition> npcDefinitions = loader.loadNpcDefinitions(
                projectRoot.resolve(Path.of("data", "npcs", "npc-definitions.json"))
        );
        List<NpcSpawnEntry> spawnEntries = loader.loadNpcSpawnEntries(
                projectRoot.resolve(Path.of("data", "npcs", "spawn-tables.json"))
        );
        Map<String, LootTableDefinition> lootTables = loader.loadLootTables(
                projectRoot.resolve(Path.of("data", "items", "loot-tables.json"))
        );

        assertEquals(2, npcDefinitions.size());
        assertTrue(npcDefinitions.containsKey("training-slime"));
        assertEquals(2, spawnEntries.size());
        assertEquals("training-slime", spawnEntries.get(0).npcId());
        assertEquals(List.of("slime_gel", "cloudy_core"), lootTables.get("slime-basic").drops());
    }
}
