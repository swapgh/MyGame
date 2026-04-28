package com.game.server.loot.definition;

import com.game.server.items.definition.DefinitionObjectReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads loot table definitions from disk.
 *
 * @since 0.1.0
 */
public final class LootTableLoader {
    public Map<String, LootTableDefinition> load(Path path) throws IOException {
        Map<String, LootTableDefinition> tables = new LinkedHashMap<>();
        for (String object : DefinitionObjectReader.readObjects(path)) {
            LootTableDefinition table = new LootTableDefinition(
                    DefinitionObjectReader.requireString(object, "id"),
                    DefinitionObjectReader.requireStringArray(object, "drops")
            );
            tables.put(table.id(), table);
        }
        return Collections.unmodifiableMap(tables);
    }
}
