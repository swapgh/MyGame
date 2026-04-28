package com.game.server.world.network;

import com.game.shared.protocol.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Wraps a raw TCP socket with line-delimited packet I/O for the world server.
 * <p>Mirrors {@code AuthConnection} from the auth server exactly.</p>
 * @since 0.1.0
 */
public final class WorldConnection implements AutoCloseable {
    private final UUID id;
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final WorldPacketCodec packetCodec;

    private WorldConnection(Socket socket, BufferedReader reader, PrintWriter writer) {
        this.id = UUID.randomUUID();
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
        this.packetCodec = new WorldPacketCodec();
    }
    /**
     * Opens a world connection wrapping the given socket.
     * @param socket the accepted TCP socket
     * @return a fully initialised world connection
     * @throws IOException if the socket streams cannot be opened
     */
    public static WorldConnection open(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        return new WorldConnection(socket, reader, writer);
    }
    /**
     * Returns the unique connection identifier.
     * @return the connection UUID
     */
    public UUID id() {
        return id;
    }
    /**
     * Returns the underlying TCP socket.
     * @return the raw socket
     */
    public Socket socket() {
        return socket;
    }
    /**
     * Returns the packet codec used by this connection.
     * @return the world packet codec
     */
    public WorldPacketCodec packetCodec() {
        return packetCodec;
    }
    /**
     * Reads the next line from the socket, blocking until available.
     * @return the next line, or {@code null} if the connection closed
     * @throws IOException if reading fails
     */
    public String readLine() throws IOException {
        return reader.readLine();
    }
    /**
     * Encodes and sends a packet to the remote peer.
     * @param packet the packet to send
     * @throws IOException if writing fails
     */
    public void send(Packet packet) throws IOException {
        String line = packetCodec.encode(packet);
        writer.println(line);
        if (writer.checkError()) {
            throw new IOException("Failed to send packet to " + socket.getRemoteSocketAddress());
        }
    }
    /**
     * Closes the underlying socket and all associated streams.
     * @throws IOException if closing fails
     */
    @Override
    public void close() throws IOException {
        socket.close();
    }
}
