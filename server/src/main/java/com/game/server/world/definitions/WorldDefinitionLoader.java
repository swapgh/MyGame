package com.game.server.world.definitions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal JSON loader for early world definitions.
 *
 * <p>This loader intentionally supports only the flat array-of-objects format used by the
 * current starter definition files so we can stay dependency-free for now.</p>
 *
 * @since 0.1.0
 */
public final class WorldDefinitionLoader {
    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);

    /**
     * Loads NPC definitions from disk.
     *
     * @param path the NPC definition file
     * @return the loaded definitions by id
     * @throws IOException if the file cannot be read
     */
    public Map<String, NpcDefinition> loadNpcDefinitions(Path path) throws IOException {
        Map<String, NpcDefinition> definitions = new LinkedHashMap<>();
        for (String object : readObjects(path)) {
            NpcDefinition definition = new NpcDefinition(
                    requireString(object, "id"),
                    requireString(object, "name"),
                    requireInt(object, "maxHealth"),
                    requireInt(object, "baseDamage"),
                    requireFloat(object, "attackRange"),
                    requireLong(object, "attackCooldownTicks"),
                    requireFloat(object, "moveSpeed"),
                    requireFloat(object, "aggroRange"),
                    requireString(object, "lootTableId")
            );
            definitions.put(definition.id(), definition);
        }
        return Collections.unmodifiableMap(definitions);
    }

    /**
     * Loads NPC spawn entries from disk.
     *
     * @param path the spawn-table file
     * @return the loaded spawn entries
     * @throws IOException if the file cannot be read
     */
    public List<NpcSpawnEntry> loadNpcSpawnEntries(Path path) throws IOException {
        List<NpcSpawnEntry> entries = new ArrayList<>();
        for (String object : readObjects(path)) {
            entries.add(new NpcSpawnEntry(
                    requireInt(object, "zoneId"),
                    requireString(object, "npcId"),
                    requireInt(object, "count"),
                    requireFloat(object, "spawnX"),
                    requireFloat(object, "spawnY"),
                    requireFloat(object, "spacing"),
                    requireLong(object, "respawnDelayTicks"),
                    requireFloat(object, "roamRadius")
            ));
        }
        return List.copyOf(entries);
    }

    /**
     * Loads loot table definitions from disk.
     *
     * @param path the loot-table file
     * @return the loaded loot tables by id
     * @throws IOException if the file cannot be read
     */
    public Map<String, LootTableDefinition> loadLootTables(Path path) throws IOException {
        Map<String, LootTableDefinition> tables = new LinkedHashMap<>();
        for (String object : readObjects(path)) {
            LootTableDefinition table = new LootTableDefinition(
                    requireString(object, "id"),
                    requireStringArray(object, "drops")
            );
            tables.put(table.id(), table);
        }
        return Collections.unmodifiableMap(tables);
    }

    private static List<String> readObjects(Path path) throws IOException {
        String content = Files.readString(path);
        List<String> objects = new ArrayList<>();
        Matcher matcher = OBJECT_PATTERN.matcher(content);
        while (matcher.find()) {
            objects.add(matcher.group(1));
        }
        return objects;
    }

    private static String requireString(String object, String key) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"").matcher(object);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing string key: " + key);
        }
        return matcher.group(1);
    }

    private static int requireInt(String object, String key) {
        return Integer.parseInt(requireNumber(object, key));
    }

    private static long requireLong(String object, String key) {
        return Long.parseLong(requireNumber(object, key));
    }

    private static float requireFloat(String object, String key) {
        return Float.parseFloat(requireNumber(object, key));
    }

    private static String requireNumber(String object, String key) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(object);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing numeric key: " + key);
        }
        return matcher.group(1);
    }

    private static List<String> requireStringArray(String object, String key) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL).matcher(object);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing array key: " + key);
        }
        String payload = matcher.group(1).trim();
        if (payload.isBlank()) {
            return List.of();
        }

        List<String> values = new ArrayList<>();
        for (String part : payload.split(",")) {
            String value = part.trim();
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                values.add(value.substring(1, value.length() - 1));
            }
        }
        return List.copyOf(values);
    }
}
