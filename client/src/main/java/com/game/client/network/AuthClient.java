package com.game.client.network;

import com.game.client.ClientConfig;
import com.game.shared.protocol.auth.CharacterCreateResponsePacket;
import com.game.shared.protocol.auth.CharacterListPacket;
import com.game.shared.protocol.auth.LoginResponsePacket;
import com.game.shared.protocol.auth.RegisterResponsePacket;

import java.io.IOException;
import java.util.List;

/**
 * Minimal auth client for the Phase 4 login and character select flow.
 *
 * @since 0.1.0
 */
public final class AuthClient {
    private final ClientConfig clientConfig;

    /**
     * Creates the auth client for the given configuration.
     *
     * @param clientConfig the client network configuration
     */
    public AuthClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /**
     * Performs a login request and loads the follow-up character list.
     *
     * @param username the account username
     * @param password the account password
     * @return the auth flow result
     * @throws IOException if the auth server cannot be reached
     */
    public AuthFlowResult login(String username, String password) throws IOException {
        try (ServerConnection connection = GameClientSocket.connect(
                clientConfig.authHost(),
                clientConfig.authPort()
        )) {
            connection.sendLine("LOGIN_REQUEST|" + username + "|" + password);

            String loginLine = connection.readLine();
            LoginResponsePacket loginResponse = decodeLoginResponse(loginLine);
            if (!loginResponse.success()) {
                return new AuthFlowResult(loginResponse, List.of());
            }

            String characterLine = connection.readLine();
            CharacterListPacket characterList = decodeCharacterList(characterLine);
            return new AuthFlowResult(loginResponse, characterList.characterNames());
        }
    }

    /**
     * Performs a registration request.
     *
     * @param username the desired username
     * @param password the desired password
     * @return the registration result message
     * @throws IOException if the auth server cannot be reached
     */
    public String register(String username, String password) throws IOException {
        try (ServerConnection connection = GameClientSocket.connect(
                clientConfig.authHost(),
                clientConfig.authPort()
        )) {
            connection.sendLine("REGISTER_REQUEST|" + username + "|" + password);
            String responseLine = connection.readLine();
            RegisterResponsePacket response = decodeRegisterResponse(responseLine);
            return response.message();
        }
    }

    /**
     * Creates a new character for the given account and returns the refreshed list.
     *
     * @param accountId the owning account id
     * @param characterName the requested character name
     * @return the creation result plus the refreshed character list
     * @throws IOException if the auth server cannot be reached
     */
    public CharacterCreateFlowResult createCharacter(long accountId, String characterName) throws IOException {
        try (ServerConnection connection = GameClientSocket.connect(
                clientConfig.authHost(),
                clientConfig.authPort()
        )) {
            connection.sendLine("CHARACTER_CREATE_REQUEST|" + accountId + "|" + characterName);

            String createLine = connection.readLine();
            CharacterCreateResponsePacket createResponse = decodeCharacterCreateResponse(createLine);
            if (!createResponse.success()) {
                return new CharacterCreateFlowResult(createResponse, List.of());
            }

            String characterLine = connection.readLine();
            CharacterListPacket characterList = decodeCharacterList(characterLine);
            return new CharacterCreateFlowResult(createResponse, characterList.characterNames());
        }
    }

    private static LoginResponsePacket decodeLoginResponse(String line) {
        String[] parts = requireParts(line, "LOGIN_RESPONSE", 5);
        return new LoginResponsePacket(
                Boolean.parseBoolean(parts[1]),
                parts[2],
                parts[3],
                Long.parseLong(parts[4])
        );
    }

    private static CharacterListPacket decodeCharacterList(String line) {
        String[] parts = requireParts(line, "CHARACTER_LIST", 3);
        List<String> names = parts[2].isBlank() ? List.of() : List.of(parts[2].split(","));
        return new CharacterListPacket(Long.parseLong(parts[1]), names);
    }

    private static RegisterResponsePacket decodeRegisterResponse(String line) {
        String[] parts = requireParts(line, "REGISTER_RESPONSE", 3);
        return new RegisterResponsePacket(Boolean.parseBoolean(parts[1]), parts[2]);
    }

    private static CharacterCreateResponsePacket decodeCharacterCreateResponse(String line) {
        String[] parts = requireParts(line, "CHARACTER_CREATE_RESPONSE", 5);
        return new CharacterCreateResponsePacket(
                Boolean.parseBoolean(parts[1]),
                parts[2],
                Long.parseLong(parts[3]),
                parts[4]
        );
    }

    private static String[] requireParts(String line, String expectedOpcode, int minLength) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Server closed the connection without a response.");
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length < minLength || !expectedOpcode.equals(parts[0])) {
            throw new IllegalArgumentException("Unexpected auth response: " + line);
        }
        return parts;
    }

    /**
     * Auth flow result carrying login state plus character names.
     *
     * @param loginResponse the login result packet
     * @param characterNames the loaded character names
     * @since 0.1.0
     */
    public record AuthFlowResult(LoginResponsePacket loginResponse, List<String> characterNames) {
    }

    /**
     * Character creation flow result carrying the updated character list.
     *
     * @param createResponse the creation result packet
     * @param characterNames the refreshed character names
     * @since 0.1.0
     */
    public record CharacterCreateFlowResult(
            CharacterCreateResponsePacket createResponse,
            List<String> characterNames
    ) {
    }
}
