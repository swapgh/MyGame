package com.game.client.screens.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.controller.auth.LoginController;
import com.game.client.input.TextInputBuffer;
import com.game.client.render.ClientUiPalette;
import com.game.client.render.ClientUiRenderer;
import com.game.client.screens.Screen;
import com.game.client.ui.ScreenController;

/**
 * Minimal login screen for the desktop flow.
 *
 * @since 0.1.0
 */
public final class LoginScreen extends InputAdapter implements Screen {
    private final GameClient gameClient;
    private final ScreenController screenController;
    private final LoginController loginController;
    private final ClientUiRenderer uiRenderer = new ClientUiRenderer();
    private final TextInputBuffer usernameBuffer = new TextInputBuffer("dev");
    private final TextInputBuffer passwordBuffer = new TextInputBuffer("dev-password");

    private volatile boolean busy;
    private volatile String status = "Type credentials, TAB switches field, ENTER logs in, R opens register.";
    private int focusedField;

    /**
     * Creates the login screen.
     *
     * @param gameClient the owning game client
     * @param screenController the screen controller
     */
    public LoginScreen(GameClient gameClient, ScreenController screenController) {
        this.gameClient = gameClient;
        this.screenController = screenController;
        this.loginController = new LoginController(gameClient.authClient());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Renders the login prompt and handles shortcut input.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        if (!busy) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                login();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                focusedField = (focusedField + 1) % 2;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                screenController.showRegister();
                return;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                screenController.showSettings();
                return;
            }
        }

        Gdx.gl.glClearColor(0.03f, 0.06f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        uiRenderer.renderBackdrop(gameClient, delta + (System.currentTimeMillis() / 1000f));
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        uiRenderer.renderHero(gameClient, "Gateway", "OOT Client", "Sign in to the auth server and continue into the world.");
        uiRenderer.renderInfo(
                gameClient,
                "Auth  " + gameClient.clientConfig().authHost() + ":" + gameClient.clientConfig().authPort(),
                96f,
                500f
        );
        uiRenderer.renderInfo(
                gameClient,
                "World " + gameClient.clientConfig().worldHost() + ":" + gameClient.clientConfig().worldPort(),
                96f,
                470f
        );
        uiRenderer.renderField(gameClient, "Username", usernameBuffer.value(), 96f, 402f, focusedField == 0);
        uiRenderer.renderField(gameClient, "Password", mask(passwordBuffer.value()), 96f, 328f, focusedField == 1);
        uiRenderer.renderStatus(gameClient, status, 96f, 242f, busy ? ClientUiPalette.TEXT_WARNING : ClientUiPalette.TEXT_ACCENT);
        uiRenderer.renderInfo(gameClient, "TAB switch field  ENTER login  R register  S settings", 96f, 196f);
        uiRenderer.renderInfo(gameClient, "State: " + gameClient.stateMachine().currentState(), 96f, 162f);
        gameClient.spriteBatch().end();
    }

    @Override
    public boolean keyTyped(char character) {
        if (busy) {
            return false;
        }
        if (character == '\b') {
            activeBuffer().backspace();
            return true;
        }
        activeBuffer().append(character);
        return true;
    }

    private void login() {
        busy = true;
        status = "Connecting to auth server...";
        String username = usernameBuffer.value().trim();
        String password = passwordBuffer.value();
        loginController.login(
                username,
                password,
                result -> {
                    busy = false;
                    if (!result.loginResponse().success()) {
                        screenController.showError("Login failed: " + result.loginResponse().message());
                        return;
                    }
                    screenController.showCharacterSelect(
                            result.loginResponse().accountId(),
                            result.loginResponse().sessionToken(),
                            result.characterNames()
                    );
                },
                message -> {
                    busy = false;
                    screenController.showError("Auth connection failed: " + message);
                }
        );
    }

    private TextInputBuffer activeBuffer() {
        return focusedField == 0 ? usernameBuffer : passwordBuffer;
    }

    private static String mask(String value) {
        return "*".repeat(Math.max(0, value.length()));
    }
}
