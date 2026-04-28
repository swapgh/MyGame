package com.game.client.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.ClientConfig;
import com.game.client.network.AuthClient;
import com.game.client.network.WorldClient;
import com.game.client.screens.ScreenManager;

/**
 * Root LibGDX game object for the Phase 4 desktop client.
 *
 * @since 0.1.0
 */
public final class GameClient extends Game {
    private final ClientConfig clientConfig;

    private final ClientStateMachine stateMachine;
    private final AuthClient authClient;
    private final WorldClient worldClient;
    private final OrthographicCamera uiCamera;

    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private ScreenManager screenManager;
    private Thread shutdownHookThread;

    /**
     * Creates the client game with its network configuration.
     *
     * @param clientConfig the desktop client configuration
     */
    public GameClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.stateMachine = new ClientStateMachine();
        this.authClient = new AuthClient(clientConfig);
        this.worldClient = new WorldClient(clientConfig);
        this.uiCamera = new OrthographicCamera();
    }

    /**
     * Bootstraps render resources and shows the login screen.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();
        uiCamera.setToOrtho(false, 1280f, 720f);

        screenManager = new ScreenManager(this);
        screenManager.showLogin();

        shutdownHookThread = new Thread(new ClientShutdownHook(this), "client-shutdown-hook");
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);
    }

    /**
     * Resizes the shared UI camera with the desktop window.
     *
     * @param width the new window width
     * @param height the new window height
     */
    @Override
    public void resize(int width, int height) {
        uiCamera.setToOrtho(false, width, height);
        super.resize(width, height);
    }

    /**
     * Releases client resources and closes sockets.
     */
    @Override
    public void dispose() {
        shutdownNetworking();

        if (screenManager != null) {
            screenManager.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }

        if (shutdownHookThread != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHookThread);
            } catch (IllegalStateException ignored) {
                // The JVM is already shutting down.
            }
        }
    }

    /**
     * Closes active client networking resources.
     */
    public void shutdownNetworking() {
        worldClient.close();
    }

    /**
     * Returns the loaded client configuration.
     *
     * @return the client configuration
     */
    public ClientConfig clientConfig() {
        return clientConfig;
    }

    /**
     * Returns the shared client state machine.
     *
     * @return the state machine
     */
    public ClientStateMachine stateMachine() {
        return stateMachine;
    }

    /**
     * Returns the auth client.
     *
     * @return the auth client
     */
    public AuthClient authClient() {
        return authClient;
    }

    /**
     * Returns the world client.
     *
     * @return the world client
     */
    public WorldClient worldClient() {
        return worldClient;
    }

    /**
     * Returns the screen manager.
     *
     * @return the screen manager
     */
    public ScreenManager screenManager() {
        return screenManager;
    }

    /**
     * Returns the shared sprite batch.
     *
     * @return the sprite batch
     */
    public SpriteBatch spriteBatch() {
        return spriteBatch;
    }

    /**
     * Returns the shared font.
     *
     * @return the font
     */
    public BitmapFont font() {
        return font;
    }

    /**
     * Returns the shared shape renderer.
     *
     * @return the shape renderer
     */
    public ShapeRenderer shapeRenderer() {
        return shapeRenderer;
    }

    /**
     * Returns the shared UI camera.
     *
     * @return the UI camera
     */
    public OrthographicCamera uiCamera() {
        return uiCamera;
    }
}
