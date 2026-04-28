package com.game.client.network;

import com.game.client.settings.ClientConfig;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.AttackPacket;
import com.game.shared.protocol.world.EntityMovePacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * World client handling movement, combat, and snapshot flow.
 *
 * @since 0.1.0
 */
public final class WorldClient implements AutoCloseable {
    private final ClientConfig clientConfig;
    private final WorldPacketCodec packetCodec = new WorldPacketCodec();
    private final AtomicReference<WorldSnapshotPacket> latestSnapshot = new AtomicReference<>();
    private final AtomicReference<Vec2> lastSentDirection = new AtomicReference<>(Vec2.ZERO);

    private volatile ServerConnection activeConnection;
    private volatile Thread readerThread;

    /**
     * Creates the world client for the given configuration.
     *
     * @param clientConfig the client network configuration
     */
    public WorldClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /**
     * Connects to the world server, enters the selected character, and stores the first snapshot.
     *
     * @param characterName the selected character name
     * @return the world entry result
     * @throws IOException if the world server cannot be reached
     */
    public WorldEntryResult enterWorld(String characterName) throws IOException {
        close();

        ServerConnection connection = GameClientSocket.connect(
                clientConfig.worldHost(),
                clientConfig.worldPort()
        );
        connection.sendLine("ENTER_WORLD|" + characterName);

        WorldSnapshotPacket snapshot = packetCodec.decodeSnapshot(connection.readLine());
        activeConnection = connection;
        latestSnapshot.set(snapshot);
        lastSentDirection.set(Vec2.ZERO);
        startReaderLoop(connection);
        return new WorldEntryResult(true, "WORLD_SNAPSHOT", snapshot);
    }

    /**
     * Returns the latest authoritative snapshot received from the world server.
     *
     * @return the latest snapshot, if present
     */
    public WorldSnapshotPacket latestSnapshot() {
        return latestSnapshot.get();
    }

    /**
     * Returns whether the world connection is still active.
     *
     * @return {@code true} when the world connection is active
     */
    public boolean isConnected() {
        ServerConnection connection = activeConnection;
        return connection != null && connection.isOpen();
    }

    /**
     * Sends a movement direction when it changed since the previous frame.
     *
     * @param playerEntityId the local player entity id
     * @param direction the desired movement direction
     * @throws IOException if sending fails
     */
    public void sendMoveDirection(SharedEntityId playerEntityId, Vec2 direction) throws IOException {
        ServerConnection connection = activeConnection;
        if (connection == null || !connection.isOpen()) {
            return;
        }

        Vec2 previousDirection = lastSentDirection.get();
        if (previousDirection.x() == direction.x() && previousDirection.y() == direction.y()) {
            return;
        }

        connection.sendLine(packetCodec.encode(new EntityMovePacket(playerEntityId, Vec2.ZERO, direction)));
        lastSentDirection.set(direction);
    }

    /**
     * Sends a one-shot attack request for the local player.
     *
     * @param playerEntityId the local player entity id
     * @throws IOException if sending fails
     */
    public void sendAttack(SharedEntityId playerEntityId) throws IOException {
        ServerConnection connection = activeConnection;
        if (connection == null || !connection.isOpen()) {
            return;
        }

        connection.sendLine(packetCodec.encode(new AttackPacket(playerEntityId)));
    }

    /**
     * Closes the active world connection if one exists.
     */
    @Override
    public void close() {
        latestSnapshot.set(null);
        lastSentDirection.set(Vec2.ZERO);

        ServerConnection connection = activeConnection;
        activeConnection = null;

        Thread currentReaderThread = readerThread;
        readerThread = null;
        if (currentReaderThread != null) {
            currentReaderThread.interrupt();
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (IOException exception) {
                System.err.println("Failed to close world connection: " + exception.getMessage());
            }
        }
    }

    private void startReaderLoop(ServerConnection connection) {
        readerThread = Thread.ofVirtual().name("world-client-reader").start(() -> {
            try {
                for (String line = connection.readLine(); line != null; line = connection.readLine()) {
                    latestSnapshot.set(packetCodec.decodeSnapshot(line));
                }
            } catch (IOException | IllegalArgumentException exception) {
                if (activeConnection != null) {
                    System.err.println("World reader stopped: " + exception.getMessage());
                }
            } finally {
                if (activeConnection == connection) {
                    activeConnection = null;
                }
            }
        });
    }

    /**
     * World entry result for the current world flow.
     *
     * @param success whether world entry succeeded
     * @param message the result message
     * @param snapshot the initial world snapshot
     * @since 0.1.0
     */
    public record WorldEntryResult(boolean success, String message, WorldSnapshotPacket snapshot) {
    }
}
