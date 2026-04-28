package com.game.client.screens.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.controllers.auth.RegisterController;
import com.game.client.input.TextInputBuffer;
import com.game.client.screens.Screen;
import com.game.client.render.ClientUiPalette;
import com.game.client.render.ClientUiRenderer;
import com.game.client.ui.ScreenController;

/**
 * Minimal registration screen for the client.
 *
 * @since 0.1.0
 */
public final class RegisterScreen extends InputAdapter implements Screen {
    private final GameClient gameClient;
    private final ScreenController screenController;
    private final RegisterController registerController;
    private final ClientUiRenderer uiRenderer = new ClientUiRenderer();
    private final TextInputBuffer usernameBuffer = new TextInputBuffer("");
    private final TextInputBuffer passwordBuffer = new TextInputBuffer("");

    private volatile boolean busy;
    private volatile String status = "Type a new account, TAB switches field, ENTER registers, B goes back.";
    private int focusedField;

    /**
     * Creates the register screen.
     *
     * @param gameClient the owning game client
     * @param screenController the screen controller
     */
    public RegisterScreen(GameClient gameClient, ScreenController screenController) {
        this.gameClient = gameClient;
        this.screenController = screenController;
        this.registerController = new RegisterController(gameClient.authClient());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Renders the register prompt and handles shortcut input.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        if (!busy) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                screenController.showLogin();
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                register();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                focusedField = (focusedField + 1) % 2;
            }
        }

        Gdx.gl.glClearColor(0.03f, 0.06f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        uiRenderer.renderBackdrop(gameClient, delta + (System.currentTimeMillis() / 1000f));
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        uiRenderer.renderHero(gameClient, "Accounts", "Create Account", "Provision a new account on the auth server.");
        uiRenderer.renderField(gameClient, "Username", usernameBuffer.value(), 96f, 432f, focusedField == 0);
        uiRenderer.renderField(gameClient, "Password", mask(passwordBuffer.value()), 96f, 358f, focusedField == 1);
        uiRenderer.renderStatus(gameClient, status, 96f, 270f, busy ? ClientUiPalette.TEXT_WARNING : ClientUiPalette.TEXT_ACCENT);
        uiRenderer.renderInfo(gameClient, "TAB switch field  ENTER register  B back", 96f, 214f);
        uiRenderer.renderInfo(gameClient, "Keyboard-first flow for fast testing, now with polished framing.", 96f, 180f);
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

    private void register() {
        busy = true;
        status = "Creating account...";
        String username = usernameBuffer.value().trim();
        String password = passwordBuffer.value();
        registerController.register(
                username,
                password,
                result -> {
                    busy = false;
                    status = "Register result for " + username + ": " + result;
                },
                message -> {
                    busy = false;
                    status = "Register failed: " + message;
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
