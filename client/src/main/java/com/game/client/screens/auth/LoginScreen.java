package com.game.client.screens.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.game.client.app.GameClient;
import com.game.client.controllers.auth.LoginController;
import com.game.client.input.SubmitKey;
import com.game.client.ui.theme.UiPalette;
import com.game.client.screens.UiScreen;
import com.game.client.screens.ScreenController;
import com.game.client.ui.components.UiButton;
import com.game.client.ui.components.UiContainer;
import com.game.client.ui.components.UiFormPanel;
import com.game.client.ui.components.UiTextField;
import com.game.client.ui.core.UiDocument;
import com.game.client.ui.core.UiHero;
import com.game.client.ui.core.UiSection;
import com.game.client.ui.core.UiTextLine;
import com.game.client.ui.layouts.UiRect;
import com.game.client.ui.layouts.UiViewportLayout;

/**
 * Minimalistic login screen using the widget system.
 *
 * @since 0.1.0
 */
public final class LoginScreen extends UiScreen {

    private static final float PANEL_WIDTH = 520f;
    private static final float PANEL_HEIGHT = 320f;
    private static final float FIELD_HEIGHT = 48f;
    private static final float BUTTON_HEIGHT = 44f;
    private static final float SPACING = 16f;

    private final GameClient gameClient;
    private final ScreenController screenController;
    private final LoginController loginController;

    private final UiFormPanel formPanel;
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
        super(gameClient);
        this.gameClient = gameClient;
        this.screenController = screenController;
        this.loginController = new LoginController(gameClient.authService());

        usernameField = new UiTextField("Username");
        usernameField.setBounds(0, 0, PANEL_WIDTH - 40f, FIELD_HEIGHT);
        usernameField.setValue("dev");
        usernameField.setFocused(true);

        passwordField = new UiTextField("Password", true);
        passwordField.setBounds(0, 0, PANEL_WIDTH - 40f, FIELD_HEIGHT);
        passwordField.setValue("dev-password");

        loginButton = new UiButton("LOGIN", this::login);
        loginButton.setBounds(0, 0, PANEL_WIDTH - 40f, BUTTON_HEIGHT);

        UiContainer formContainer = new UiContainer(UiContainer.LayoutType.VERTICAL, 20f, SPACING);
        formContainer.addChild(usernameField);
        formContainer.addChild(passwordField);
        formContainer.addChild(loginButton);
        formPanel = new UiFormPanel(formContainer);
    }

    @Override
    protected void update(float delta) {
        if (!busy) {
            if (SubmitKey.isJustPressed()) {
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

        float viewportWidth = gameClient.uiCamera().viewportWidth;
        float viewportHeight = gameClient.uiCamera().viewportHeight;
        UiRect panelBounds = UiViewportLayout.centeredPanel(
                viewportWidth,
                viewportHeight,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                72f
        );

        formPanel.setBounds(panelBounds);
        updateWidgetInput(formPanel.root(), formPanel::clearFocus);
    }

    @Override
    protected void afterRender(float delta) {
        formPanel.update(delta);
    }

    @Override
    public boolean keyTyped(char character) {
        if (busy) {
            return false;
        }
        return formPanel.root().handleKeyTyped(character);
    }

    @Override
    protected UiDocument buildDocument(float delta) {
        UiRect panelBounds = formPanel.bounds();
        return UiDocument.builder()
                .hero(UiHero.centered(
                        "",
                        "OOT Client",
                        "Secure access required",
                        panelBounds.centerX(),
                        panelBounds.top() + 100f
                ))
                .section(UiSection.builder(panelBounds)
                        .form(formPanel)
                        .line(UiTextLine.status(
                                status,
                                panelBounds.x() + 20f,
                                panelBounds.y() - 18f,
                                busy ? UiPalette.TEXT_WARNING : UiPalette.TEXT_MUTED
                        ))
                        .build())
                .build();
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
                    if (result.session().accountId() < 0L) {
                        screenController.showError("Login failed.");
                        return;
                    }
                    screenController.showCharacterSelect(
                            result.session().accountId(),
                            result.session().sessionToken(),
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
