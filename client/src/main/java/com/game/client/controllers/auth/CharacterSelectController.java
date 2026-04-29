package com.game.client.controllers.auth;

import com.badlogic.gdx.Gdx;
import com.game.client.network.auth.AuthClient;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Executes character actions from the character select screen.
 *
 * @since 0.1.0
 */
public final class CharacterSelectController {
    private final AuthClient authClient;

    /**
     * Creates a controller backed by the auth client.
     *
     * @param authClient the auth client
     */
    public CharacterSelectController(AuthClient authClient) {
        this.authClient = authClient;
    }

    /**
     * Starts an asynchronous character creation request.
     *
     * @param accountId owning account id
     * @param characterName requested character name
     * @param onSuccess success callback on the render thread
     * @param onError error callback on the render thread
     */
    public void createCharacter(
            long accountId,
            String characterName,
            Consumer<AuthClient.CharacterCreateFlowResult> onSuccess,
            Consumer<String> onError
    ) {
        Thread createThread = new Thread(() -> {
            try {
                AuthClient.CharacterCreateFlowResult result = authClient.createCharacter(accountId, characterName);
                Gdx.app.postRunnable(() -> onSuccess.accept(result));
            } catch (IOException | IllegalArgumentException exception) {
                Gdx.app.postRunnable(() -> onError.accept(exception.getMessage()));
            }
        }, "character-create");
        createThread.setDaemon(true);
        createThread.start();
    }
}
