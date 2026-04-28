package com.game.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.input.TextInputBuffer;
import com.game.client.network.AuthClient;
import com.game.client.ui.ScreenManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal character selection screen for the client flow.
 *
 * @since 0.1.0
 */
public final class CharacterSelectScreen extends InputAdapter implements Screen {
    private final GameClient gameClient;
    private final ScreenManager screenManager;
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
     * @param screenManager the screen manager
     * @param accountId the logged-in account id
     * @param sessionToken the issued session token
     * @param characterNames available character names
     */
    public CharacterSelectScreen(
            GameClient gameClient,
            ScreenManager screenManager,
            long accountId,
            String sessionToken,
            List<String> characterNames
    ) {
        this.gameClient = gameClient;
        this.screenManager = screenManager;
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
            screenManager.showLogin();
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
                screenManager.showLoadingWorld(characterNames.get(selectedIndex));
                return;
            }
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        gameClient.spriteBatch().begin();
        gameClient.font().draw(gameClient.spriteBatch(), "Character Select", 80f, 640f);
        gameClient.font().draw(gameClient.spriteBatch(), "Account ID: " + accountId, 80f, 600f);
        gameClient.font().draw(gameClient.spriteBatch(), "Session token loaded: " + (!sessionToken.isBlank()), 80f, 570f);
        gameClient.font().draw(gameClient.spriteBatch(), "New character: " + characterNameBuffer.value(), 80f, 530f);
        gameClient.font().draw(gameClient.spriteBatch(), status, 80f, 500f);

        if (characterNames.isEmpty()) {
            gameClient.font().draw(gameClient.spriteBatch(), "No characters were returned by the auth server yet.", 80f, 440f);
        } else {
            for (int index = 0; index < characterNames.size(); index++) {
                String prefix = index == selectedIndex ? "> " : "  ";
                gameClient.font().draw(gameClient.spriteBatch(), prefix + characterNames.get(index), 100f, 440f - (index * 30f));
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

        Thread createThread = new Thread(() -> {
            try {
                AuthClient.CharacterCreateFlowResult result = gameClient.authClient()
                        .createCharacter(accountId, requestedName);
                Gdx.app.postRunnable(() -> {
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
                });
            } catch (IOException | IllegalArgumentException exception) {
                Gdx.app.postRunnable(() -> {
                    busy = false;
                    status = "Create failed: " + exception.getMessage();
                });
            }
        }, "character-create");
        createThread.setDaemon(true);
        createThread.start();
    }
}
