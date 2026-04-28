package com.game.server.auth.network;

import com.game.server.auth.AuthServerConfig;
import com.game.shared.protocol.Packet;
import com.game.shared.protocol.error.ErrorPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal TCP bootstrap for the authentication server.
 * <p>This phase binds the listening socket and owns the packet router. The connection accept loop
 * and packet decoding are added in later Phase 2 steps.</p>
 * @since 0.1.0
 */
public final class AuthSocketServer implements AutoCloseable {
    private final AuthServerConfig config;
    private final AuthPacketRouter packetRouter;
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    /**
     * Creates a socket server bootstrap for the provided config.
     * @param config the auth server config
     * @param packetRouter the packet router used by future connection handling
     */
    public AuthSocketServer(AuthServerConfig config, AuthPacketRouter packetRouter) {
        this.config = config;
        this.packetRouter = packetRouter;
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
        Thread.ofVirtual().name("auth-accept-loop").start(this::acceptLoop);
    }
    /**
     * Returns whether the listening socket is currently bound and open.
     * @return {@code true} if the auth socket server is running
     */
    public boolean isRunning() {
        return serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed();
    }
    /**
     * Returns the packet router owned by this server bootstrap.
     * @return the auth packet router
     */
    public AuthPacketRouter packetRouter() {
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
            AuthConnection connection = AuthConnection.open(socket);
            try {
                for (String line = connection.readLine(); line != null; line = connection.readLine()) {
                    Packet packet = connection.packetCodec().decode(line);
                    boolean handled = packetRouter.route(connection, packet);
                    if (!handled) {
                        connection.send(new ErrorPacket("UNHANDLED_PACKET", "No handler for " + packet.opcode()));
                    }
                }
            } finally {
                connection.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Closes the underlying server socket if it has been started.
     *
     * @throws IOException if closing the socket fails
     */
    @Override
    public void close() throws IOException {
        clientExecutor.shutdownNow();
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
