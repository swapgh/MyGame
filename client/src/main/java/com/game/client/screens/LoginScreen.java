package com.game.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.network.AuthClient;

import java.io.IOException;

/**
 * Minimal login screen for the Phase 4 desktop flow.
 *
 * @since 0.1.0
 */
public final class LoginScreen extends InputAdapter implements Screen {
    private final GameClient gameClient;
    private final ScreenManager screenManager;
    private final TextInputBuffer usernameBuffer = new TextInputBuffer("dev");
    private final TextInputBuffer passwordBuffer = new TextInputBuffer("dev-password");

    private volatile boolean busy;
    private volatile String status = "Type credentials, TAB switches field, ENTER logs in, R opens register.";
    private int focusedField;

    /**
     * Creates the login screen.
     *
     * @param gameClient the owning game client
     * @param screenManager the screen manager
     */
    public LoginScreen(GameClient gameClient, ScreenManager screenManager) {
        this.gameClient = gameClient;
        this.screenManager = screenManager;
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
                screenManager.showRegister();
                return;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                screenManager.showSettings();
                return;
            }
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        gameClient.font().draw(gameClient.spriteBatch(), "OOT Client - Phase 4", 80f, 640f);
        gameClient.font().draw(gameClient.spriteBatch(), "Login Screen", 80f, 590f);
        gameClient.font().draw(gameClient.spriteBatch(), "Auth: " + gameClient.clientConfig().authHost() + ":" + gameClient.clientConfig().authPort(), 80f, 540f);
        gameClient.font().draw(gameClient.spriteBatch(), "World: " + gameClient.clientConfig().worldHost() + ":" + gameClient.clientConfig().worldPort(), 80f, 510f);
        gameClient.font().draw(gameClient.spriteBatch(), fieldLabel(0, "Username", usernameBuffer.value()), 80f, 460f);
        gameClient.font().draw(gameClient.spriteBatch(), fieldLabel(1, "Password", mask(passwordBuffer.value())), 80f, 430f);
        gameClient.font().draw(gameClient.spriteBatch(), status, 80f, 390f);
        gameClient.font().draw(gameClient.spriteBatch(), "Current state: " + gameClient.stateMachine().currentState(), 80f, 350f);
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

        Thread loginThread = new Thread(() -> {
            try {
                AuthClient.AuthFlowResult result = gameClient.authClient().login(username, password);
                Gdx.app.postRunnable(() -> {
                    busy = false;
                    if (!result.loginResponse().success()) {
                        screenManager.showError("Login failed: " + result.loginResponse().message());
                        return;
                    }
                    screenManager.showCharacterSelect(
                            result.loginResponse().accountId(),
                            result.loginResponse().sessionToken(),
                            result.characterNames()
                    );
                });
            } catch (IOException | IllegalArgumentException exception) {
                Gdx.app.postRunnable(() -> {
                    busy = false;
                    screenManager.showError("Auth connection failed: " + exception.getMessage());
                });
            }
        }, "auth-login");
        loginThread.setDaemon(true);
        loginThread.start();
    }

    private TextInputBuffer activeBuffer() {
        return focusedField == 0 ? usernameBuffer : passwordBuffer;
    }

    private String fieldLabel(int fieldIndex, String label, String value) {
        String prefix = fieldIndex == focusedField ? "> " : "  ";
        return prefix + label + ": " + value;
    }

    private static String mask(String value) {
        return "*".repeat(Math.max(0, value.length()));
    }
}
