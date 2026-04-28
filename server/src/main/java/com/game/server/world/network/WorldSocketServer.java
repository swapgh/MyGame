package com.game.server.world.network;

import com.game.server.world.config.WorldServerConfig;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.shared.protocol.core.Packet;
import com.game.shared.protocol.error.ErrorPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * TCP socket server for the world server.
 * <p>Mirrors {@code AuthSocketServer} from the auth server exactly, substituting
 * world-domain types throughout.</p>
 * @since 0.1.0
 */
public final class WorldSocketServer implements AutoCloseable {
    private final WorldServerConfig config;
    private final WorldPacketRouter packetRouter;
    private final WorldConnectionManager connectionManager;
    private final EntityManager entityManager;
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    /**
     * Creates a socket server bootstrap for the provided config.
     * @param config       the world server config
     * @param packetRouter the packet router for connection handling
     */
    public WorldSocketServer(
            WorldServerConfig config,
            WorldPacketRouter packetRouter,
            WorldConnectionManager connectionManager,
            EntityManager entityManager
    ) {
        this.config = config;
        this.packetRouter = packetRouter;
        this.connectionManager = connectionManager;
        this.entityManager = entityManager;
    }
    /**
     * Binds the underlying TCP socket if it is not already running.
     * @throws IOException if the socket cannot be opened or bound
     */
    public void start() throws IOException {
        if (isRunning()) {
            return;
        }
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(config.host(), config.port()));
        Thread.ofVirtual().name("world-accept-loop").start(this::acceptLoop);
    }
    /**
     * Returns whether the listening socket is currently bound and open.
     *
     * @return {@code true} if the world socket server is running
     */
    public boolean isRunning() {
        return serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed();
    }
    /**
     * Returns the packet router owned by this server.
     * @return the world packet router
     */
    public WorldPacketRouter packetRouter() {
        return packetRouter;
    }
    /**
     * Keeps the current thread alive while the server socket remains open.
     * @param pollInterval the delay between socket state checks
     * @throws InterruptedException if the waiting thread is interrupted
     */
    public void awaitShutdown(Duration pollInterval) throws InterruptedException {
        while (isRunning()) {
            Thread.sleep(pollInterval.toMillis());
        }
    }
    private void acceptLoop() {
        while (isRunning()) {
            try {
                Socket socket = serverSocket.accept();
                clientExecutor.submit(() -> handleClient(socket));
            } catch (IOException exception) {
                if (isRunning()) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void handleClient(Socket socket) {
        try {
            WorldConnection connection = WorldConnection.open(socket);
            connectionManager.register(connection);
            try {
                for (String line = connection.readLine(); line != null; line = connection.readLine()) {
                    Packet packet = connection.packetCodec().decode(line);
                    boolean handled = packetRouter.route(connection, packet);
                    if (!handled) {
                        connection.send(new ErrorPacket("UNHANDLED_PACKET", "No handler for " + packet.opcode()));
                    }
                }
            } finally {
                EntityId playerEntityId = connectionManager.releasePlayerEntityId(connection.id()).orElse(null);
                if (playerEntityId != null && entityManager.isAlive(playerEntityId)) {
                    entityManager.destroy(playerEntityId);
                }
                connectionManager.unregister(connection.id());
                connection.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    /**
     * Closes the underlying server socket.
     * @throws IOException if closing fails
     */
    @Override
    public void close() throws IOException {
        clientExecutor.shutdownNow();
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
