package com.game.client.app;

/**
 * Closes client-side network resources during desktop shutdown.
 *
 * @since 0.1.0
 */
public final class ClientShutdownHook implements Runnable {
    private final GameClient gameClient;

    /**
     * Creates a shutdown hook for the given game client.
     *
     * @param gameClient the owning game client
     */
    public ClientShutdownHook(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    /**
     * Closes active network resources if the client is still running.
     */
    @Override
    public void run() {
        gameClient.shutdownNetworking();
    }
}
