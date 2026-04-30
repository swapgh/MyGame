package com.game.client.controllers.auth;

import com.badlogic.gdx.Gdx;
import com.game.client.network.auth.AuthClient;
import com.game.client.service.AuthService;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Executes character actions from the character select screen.
 *
 * @since 0.1.0
 */
public final class CharacterSelectController {
    private final AuthService authService;

    /**
     * Creates a controller backed by the auth client.
     *
     * @param authService the auth service
     */
    public CharacterSelectController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Starts an asynchronous character creation request.
     *
     * @param characterName requested character name
     * @param onSuccess success callback on the render thread
     * @param onError error callback on the render thread
     */
    public void createCharacter(
            String characterName,
            Consumer<AuthClient.CharacterCreateFlowResult> onSuccess,
            Consumer<String> onError
    ) {
        Thread createThread = new Thread(() -> {
            try {
                AuthClient.CharacterCreateFlowResult result = authService.createCharacter(characterName);
                Gdx.app.postRunnable(() -> onSuccess.accept(result));
            } catch (IOException | IllegalArgumentException | IllegalStateException exception) {
                Gdx.app.postRunnable(() -> onError.accept(exception.getMessage()));
            }
        }, "character-create");
        createThread.setDaemon(true);
        createThread.start();
    }
}
