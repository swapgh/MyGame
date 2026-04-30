package com.game.client.controllers.auth;

import com.badlogic.gdx.Gdx;
import com.game.client.model.LoginSessionState;
import com.game.client.service.AuthService;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Executes login actions for the auth screens.
 *
 * @since 0.1.0
 */
public final class LoginController {
    private final AuthService authService;

    /**
     * Creates a login controller backed by the auth client.
     *
     * @param authService the auth service
     */
    public LoginController(AuthService authService) {
        this.authService = authService;
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
            Consumer<LoginSessionState> onSuccess,
            Consumer<String> onError
    ) {
        Thread loginThread = new Thread(() -> {
            try {
                LoginSessionState result = authService.login(username, password);
                Gdx.app.postRunnable(() -> onSuccess.accept(result));
            } catch (IOException | IllegalArgumentException | IllegalStateException exception) {
                Gdx.app.postRunnable(() -> onError.accept(exception.getMessage()));
            }
        }, "auth-login");
        loginThread.setDaemon(true);
        loginThread.start();
    }
}
