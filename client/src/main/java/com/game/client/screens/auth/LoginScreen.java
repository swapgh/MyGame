package com.game.client.screens.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.controllers.auth.LoginController;
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
    private volatile String status = "Secure access required";
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
        float viewportWidth = gameClient.uiCamera().viewportWidth;
        float viewportHeight = gameClient.uiCamera().viewportHeight;
        float panelWidth = 680f;
        float panelHeight = 320f;
        float panelX = (viewportWidth - panelWidth) * 0.5f;
        float panelY = Math.max(72f, (viewportHeight * 0.5f) - 110f);
        float titleCenterX = viewportWidth * 0.5f;
        float titleTopY = panelY + panelHeight + 120f;

        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        uiRenderer.renderHeroCentered(
                gameClient,
                "",
                "OOT Client",
                "Secure access required",
                titleCenterX,
                titleTopY
        );
        uiRenderer.renderLauncherPanel(gameClient, panelX, panelY, panelWidth, panelHeight);
        uiRenderer.renderStatusBadge(gameClient, panelX + panelWidth - 170f, panelY + panelHeight - 66f, "ACTIVE");
        uiRenderer.renderLauncherField(
                gameClient,
                "Username",
                usernameBuffer.value(),
                panelX + 50f,
                panelY + 168f,
                focusedField == 0,
                "user"
        );
        uiRenderer.renderLauncherField(
                gameClient,
                "Password",
                mask(passwordBuffer.value()),
                panelX + 50f,
                panelY + 74f,
                focusedField == 1,
                "lock"
        );
        uiRenderer.renderActionButton(gameClient, "LOGIN", panelX + 50f, panelY + 12f, 580f, 44f, !busy);
        uiRenderer.renderStatus(
                gameClient,
                status,
                panelX + 50f,
                panelY - 18f,
                busy ? ClientUiPalette.TEXT_WARNING : ClientUiPalette.TEXT_MUTED
        );
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
        status = "Connecting...";
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
