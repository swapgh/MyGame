package com.game.client.screens.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.game.client.app.GameClient;
import com.game.client.controllers.auth.RegisterController;
import com.game.client.input.SubmitKey;
import com.game.client.input.TextInputBuffer;
import com.game.client.ui.theme.UiPalette;
import com.game.client.screens.UiScreen;
import com.game.client.screens.ScreenController;
import com.game.client.ui.core.UiDocument;
import com.game.client.ui.core.UiHero;
import com.game.client.ui.core.UiTextLine;

/**
 * Minimal registration screen for the client.
 *
 * @since 0.1.0
 */
public final class RegisterScreen extends UiScreen {
    private final GameClient gameClient;
    private final ScreenController screenController;
    private final RegisterController registerController;
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
        super(gameClient);
        this.gameClient = gameClient;
        this.screenController = screenController;
        this.registerController = new RegisterController(gameClient.authService());
    }

    @Override
    protected void update(float delta) {
        if (!busy) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                screenController.showLogin();
                return;
            }
            if (SubmitKey.isJustPressed()) {
                register();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                focusedField = (focusedField + 1) % 2;
            }
        }
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

    @Override
    protected UiDocument buildDocument(float delta) {
        return UiDocument.builder()
                .hero(UiHero.left(
                        "Accounts",
                        "Create Account",
                        "Provision a new account on the auth server.",
                        96f,
                        642f
                ))
                .line(UiTextLine.status(
                        status,
                        96f,
                        270f,
                        busy ? UiPalette.TEXT_WARNING : UiPalette.TEXT_ACCENT
                ))
                .line(UiTextLine.info("TAB switch field  ENTER register  B back", 96f, 214f))
                .line(UiTextLine.info("Keyboard-first flow for fast testing, now with polished framing.", 96f, 180f))
                .build();
    }

    @Override
    protected void afterRender(float delta) {
        uiRenderer().renderField(gameClient, "Username", usernameBuffer.value(), 96f, 432f, focusedField == 0);
        uiRenderer().renderField(gameClient, "Password", mask(passwordBuffer.value()), 96f, 358f, focusedField == 1);
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
