package com.game.client.network.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Thin line-oriented socket wrapper used by the client flow.
 *
 * @since 0.1.0
 */
public final class ServerConnection implements Closeable {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    /**
     * Wraps a connected socket.
     *
     * @param socket the connected socket
     * @throws IOException if stream setup fails
     */
    public ServerConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    /**
     * Sends a single protocol line.
     *
     * @param line the protocol line
     * @throws IOException if sending fails
     */
    public void sendLine(String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
    }

    /**
     * Reads a single protocol line.
     *
     * @return the incoming line, or {@code null} if the socket closed
     * @throws IOException if reading fails
     */
    public String readLine() throws IOException {
        return reader.readLine();
    }

    /**
     * Returns whether the underlying socket is still open.
     *
     * @return {@code true} when the socket is open
     */
    public boolean isOpen() {
        return !socket.isClosed();
    }

    /**
     * Closes the socket and its streams.
     *
     * @throws IOException if closing fails
     */
    @Override
    public void close() throws IOException {
        socket.close();
    }
}
