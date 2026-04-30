package com.game.client.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.network.auth.AuthClient;
import com.game.client.network.world.WorldClient;
import com.game.client.ui.theme.UiFont;
import com.game.client.service.AuthService;
import com.game.client.service.ClientSessionService;
import com.game.client.service.TargetingService;
import com.game.client.service.WorldActionService;
import com.game.client.service.WorldFeedbackService;
import com.game.client.service.WorldService;
import com.game.client.settings.ClientConfig;
import com.game.client.screens.ScreenController;

/**
 * Root LibGDX game object for the desktop client.
 *
 * @since 0.1.0
 */
public final class GameClient extends Game {
    private final ClientConfig clientConfig;

    private final ClientStateMachine stateMachine;
    private final AuthClient authClient;
    private final WorldClient worldClient;
    private final ClientSessionService clientSessionService;
    private final AuthService authService;
    private final WorldService worldService;
    private final TargetingService targetingService;
    private final WorldActionService worldActionService;
    private final WorldFeedbackService worldFeedbackService;
    private final OrthographicCamera uiCamera;

    private SpriteBatch spriteBatch;
    private UiFont uiFont;
    private ShapeRenderer shapeRenderer;
    private ScreenController screenController;
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
        this.clientSessionService = new ClientSessionService();
        this.authService = new AuthService(authClient, clientSessionService);
        this.worldService = new WorldService(worldClient, clientSessionService);
        this.targetingService = new TargetingService();
        this.worldActionService = new WorldActionService();
        this.worldFeedbackService = new WorldFeedbackService();
        this.uiCamera = new OrthographicCamera();
    }

    /**
     * Bootstraps render resources and shows the login screen.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        uiFont = UiFont.load("assets/ui/fonts/Purisa.ttf");
        shapeRenderer = new ShapeRenderer();
        uiCamera.setToOrtho(false, 1280f, 720f);

        screenController = new ScreenController(this);
        screenController.showLogin();

        shutdownHookThread = new Thread(new ClientShutdownHook(this), "client-shutdown-hook");
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);
    }

    /**
     * Resizes the shared UI camera with the desktop window.
     *
     * @param width  the new window width
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

        if (screenController != null) {
            screenController.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (uiFont != null) {
            uiFont.dispose();
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
     * Returns the auth service.
     *
     * @return the auth service
     */
    public AuthService authService() {
        return authService;
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
     * Returns the world service.
     *
     * @return the world service
     */
    public WorldService worldService() {
        return worldService;
    }

    /**
     * Returns the shared client session service.
     *
     * @return the session service
     */
    public ClientSessionService clientSessionService() {
        return clientSessionService;
    }

    /**
     * Returns the shared targeting service.
     *
     * @return the targeting service
     */
    public TargetingService targetingService() {
        return targetingService;
    }

    /**
     * Returns the world action service.
     *
     * @return the world action service
     */
    public WorldActionService worldActionService() {
        return worldActionService;
    }

    /**
     * Returns the world feedback service.
     *
     * @return the world feedback service
     */
    public WorldFeedbackService worldFeedbackService() {
        return worldFeedbackService;
    }

    /**
     * Returns the screen controller.
     *
     * @return the screen controller
     */
    public ScreenController screenController() {
        return screenController;
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
     * Returns the shared UI fonts at all sizes.
     *
     * @return the UI font set
     */
    public UiFont uiFont() {
        return uiFont;
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
