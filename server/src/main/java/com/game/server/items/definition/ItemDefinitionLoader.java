package com.game.server.items.definition;

import com.game.shared.protocol.world.EquipmentSlot;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads item definitions from disk.
 *
 * @since 0.1.0
 */
public final class ItemDefinitionLoader {
    public Map<String, ItemDefinition> load(Path path) throws IOException {
        Map<String, ItemDefinition> definitions = new LinkedHashMap<>();
        for (String object : DefinitionObjectReader.readObjects(path)) {
            String encodedSlot = DefinitionObjectReader.requireString(object, "equipmentSlot");
            ItemDefinition definition = new ItemDefinition(
                    DefinitionObjectReader.requireString(object, "id"),
                    DefinitionObjectReader.requireString(object, "name"),
                    encodedSlot.isBlank() ? null : EquipmentSlot.valueOf(encodedSlot),
                    DefinitionObjectReader.requireInt(object, "baseDamageBonus"),
                    DefinitionObjectReader.requireFloat(object, "attackRangeBonus")
            );
            definitions.put(definition.id(), definition);
        }
        return Collections.unmodifiableMap(definitions);
    }
}
