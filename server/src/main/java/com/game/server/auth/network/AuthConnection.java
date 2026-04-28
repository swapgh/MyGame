package com.game.server.auth.network;

import com.game.shared.protocol.core.Packet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.UUID;

/**
 * Represents a single client connection to the authentication server.
 * @param id the unique connection identifier
 * @param socket the underlying client socket
 * @param reader the buffered socket reader
 * @param writer the buffered socket writer
 * @param packetCodec the auth packet codec
 * @since 0.1.0
 */
public record AuthConnection(
        UUID id,
        Socket socket,
        BufferedReader reader,
        BufferedWriter writer,
        AuthPacketCodec packetCodec
) {
    /**
     * Creates an auth connection from a client socket.
     * @param socket the accepted client socket
     * @return a new auth connection with a generated identifier
     * @throws IOException if the socket streams cannot be opened
     */
    public static AuthConnection open(Socket socket) throws IOException {
        return new AuthConnection(
                UUID.randomUUID(),
                socket,
                new BufferedReader(new InputStreamReader(socket.getInputStream())),
                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                new AuthPacketCodec()
        );
    }
    /**
     * Reads the next protocol line from the connection.
     * @return the next line or {@code null} when the client disconnects
     * @throws IOException if socket reading fails
     */
    public String readLine() throws IOException {
        return reader.readLine();
    }
    /**
     * Sends a packet over the connection using the auth line codec.
     * @param packet the packet to send
     * @throws IOException if socket writing fails
     */
    public void send(Packet packet) throws IOException {
        writer.write(packetCodec.encode(packet));
        writer.newLine();
        writer.flush();
    }
    /**
     * Closes the underlying client socket.
     * @throws IOException if socket closing fails
     */
    public void close() throws IOException {
        socket.close();
    }
}
