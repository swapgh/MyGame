package com.game.client.network.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket factory for early client-server connections.
 *
 * @since 0.1.0
 */
public final class GameClientSocket {
    private GameClientSocket() {
    }

    /**
     * Connects to a remote TCP endpoint.
     *
     * @param host the remote host
     * @param port the remote port
     * @return a line-based server connection
     * @throws IOException if the connection fails
     */
    public static ServerConnection connect(String host, int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 2_000);
        return new ServerConnection(socket);
    }
}
