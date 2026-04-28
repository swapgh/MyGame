package com.game.server.auth.network;

import com.game.server.auth.AuthServerMain.AuthApplication;
import com.game.server.auth.database.CharacterRecord;
import com.game.server.auth.characters.CharacterListService;
import com.game.server.auth.login.LoginResult;
import com.game.server.auth.sessions.AuthSession;
import com.game.shared.protocol.Packet;
import com.game.shared.protocol.auth.CharacterCreateRequestPacket;
import com.game.shared.protocol.auth.CharacterCreateResponsePacket;
import com.game.shared.protocol.auth.CharacterListPacket;
import com.game.shared.protocol.auth.LoginRequestPacket;
import com.game.shared.protocol.auth.LoginResponsePacket;
import com.game.shared.protocol.auth.RegisterRequestPacket;
import com.game.shared.protocol.auth.RegisterResponsePacket;
import com.game.shared.util.Result;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
/**
 * Registers and executes authentication packet handlers for the current auth server bootstrap.
 * @since 0.1.0
 */
public final class AuthPacketHandlers {
    private AuthPacketHandlers() {
    }
    /**
     * Registers the currently supported auth packet handlers.
     * @param router the auth packet router
     * @param application the auth application collaborators
     */
    public static void register(AuthPacketRouter router, AuthApplication application) {
        router.register(LoginRequestPacket.class, (connection, packet) ->
                handleLogin(connection, application, (LoginRequestPacket) packet));
        router.register(RegisterRequestPacket.class, (connection, packet) ->
                handleRegister(connection, application, (RegisterRequestPacket) packet));
        router.register(CharacterCreateRequestPacket.class, (connection, packet) ->
                handleCharacterCreate(connection, application, (CharacterCreateRequestPacket) packet));
        router.register(CharacterListPacket.class, (connection, packet) ->
                logCharacterListRequest(connection, application, packet));
    }

    private static void handleLogin(
            AuthConnection connection,
            AuthApplication application,
            LoginRequestPacket packet
    ) throws IOException {
        Result<AuthSession, LoginResult> loginResult = application.loginService()
                .login(remoteKey(connection), packet.username(), packet.password());

        if (loginResult.isFailure()) {
            connection.send(new LoginResponsePacket(false, loginResult.error().name(), "", -1L));
            return;
        }

        AuthSession session = loginResult.value();
        connection.send(new LoginResponsePacket(true, "LOGIN_SUCCESS", session.token(), session.accountId()));
        sendCharacterList(connection, application.characterListService(), session.accountId());
    }

    private static void handleRegister(
            AuthConnection connection,
            AuthApplication application,
            RegisterRequestPacket packet
    ) throws IOException {
        Result<?, String> registrationResult = application.registrationService()
                .register(packet.username(), packet.password());

        if (registrationResult.isFailure()) {
            connection.send(new RegisterResponsePacket(false, registrationResult.error()));
            return;
        }

        connection.send(new RegisterResponsePacket(true, "REGISTER_SUCCESS"));
    }

    private static void sendCharacterList(
            AuthConnection connection,
            CharacterListService characterListService,
            long accountId
    ) throws IOException {
        List<String> characterNames = characterListService.list(accountId).stream()
                .map(character -> character.name())
                .toList();
        connection.send(new CharacterListPacket(accountId, characterNames));
    }

    private static void handleCharacterCreate(
            AuthConnection connection,
            AuthApplication application,
            CharacterCreateRequestPacket packet
    ) throws IOException {
        Result<CharacterRecord, String> createResult = application.characterCreateService()
                .create(packet.accountId(), packet.characterName());

        if (createResult.isFailure()) {
            connection.send(new CharacterCreateResponsePacket(
                    false,
                    createResult.error(),
                    packet.accountId(),
                    ""
            ));
            return;
        }

        CharacterRecord character = createResult.value();
        connection.send(new CharacterCreateResponsePacket(
                true,
                "CHARACTER_CREATE_SUCCESS",
                character.accountId(),
                character.name()
        ));
        sendCharacterList(connection, application.characterListService(), character.accountId());
    }

    private static void logCharacterListRequest(
            AuthConnection connection,
            AuthApplication application,
            Packet packet
    ) throws IOException {
        SocketAddress remoteAddress = connection.socket().getRemoteSocketAddress();
        int availableCharacters = application.characterListService().list(1L).size();
        System.out.printf(
                "Auth packet %s received from %s. Placeholder account has %d characters.%n",
                packet.opcode(),
                remoteAddress,
                availableCharacters
        );
    }

    private static String remoteKey(AuthConnection connection) {
        SocketAddress remoteAddress = connection.socket().getRemoteSocketAddress();
        return remoteAddress == null ? connection.id().toString() : remoteAddress.toString();
    }
}
