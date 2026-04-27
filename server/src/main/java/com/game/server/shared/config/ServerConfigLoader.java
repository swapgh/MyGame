package com.game.server.shared.config;

import com.game.server.auth.AuthServerConfig;
import com.game.server.shared.database.DatabaseConfig;
import com.game.server.world.WorldServerConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal configuration loader for the simple YAML-style config files used in early phases.
 * <p>This loader intentionally supports only the current flat key-value shape under a single
 * {@code server:} section, which keeps Phase 2 dependency-free until a richer config stack is
 * justified.</p>
 * @since 0.1.0
 */
public final class ServerConfigLoader {
    private ServerConfigLoader() {
    }
    /**
     * Loads an authentication server config from disk.
     * @param path the config file path
     * @return the parsed auth server config
     * @throws IOException if the file cannot be read
     */
    public static AuthServerConfig loadAuthServerConfig(Path path) throws IOException {
        Map<String, String> values = readServerSection(path);
        return new AuthServerConfig(
                require(values, "name"),
                require(values, "host"),
                Integer.parseInt(require(values, "port"))
        );
    }
    /**
     * Loads a {@link WorldServerConfig} from the YAML file at the given path.
     * @param path the path to the world server YAML config
     * @return the parsed world server config
     * @throws IOException if the file cannot be read or is malformed
     */
    public static WorldServerConfig loadWorldServerConfig(Path path) throws IOException {
        Map<String, String> values = readServerSection(path);
        return new WorldServerConfig(
                require(values, "name"),
                require(values, "host"),
                Integer.parseInt(require(values, "port")),
                Integer.parseInt(require(values, "ticksPerSecond"))
        );
    }
    /**
     * Loads a database config from disk.
     * @param path the config file path
     * @return the parsed database config
     * @throws IOException if the file cannot be read
     */
    public static DatabaseConfig loadDatabaseConfig(Path path) throws IOException {
        Map<String, String> values = readSection(path, "database:");
        return new DatabaseConfig(
                require(values, "engine"),
                require(values, "host"),
                Integer.parseInt(require(values, "port")),
                require(values, "name"),
                require(values, "schema"),
                require(values, "username"),
                require(values, "password")
        );
    }

    private static Map<String, String> readServerSection(Path path) throws IOException {
        return readSection(path, "server:");
    }

    private static Map<String, String> readSection(Path path, String sectionName) throws IOException {
        List<String> lines = Files.readAllLines(path);
        Map<String, String> values = new HashMap<>();
        boolean inSection = false;

        for (String rawLine : lines) {
            String trimmed = rawLine.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            if (!rawLine.startsWith(" ") && trimmed.endsWith(":")) {
                inSection = sectionName.equals(trimmed);
                continue;
            }
            if (!inSection) {
                continue;
            }
            int separator = trimmed.indexOf(':');
            if (separator < 0) {
                continue;
            }
            String key = trimmed.substring(0, separator).trim();
            String value = trimmed.substring(separator + 1).trim();
            values.put(key, value);
        }

        return values;
    }

    private static String require(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing config value: server." + key);
        }
        return value;
    }
}
