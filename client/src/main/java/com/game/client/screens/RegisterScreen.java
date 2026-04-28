package com.game.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.input.TextInputBuffer;
import com.game.client.ui.ScreenManager;

import java.io.IOException;

/**
 * Minimal registration screen for the client.
 *
 * @since 0.1.0
 */
public final class RegisterScreen extends InputAdapter implements Screen {
    private final GameClient gameClient;
    private final ScreenManager screenManager;
    private final TextInputBuffer usernameBuffer = new TextInputBuffer("");
    private final TextInputBuffer passwordBuffer = new TextInputBuffer("");

    private volatile boolean busy;
    private volatile String status = "Type a new account, TAB switches field, ENTER registers, B goes back.";
    private int focusedField;

    /**
     * Creates the register screen.
     *
     * @param gameClient the owning game client
     * @param screenManager the screen manager
     */
    public RegisterScreen(GameClient gameClient, ScreenManager screenManager) {
        this.gameClient = gameClient;
        this.screenManager = screenManager;
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
                screenManager.showLogin();
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                register();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                focusedField = (focusedField + 1) % 2;
            }
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        gameClient.font().draw(gameClient.spriteBatch(), "Register Screen", 80f, 640f);
        gameClient.font().draw(gameClient.spriteBatch(), fieldLabel(0, "Username", usernameBuffer.value()), 80f, 590f);
        gameClient.font().draw(gameClient.spriteBatch(), fieldLabel(1, "Password", mask(passwordBuffer.value())), 80f, 560f);
        gameClient.font().draw(gameClient.spriteBatch(), status, 80f, 510f);
        gameClient.font().draw(gameClient.spriteBatch(), "This is still keyboard-driven placeholder UI for Phase 4.", 80f, 470f);
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

        Thread registerThread = new Thread(() -> {
            try {
                String result = gameClient.authClient().register(username, password);
                Gdx.app.postRunnable(() -> {
                    busy = false;
                    status = "Register result for " + username + ": " + result;
                });
            } catch (IOException | IllegalArgumentException exception) {
                Gdx.app.postRunnable(() -> {
                    busy = false;
                    status = "Register failed: " + exception.getMessage();
                });
            }
        }, "auth-register");
        registerThread.setDaemon(true);
        registerThread.start();
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
