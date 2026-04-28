package com.game.client.network;

import com.game.client.ClientConfig;

import java.io.IOException;

/**
 * Minimal world client for the Phase 4 world entry flow.
 *
 * @since 0.1.0
 */
public final class WorldClient implements AutoCloseable {
    private final ClientConfig clientConfig;
    private volatile ServerConnection activeConnection;

    /**
     * Creates the world client for the given configuration.
     *
     * @param clientConfig the client network configuration
     */
    public WorldClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /**
     * Connects to the world server and performs the enter-world handshake.
     *
     * @return the world entry result
     * @throws IOException if the world server cannot be reached
     */
    public WorldEntryResult enterWorld() throws IOException {
        close();

        ServerConnection connection = GameClientSocket.connect(
                clientConfig.worldHost(),
                clientConfig.worldPort()
        );
        connection.sendLine("ENTER_WORLD");

        String responseLine = connection.readLine();
        if ("WORLD_SNAPSHOT".equals(responseLine)) {
            activeConnection = connection;
            return new WorldEntryResult(true, "WORLD_SNAPSHOT");
        }

        connection.close();
        return new WorldEntryResult(false, responseLine == null ? "Disconnected" : responseLine);
    }

    /**
     * Closes the active world connection if one exists.
     */
    @Override
    public void close() {
        ServerConnection connection = activeConnection;
        activeConnection = null;
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException exception) {
                System.err.println("Failed to close world connection: " + exception.getMessage());
            }
        }
    }

    /**
     * World entry result for the early client skeleton.
     *
     * @param success whether world entry succeeded
     * @param message the result message
     * @since 0.1.0
     */
    public record WorldEntryResult(boolean success, String message) {
    }
}
