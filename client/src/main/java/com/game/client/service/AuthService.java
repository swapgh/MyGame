package com.game.client.service;

import com.game.client.model.AuthenticatedSession;
import com.game.client.model.LoginSessionState;
import com.game.client.network.auth.AuthClient;

import java.io.IOException;
import java.util.List;

/**
 * Coordinates authentication and character roster loading for the client.
 *
 * @since 0.1.0
 */
public final class AuthService {
    private final AuthClient authClient;
    private final ClientSessionService clientSessionService;

    /**
     * Creates an auth service backed by the auth client.
     *
     * @param authClient auth transport client
     * @param clientSessionService shared session state
     */
    public AuthService(AuthClient authClient, ClientSessionService clientSessionService) {
        this.authClient = authClient;
        this.clientSessionService = clientSessionService;
    }

    /**
     * Performs login and stores the resulting authenticated session.
     *
     * @param username requested username
     * @param password requested password
     * @return login session state plus character roster
     * @throws IOException if the auth server cannot be reached
     */
    public LoginSessionState login(String username, String password) throws IOException {
        AuthClient.AuthFlowResult result = authClient.login(username, password);
        if (!result.loginResponse().success()) {
            return new LoginSessionState(new AuthenticatedSession(-1L, ""), List.of());
        }

        LoginSessionState sessionState = new LoginSessionState(
                new AuthenticatedSession(
                        result.loginResponse().accountId(),
                        result.loginResponse().sessionToken()
                ),
                List.copyOf(result.characterNames())
        );
        clientSessionService.storeLoginSession(sessionState);
        return sessionState;
    }

    /**
     * Registers a new account on the auth server.
     *
     * @param username requested username
     * @param password requested password
     * @return the raw server response
     * @throws IOException if the auth server cannot be reached
     */
    public String register(String username, String password) throws IOException {
        return authClient.register(username, password);
    }

    /**
     * Creates a character using the currently stored login session.
     *
     * @param characterName requested character name
     * @return the creation flow result
     * @throws IOException if the auth server cannot be reached
     */
    public AuthClient.CharacterCreateFlowResult createCharacter(String characterName) throws IOException {
        LoginSessionState loginSession = requireLoginSession();
        AuthClient.CharacterCreateFlowResult result = authClient.createCharacter(
                loginSession.session().accountId(),
                characterName
        );
        if (result.createResponse().success()) {
            clientSessionService.storeLoginSession(new LoginSessionState(
                    loginSession.session(),
                    List.copyOf(result.characterNames())
            ));
        }
        return result;
    }

    /**
     * Clears local session state after logout/back-to-login flow.
     */
    public void clearSession() {
        clientSessionService.clear();
    }

    private LoginSessionState requireLoginSession() {
        LoginSessionState loginSession = clientSessionService.loginSession();
        if (loginSession == null) {
            throw new IllegalStateException("No authenticated login session is available.");
        }
        return loginSession;
    }
}
