package com.game.client.controllers.auth;

import com.badlogic.gdx.Gdx;
import com.game.client.service.AuthService;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Executes registration actions for the auth screens.
 *
 * @since 0.1.0
 */
public final class RegisterController {
    private final AuthService authService;

    /**
     * Creates a register controller backed by the auth client.
     *
     * @param authService the auth service
     */
    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Starts an asynchronous registration request.
     *
     * @param username requested username
     * @param password requested password
     * @param onSuccess success callback on the render thread
     * @param onError error callback on the render thread
     */
    public void register(
            String username,
            String password,
            Consumer<String> onSuccess,
            Consumer<String> onError
    ) {
        Thread registerThread = new Thread(() -> {
            try {
                String result = authService.register(username, password);
                Gdx.app.postRunnable(() -> onSuccess.accept(result));
            } catch (IOException | IllegalArgumentException exception) {
                Gdx.app.postRunnable(() -> onError.accept(exception.getMessage()));
            }
        }, "auth-register");
        registerThread.setDaemon(true);
        registerThread.start();
    }
}
