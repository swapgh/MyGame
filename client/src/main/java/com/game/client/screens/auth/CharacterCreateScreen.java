package com.game.client.screens.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.game.client.app.GameClient;
import com.game.client.controllers.auth.CharacterSelectController;
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
import com.game.client.ui.theme.UiPalette;

import java.util.ArrayList;
import java.util.List;

/**
 * Dedicated character creation screen used before entering the world.
 *
 * @since 0.1.0
 */
public final class CharacterCreateScreen extends com.game.client.screens.UiScreen {
    private static final float PANEL_WIDTH = 560f;
    private static final float PANEL_HEIGHT = 320f;
    private static final float FIELD_HEIGHT = 48f;
    private static final float BUTTON_HEIGHT = 44f;
    private static final float SPACING = 16f;

    private final GameClient gameClient;
    private final ScreenController screenController;
    private final CharacterSelectController characterSelectController;
    private final long accountId;
    private final String sessionToken;
    private final List<String> characterNames;

    private final UiFormPanel formPanel;
    private final UiTextField nameField;
    private final UiButton createButton;
    private final UiButton backButton;

    private volatile boolean busy;
    private volatile String status = "Choose a name for your first adventurer.";

    /**
     * Creates the character creation screen.
     *
     * @param gameClient the owning game client
     * @param screenController the screen controller
     * @param accountId the logged-in account id
     * @param sessionToken the issued auth session token
     * @param characterNames available character names
     */
    public CharacterCreateScreen(
            GameClient gameClient,
            ScreenController screenController,
            long accountId,
            String sessionToken,
            List<String> characterNames
    ) {
        super(gameClient);
        this.gameClient = gameClient;
        this.screenController = screenController;
        this.characterSelectController = new CharacterSelectController(gameClient.authService());
        this.accountId = accountId;
        this.sessionToken = sessionToken;
        this.characterNames = new ArrayList<>(characterNames);

        nameField = new UiTextField("Character Name");
        nameField.setBounds(0, 0, PANEL_WIDTH - 40f, FIELD_HEIGHT);
        nameField.setFocused(true);

        createButton = new UiButton("CREATE", this::createCharacter);
        createButton.setBounds(0, 0, PANEL_WIDTH - 40f, BUTTON_HEIGHT);

        backButton = new UiButton("BACK TO ROSTER", this::backToRoster);
        backButton.setBounds(0, 0, PANEL_WIDTH - 40f, BUTTON_HEIGHT);

        UiContainer formContainer = new UiContainer(UiContainer.LayoutType.VERTICAL, 20f, SPACING);
        formContainer.addChild(nameField);
        formContainer.addChild(createButton);
        formContainer.addChild(backButton);
        formPanel = new UiFormPanel(formContainer);
    }

    @Override
    protected void update(float delta) {
        if (!busy) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (!nameField.getValue().trim().isEmpty()) {
                    createCharacter();
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                backToRoster();
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
                        "Forge",
                        "Create Character",
                        "Customization can come later. For now, we just need the name and a clean first step.",
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

    private void createCharacter() {
        busy = true;
        status = "Creating character...";
        String requestedName = nameField.getValue().trim();
        characterSelectController.createCharacter(
                requestedName,
                result -> {
                    busy = false;
                    if (!result.createResponse().success()) {
                        status = "Create failed: " + result.createResponse().message();
                        return;
                    }

                    List<String> refreshedNames = new ArrayList<>(result.characterNames());
                    screenController.showCharacterSelect(accountId, sessionToken, refreshedNames);
                },
                message -> {
                    busy = false;
                    status = "Create failed: " + message;
                }
        );
    }

    private void backToRoster() {
        screenController.showCharacterSelect(accountId, sessionToken, characterNames);
    }
}
