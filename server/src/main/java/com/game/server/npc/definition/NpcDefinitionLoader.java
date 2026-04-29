package com.game.server.npc.definition;

import com.game.server.items.definition.DefinitionObjectReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads NPC definitions from disk.
 *
 * @since 0.1.0
 */
public final class NpcDefinitionLoader {
    public Map<String, NpcDefinition> load(Path path) throws IOException {
        Map<String, NpcDefinition> definitions = new LinkedHashMap<>();
        for (String object : DefinitionObjectReader.readObjects(path)) {
            NpcDefinition definition = new NpcDefinition(
                    DefinitionObjectReader.requireString(object, "id"),
                    DefinitionObjectReader.requireString(object, "name"),
                    DefinitionObjectReader.requireInt(object, "maxHealth"),
                    DefinitionObjectReader.requireInt(object, "baseDamage"),
                    DefinitionObjectReader.requireFloat(object, "attackRange"),
                    DefinitionObjectReader.requireLong(object, "attackCooldownTicks"),
                    DefinitionObjectReader.requireFloat(object, "moveSpeed"),
                    DefinitionObjectReader.requireFloat(object, "aggroRange"),
                    DefinitionObjectReader.requireString(object, "lootTableId")
            );
            definitions.put(definition.id(), definition);
        }
        return Collections.unmodifiableMap(definitions);
    }
}
