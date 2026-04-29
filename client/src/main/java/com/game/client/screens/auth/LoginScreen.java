package com.game.client.screens.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.controllers.auth.LoginController;
import com.game.client.render.ClientUiPalette;
import com.game.client.render.ClientUiRenderer;
import com.game.client.screens.Screen;
import com.game.client.ui.ScreenController;
import com.game.client.ui.widget.UiButton;
import com.game.client.ui.widget.UiContainer;
import com.game.client.ui.widget.UiTextField;

/**
 * Minimalistic login screen using the widget system.
 *
 * @since 0.1.0
 */
public final class LoginScreen extends InputAdapter implements Screen {

    private static final float PANEL_WIDTH = 520f;
    private static final float PANEL_HEIGHT = 280f;
    private static final float FIELD_HEIGHT = 48f;
    private static final float BUTTON_HEIGHT = 44f;
    private static final float SPACING = 16f;

    private final GameClient gameClient;
    private final ScreenController screenController;
    private final LoginController loginController;
    private final ClientUiRenderer uiRenderer = new ClientUiRenderer();

    private final UiContainer formContainer;
    private final UiTextField usernameField;
    private final UiTextField passwordField;
    private final UiButton loginButton;

    private volatile boolean busy;
    private volatile String status = "Secure access required";

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

        usernameField = new UiTextField("Username");
        usernameField.setValue("dev");
        usernameField.setFocused(true);

        passwordField = new UiTextField("Password", true);
        passwordField.setValue("dev-password");

        loginButton = new UiButton("LOGIN", this::login);
        loginButton.setBounds(0, 0, PANEL_WIDTH - 40f, BUTTON_HEIGHT);

        formContainer = new UiContainer(UiContainer.LayoutType.VERTICAL, 20f, SPACING);
        formContainer.addChild(usernameField);
        formContainer.addChild(passwordField);
        formContainer.addChild(loginButton);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        if (!busy) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (!usernameField.getValue().trim().isEmpty()) {
                    login();
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                if (usernameField.isFocused()) {
                    usernameField.setFocused(false);
                    passwordField.setFocused(true);
                } else {
                    passwordField.setFocused(false);
                    usernameField.setFocused(true);
                }
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
        float panelX = (viewportWidth - PANEL_WIDTH) * 0.5f;
        float panelY = Math.max(72f, (viewportHeight * 0.5f) - (PANEL_HEIGHT * 0.5f));
        float titleCenterX = viewportWidth * 0.5f;
        float titleTopY = panelY + PANEL_HEIGHT + 100f;

        formContainer.setBounds(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        updateWidgetInput();

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
        uiRenderer.renderPanel(gameClient, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
        formContainer.render(gameClient, gameClient.spriteBatch());
        renderStatusLine(gameClient, panelX, panelY);
        gameClient.spriteBatch().end();

        usernameField.update(delta);
        passwordField.update(delta);
    }

    @Override
    public boolean keyTyped(char character) {
        if (busy) {
            return false;
        }
        return formContainer.handleKeyTyped(character);
    }

    private void updateWidgetInput() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        float worldMouseX = screenXToWorld(mouseX);
        float worldMouseY = screenYToWorld(mouseY);

        formContainer.handleMouseMove(worldMouseX, worldMouseY);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            boolean handled = formContainer.handleMousePressed(worldMouseX, worldMouseY, Input.Buttons.LEFT);
            if (!handled) {
                usernameField.setFocused(false);
                passwordField.setFocused(false);
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            formContainer.handleMouseReleased(worldMouseX, worldMouseY, Input.Buttons.LEFT);
        }
    }

    private float screenXToWorld(int screenX) {
        float viewportWidth = gameClient.uiCamera().viewportWidth;
        int screenWidth = Gdx.graphics.getWidth();
        return screenX * (viewportWidth / screenWidth);
    }

    private float screenYToWorld(int screenY) {
        float viewportHeight = gameClient.uiCamera().viewportHeight;
        int screenHeight = Gdx.graphics.getHeight();
        return viewportHeight - (screenY * (viewportHeight / screenHeight));
    }

    private void renderStatusLine(GameClient gameClient, float panelX, float panelY) {
        uiRenderer.renderStatus(
                gameClient,
                status,
                panelX + 20f,
                panelY - 18f,
                busy ? ClientUiPalette.TEXT_WARNING : ClientUiPalette.TEXT_MUTED
        );
    }

    private void login() {
        busy = true;
        status = "Connecting...";
        String username = usernameField.getValue().trim();
        String password = passwordField.getValue();
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
}
