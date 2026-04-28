package com.game.client.screens.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.controller.auth.CharacterSelectController;
import com.game.client.input.TextInputBuffer;
import com.game.client.render.ClientUiPalette;
import com.game.client.render.ClientUiRenderer;
import com.game.client.screens.Screen;
import com.game.client.ui.ScreenController;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal character selection screen for the client flow.
 *
 * @since 0.1.0
 */
public final class CharacterSelectScreen extends InputAdapter implements Screen {
    private final GameClient gameClient;
    private final ScreenController screenController;
    private final CharacterSelectController characterSelectController;
    private final ClientUiRenderer uiRenderer = new ClientUiRenderer();
    private final long accountId;
    private final String sessionToken;
    private final List<String> characterNames;
    private final TextInputBuffer characterNameBuffer = new TextInputBuffer("");

    private int selectedIndex;
    private volatile boolean busy;
    private volatile String status = "Use UP/DOWN and ENTER. Press C to create a character. Press B to return.";

    /**
     * Creates the character selection screen.
     *
     * @param gameClient the owning game client
     * @param screenController the screen controller
     * @param accountId the logged-in account id
     * @param sessionToken the issued session token
     * @param characterNames available character names
     */
    public CharacterSelectScreen(
            GameClient gameClient,
            ScreenController screenController,
            long accountId,
            String sessionToken,
            List<String> characterNames
    ) {
        this.gameClient = gameClient;
        this.screenController = screenController;
        this.characterSelectController = new CharacterSelectController(gameClient.authClient());
        this.accountId = accountId;
        this.sessionToken = sessionToken;
        this.characterNames = new ArrayList<>(characterNames);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Renders the character list and handles selection input.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            screenController.showLogin();
            return;
        }
        if (!busy && Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            createCharacter();
            return;
        }
        if (!busy && !characterNames.isEmpty()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedIndex = (selectedIndex + 1) % characterNames.size();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedIndex = (selectedIndex - 1 + characterNames.size()) % characterNames.size();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                screenController.showLoadingWorld(characterNames.get(selectedIndex));
                return;
            }
        }

        Gdx.gl.glClearColor(0.03f, 0.06f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        uiRenderer.renderBackdrop(gameClient, delta + (System.currentTimeMillis() / 1000f));
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        uiRenderer.renderHero(gameClient, "Roster", "Character Select", "Choose a character, or create a new one for this account.");
        uiRenderer.renderInfo(gameClient, "Account ID: " + accountId, 96f, 500f);
        uiRenderer.renderInfo(gameClient, "Session token loaded: " + (!sessionToken.isBlank()), 96f, 470f);
        uiRenderer.renderField(gameClient, "New Character", characterNameBuffer.value(), 96f, 398f, !busy);
        uiRenderer.renderStatus(gameClient, status, 96f, 312f, busy ? ClientUiPalette.TEXT_WARNING : ClientUiPalette.TEXT_ACCENT);
        uiRenderer.renderInfo(gameClient, "UP/DOWN select  ENTER enter world  C create  B back", 96f, 270f);

        if (characterNames.isEmpty()) {
            uiRenderer.renderInfo(gameClient, "No characters are available yet. Create one to continue.", 96f, 214f);
        } else {
            for (int index = 0; index < characterNames.size(); index++) {
                uiRenderer.renderListRow(
                        gameClient,
                        (index + 1) + ". " + characterNames.get(index),
                        700f,
                        466f - (index * 54f),
                        index == selectedIndex
                );
            }
        }
        gameClient.spriteBatch().end();
    }

    @Override
    public boolean keyTyped(char character) {
        if (!busy) {
            if (character == '\b') {
                characterNameBuffer.backspace();
                return true;
            }
            characterNameBuffer.append(character);
        }
        return true;
    }

    private void createCharacter() {
        busy = true;
        status = "Creating character...";
        String requestedName = characterNameBuffer.value().trim();
        characterSelectController.createCharacter(
                accountId,
                requestedName,
                result -> {
                    busy = false;
                    if (!result.createResponse().success()) {
                        status = "Create failed: " + result.createResponse().message();
                        return;
                    }

                    characterNames.clear();
                    characterNames.addAll(result.characterNames());
                    selectedIndex = Math.max(0, characterNames.indexOf(result.createResponse().characterName()));
                    characterNameBuffer.clear();
                    status = "Created " + result.createResponse().characterName();
                },
                message -> {
                    busy = false;
                    status = "Create failed: " + message;
                }
        );
    }
}
