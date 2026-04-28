package com.game.server.world.definitions.loaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared helper for the simple flat JSON definition files used by the project.
 *
 * @since 0.1.0
 */
final class DefinitionObjectReader {
    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);

    private DefinitionObjectReader() {
    }

    static List<String> readObjects(Path path) throws IOException {
        String content = Files.readString(path);
        List<String> objects = new ArrayList<>();
        Matcher matcher = OBJECT_PATTERN.matcher(content);
        while (matcher.find()) {
            objects.add(matcher.group(1));
        }
        return objects;
    }

    static String requireString(String object, String key) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"").matcher(object);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing string key: " + key);
        }
        return matcher.group(1);
    }

    static int requireInt(String object, String key) {
        return Integer.parseInt(requireNumber(object, key));
    }

    static long requireLong(String object, String key) {
        return Long.parseLong(requireNumber(object, key));
    }

    static float requireFloat(String object, String key) {
        return Float.parseFloat(requireNumber(object, key));
    }

    static String requireNumber(String object, String key) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(object);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing numeric key: " + key);
        }
        return matcher.group(1);
    }

    static List<String> requireStringArray(String object, String key) {
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
