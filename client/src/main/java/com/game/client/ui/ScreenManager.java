package com.game.client.ui;

import com.game.client.app.ClientState;
import com.game.client.app.GameClient;
import com.game.client.screens.CharacterSelectScreen;
import com.game.client.screens.ErrorScreen;
import com.game.client.screens.GameScreen;
import com.game.client.screens.LoadingScreen;
import com.game.client.screens.LoginScreen;
import com.game.client.screens.RegisterScreen;
import com.game.client.screens.SettingsScreen;
import com.game.shared.ecs.SharedEntityId;

import java.util.List;

/**
 * Creates and swaps the client screens.
 *
 * @since 0.1.0
 */
public final class ScreenManager {
    private final GameClient gameClient;

    /**
     * Creates a screen manager for the given game client.
     *
     * @param gameClient the owning game client
     */
    public ScreenManager(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    /**
     * Shows the login screen.
     */
    public void showLogin() {
        gameClient.stateMachine().transitionTo(ClientState.LOGIN);
        switchTo(new LoginScreen(gameClient, this));
    }

    /**
     * Shows the register screen.
     */
    public void showRegister() {
        gameClient.stateMachine().transitionTo(ClientState.REGISTER);
        switchTo(new RegisterScreen(gameClient, this));
    }

    /**
     * Shows the character select screen.
     *
     * @param accountId the logged-in account id
     * @param sessionToken the issued auth session token
     * @param characterNames available character names
     */
    public void showCharacterSelect(long accountId, String sessionToken, List<String> characterNames) {
        gameClient.stateMachine().transitionTo(ClientState.CHARACTER_SELECT);
        switchTo(new CharacterSelectScreen(gameClient, this, accountId, sessionToken, characterNames));
    }

    /**
     * Shows the loading screen while world entry is in progress.
     *
     * @param characterName the selected character name
     */
    public void showLoadingWorld(String characterName) {
        gameClient.stateMachine().transitionTo(ClientState.LOADING_WORLD);
        switchTo(new LoadingScreen(gameClient, this, characterName));
    }

    /**
     * Shows the in-world screen.
     *
     * @param characterName the active character name
     */
    public void showGame(String characterName, SharedEntityId playerEntityId) {
        gameClient.stateMachine().transitionTo(ClientState.IN_WORLD);
        switchTo(new GameScreen(gameClient, this, characterName, playerEntityId));
    }

    /**
     * Shows a placeholder settings screen.
     */
    public void showSettings() {
        gameClient.stateMachine().transitionTo(ClientState.SETTINGS);
        switchTo(new SettingsScreen(gameClient, this));
    }

    /**
     * Shows an error screen.
     *
     * @param message the user-facing error message
     */
    public void showError(String message) {
        gameClient.stateMachine().transitionTo(ClientState.ERROR);
        switchTo(new ErrorScreen(gameClient, this, message));
    }

    /**
     * Disposes the active screen if one exists.
     */
    public void dispose() {
        if (gameClient.getScreen() != null) {
            gameClient.getScreen().dispose();
        }
    }

    private void switchTo(com.badlogic.gdx.Screen nextScreen) {
        com.badlogic.gdx.Screen previousScreen = gameClient.getScreen();
        gameClient.setScreen(nextScreen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }
}
