package com.game.server.world.definitions;

import com.game.server.world.definitions.loaders.ItemDefinitionLoader;
import com.game.server.world.definitions.loaders.LootTableLoader;
import com.game.server.world.definitions.loaders.NpcDefinitionLoader;
import com.game.server.world.definitions.loaders.NpcSpawnEntryLoader;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefinitionLoadersTest {
    @Test
    void loadsNpcDefinitionsSpawnEntriesAndLootTables() throws Exception {
        Path projectRoot = Path.of("").toAbsolutePath().getParent();

        Map<String, NpcDefinition> npcDefinitions = new NpcDefinitionLoader().load(
                projectRoot.resolve(Path.of("data", "npcs", "npc-definitions.json"))
        );
        List<NpcSpawnEntry> spawnEntries = new NpcSpawnEntryLoader().load(
                projectRoot.resolve(Path.of("data", "npcs", "spawn-tables.json"))
        );
        Map<String, LootTableDefinition> lootTables = new LootTableLoader().load(
                projectRoot.resolve(Path.of("data", "items", "loot-tables.json"))
        );
        Map<String, ItemDefinition> itemDefinitions = new ItemDefinitionLoader().load(
                projectRoot.resolve(Path.of("data", "items", "item-definitions.json"))
        );

        assertEquals(2, npcDefinitions.size());
        assertTrue(npcDefinitions.containsKey("training-slime"));
        assertEquals(2, spawnEntries.size());
        assertEquals("training-slime", spawnEntries.get(0).npcId());
        assertEquals(List.of("slime_gel", "cloudy_core"), lootTables.get("slime-basic").drops());
        assertEquals(4, itemDefinitions.size());
        assertTrue(itemDefinitions.get("sharp_fang").equippable());
    }
}
