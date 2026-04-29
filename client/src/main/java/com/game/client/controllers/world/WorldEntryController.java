package com.game.client.controllers.world;

import com.badlogic.gdx.Gdx;
import com.game.client.network.world.WorldClient;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Executes world-entry actions for the loading screen.
 *
 * @since 0.1.0
 */
public final class WorldEntryController {
    private final WorldClient worldClient;

    /**
     * Creates a world entry controller backed by the world client.
     *
     * @param worldClient the world client
     */
    public WorldEntryController(WorldClient worldClient) {
        this.worldClient = worldClient;
    }

    /**
     * Starts an asynchronous world entry request.
     *
     * @param characterName selected character name
     * @param onSuccess success callback on the render thread
     * @param onError error callback on the render thread
     */
    public void enterWorld(
            String characterName,
            Consumer<WorldClient.WorldEntryResult> onSuccess,
            Consumer<String> onError
    ) {
        Thread worldThread = new Thread(() -> {
            try {
                WorldClient.WorldEntryResult result = worldClient.enterWorld(characterName);
                Gdx.app.postRunnable(() -> onSuccess.accept(result));
            } catch (IOException | IllegalArgumentException exception) {
                Gdx.app.postRunnable(() -> onError.accept(exception.getMessage()));
            }
        }, "world-entry");
        worldThread.setDaemon(true);
        worldThread.start();
    }
}
