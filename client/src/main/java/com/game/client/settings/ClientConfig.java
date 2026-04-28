package com.game.client.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal desktop client configuration loaded from {@code config/client.yaml}.
 *
 * @param authHost the auth server host
 * @param authPort the auth server port
 * @param worldHost the world server host
 * @param worldPort the world server port
 * @since 0.1.0
 */
public record ClientConfig(
        String authHost,
        int authPort,
        String worldHost,
        int worldPort
) {
    private static final Path DEFAULT_CONFIG_PATH = Path.of("config", "client.yaml");

    /**
     * Loads the default client configuration file.
     *
     * @return the parsed client configuration
     * @throws IOException if the file cannot be read
     */
    public static ClientConfig loadDefault() throws IOException {
        return load(DEFAULT_CONFIG_PATH);
    }

    /**
     * Loads a client configuration from the given path.
     *
     * @param path the configuration file path
     * @return the parsed client configuration
     * @throws IOException if the file cannot be read
     */
    public static ClientConfig load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        Map<String, String> values = new HashMap<>();
        boolean inClientBlock = false;

        for (String rawLine : lines) {
            String line = rawLine.stripTrailing();
            if (line.isBlank() || line.stripLeading().startsWith("#")) {
                continue;
            }
            if ("client:".equals(line.strip())) {
                inClientBlock = true;
                continue;
            }
            if (!inClientBlock || !Character.isWhitespace(rawLine.charAt(0))) {
                continue;
            }

            String stripped = line.strip();
            int separatorIndex = stripped.indexOf(':');
            if (separatorIndex < 0) {
                continue;
            }

            String key = stripped.substring(0, separatorIndex).trim();
            String value = stripped.substring(separatorIndex + 1).trim();
            values.put(key, value);
        }

        return new ClientConfig(
                require(values, "authHost"),
                Integer.parseInt(require(values, "authPort")),
                require(values, "worldHost"),
                Integer.parseInt(require(values, "worldPort"))
        );
    }

    private static String require(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing client config value: " + key);
        }
        return value;
    }
}
