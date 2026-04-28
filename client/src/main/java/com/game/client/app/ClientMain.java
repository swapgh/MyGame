package com.game.client.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.game.client.settings.ClientConfig;

import java.io.IOException;

/**
 * Desktop entry point for the desktop client.
 *
 * @since 0.1.0
 */
public final class ClientMain {
    private ClientMain() {
    }

    /**
     * Loads the desktop client configuration and opens the LibGDX window.
     *
     * @param args unused startup arguments
     * @throws IOException if the client config cannot be read
     */
    public static void main(String[] args) throws IOException {
        ClientConfig clientConfig = ClientConfig.loadDefault();

        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("OOT Client");
        configuration.setWindowedMode(1280, 720);
        configuration.setForegroundFPS(60);
        configuration.useVsync(true);

        new Lwjgl3Application(new GameClient(clientConfig), configuration);
    }
}
