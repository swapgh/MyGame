package com.game.server.npc.definition;

import com.game.server.items.definition.DefinitionObjectReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads NPC spawn entries from disk.
 *
 * @since 0.1.0
 */
public final class NpcSpawnEntryLoader {
    public List<NpcSpawnEntry> load(Path path) throws IOException {
        List<NpcSpawnEntry> entries = new ArrayList<>();
        for (String object : DefinitionObjectReader.readObjects(path)) {
            entries.add(new NpcSpawnEntry(
                    DefinitionObjectReader.requireInt(object, "zoneId"),
                    DefinitionObjectReader.requireString(object, "npcId"),
                    DefinitionObjectReader.requireInt(object, "count"),
                    DefinitionObjectReader.requireFloat(object, "spawnX"),
                    DefinitionObjectReader.requireFloat(object, "spawnY"),
                    DefinitionObjectReader.requireFloat(object, "spacing"),
                    DefinitionObjectReader.requireLong(object, "respawnDelayTicks"),
                    DefinitionObjectReader.requireFloat(object, "roamRadius")
            ));
        }
        return List.copyOf(entries);
    }
}
