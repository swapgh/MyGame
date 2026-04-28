package com.game.client.controller.auth;

import com.badlogic.gdx.Gdx;
import com.game.client.network.auth.AuthClient;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Executes login actions for the auth screens.
 *
 * @since 0.1.0
 */
public final class LoginController {
    private final AuthClient authClient;

    /**
     * Creates a login controller backed by the auth client.
     *
     * @param authClient the auth client
     */
    public LoginController(AuthClient authClient) {
        this.authClient = authClient;
    }

    /**
     * Starts an asynchronous login request.
     *
     * @param username requested username
     * @param password requested password
     * @param onSuccess success callback on the render thread
     * @param onError error callback on the render thread
     */
    public void login(
            String username,
            String password,
            Consumer<AuthClient.AuthFlowResult> onSuccess,
            Consumer<String> onError
    ) {
        Thread loginThread = new Thread(() -> {
            try {
                AuthClient.AuthFlowResult result = authClient.login(username, password);
                Gdx.app.postRunnable(() -> onSuccess.accept(result));
            } catch (IOException | IllegalArgumentException exception) {
                Gdx.app.postRunnable(() -> onError.accept(exception.getMessage()));
            }
        }, "auth-login");
        loginThread.setDaemon(true);
        loginThread.start();
    }
}
