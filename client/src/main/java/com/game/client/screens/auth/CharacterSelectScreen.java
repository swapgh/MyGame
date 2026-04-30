package com.game.client.screens.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.game.client.app.GameClient;
import com.game.client.ui.theme.UiPalette;
import com.game.client.ui.render.UiRenderState;
import com.game.client.ui.render.UiRenderer;
import com.game.client.screens.Screen;
import com.game.client.screens.ScreenController;
import com.game.client.ui.layouts.UiRect;
import com.game.client.ui.layouts.UiViewportLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal character selection screen for the client flow.
 *
 * @since 0.1.0
 */
public final class CharacterSelectScreen extends InputAdapter implements Screen {
    private static final float PANEL_WIDTH = 1100f;
    private static final float PANEL_HEIGHT = 500f;
    private static final float PANEL_PADDING = 28f;
    private static final float COLUMN_GAP = 28f;
    private static final float LIST_ROW_SPACING = 58f;
    private static final float PREVIEW_WIDTH = 470f;

    private final GameClient gameClient;
    private final ScreenController screenController;
    private final UiRenderer uiRenderer = new UiRenderer();
    private final long accountId;
    private final String sessionToken;
    private final List<String> characterNames;

    private int selectedIndex;
    private boolean blockEnterUntilRelease;

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
        this.accountId = accountId;
        this.sessionToken = sessionToken;
        this.characterNames = new ArrayList<>(characterNames);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        blockEnterUntilRelease = Gdx.input.isKeyPressed(Input.Keys.ENTER);
    }

    /**
     * Renders the character list and handles selection input.
     *
     * @param delta time since the previous frame
     */
    @Override
    public void render(float delta) {
        if (blockEnterUntilRelease && !Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            blockEnterUntilRelease = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            screenController.showLogin();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            screenController.showCharacterCreate(accountId, sessionToken, characterNames);
            return;
        }
        if (!characterNames.isEmpty()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedIndex = (selectedIndex + 1) % characterNames.size();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedIndex = (selectedIndex - 1 + characterNames.size()) % characterNames.size();
            } else if (!blockEnterUntilRelease && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                screenController.showLoadingWorld(characterNames.get(selectedIndex));
                return;
            }
        }

        Gdx.gl.glClearColor(0.03f, 0.06f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameClient.uiCamera().update();
        uiRenderer.renderBackdrop(gameClient, delta + (System.currentTimeMillis() / 1000f));
        float viewportWidth = gameClient.uiCamera().viewportWidth;
        float viewportHeight = gameClient.uiCamera().viewportHeight;
        UiRect panelBounds = UiViewportLayout.centeredPanel(
                viewportWidth,
                viewportHeight,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                72f
        );
        uiRenderer.renderPanel(gameClient, panelBounds.x(), panelBounds.y(), panelBounds.width(), panelBounds.height());

        float leftColumnX = panelBounds.x() + PANEL_PADDING;
        float leftColumnTop = panelBounds.top() - PANEL_PADDING;
        float leftColumnWidth = PREVIEW_WIDTH;
        float rightColumnX = leftColumnX + leftColumnWidth + COLUMN_GAP;
        float rightColumnTop = leftColumnTop;
        float listPanelWidth = panelBounds.width() - (rightColumnX - panelBounds.x()) - PANEL_PADDING;
        float listStartY = rightColumnTop - 70f;
        String selectedCharacter = characterNames.isEmpty()
                ? "No Characters"
                : characterNames.get(Math.max(0, Math.min(selectedIndex, characterNames.size() - 1)));

        gameClient.spriteBatch().setProjectionMatrix(gameClient.uiCamera().combined);
        UiRenderState.beginText(gameClient);
        uiRenderer.renderHeroCentered(
                gameClient,
                "Roster",
                "Character Select",
                "Choose an adventurer from the roster, then enter the world.",
                panelBounds.centerX(),
                panelBounds.top() + 96f
        );
        uiRenderer.renderInfo(gameClient, "Account ID: " + accountId, leftColumnX, leftColumnTop - 20f);
        uiRenderer.renderInfo(gameClient, "Session ready: " + (!sessionToken.isBlank()), leftColumnX, leftColumnTop - 50f);

        float previewY = panelBounds.y() + 64f;
        float previewHeight = 280f;
        uiRenderer.renderPanel(gameClient, leftColumnX, previewY, leftColumnWidth, previewHeight);
        uiRenderer.renderInfo(gameClient, "Selected Adventurer", leftColumnX + 24f, previewY + previewHeight - 28f);
        uiRenderer.renderHeroCentered(
                gameClient,
                "",
                selectedCharacter,
                characterNames.isEmpty()
                        ? "Create a character to begin your journey."
                        : "A simple stand-in preview for now. Full 3D customization can come later.",
                leftColumnX + (leftColumnWidth * 0.5f),
                previewY + previewHeight - 72f
        );
        uiRenderer.renderInfo(gameClient, "Preview Placeholder", leftColumnX + 24f, previewY + 122f);
        uiRenderer.renderInfo(gameClient, "Level 1 Adventurer", leftColumnX + 24f, previewY + 88f);
        uiRenderer.renderInfo(gameClient, "Realm: Dev Test", leftColumnX + 24f, previewY + 58f);

        uiRenderer.renderPanel(gameClient, rightColumnX, panelBounds.y() + 64f, listPanelWidth, 336f);
        uiRenderer.renderInfo(gameClient, "Characters", rightColumnX + 20f, rightColumnTop - 20f);
        uiRenderer.renderInfo(gameClient, "UP/DOWN select", rightColumnX + 20f, panelBounds.y() + 24f);
        uiRenderer.renderInfo(gameClient, "ENTER play   C create new   B back", rightColumnX + 170f, panelBounds.y() + 24f);

        if (characterNames.isEmpty()) {
            uiRenderer.renderInfo(
                    gameClient,
                    "No characters are available yet.",
                    rightColumnX + 24f,
                    listStartY
            );
            uiRenderer.renderInfo(
                    gameClient,
                    "Press C to open the character creation screen.",
                    rightColumnX + 24f,
                    listStartY - 30f
            );
        } else {
            for (int index = 0; index < characterNames.size(); index++) {
                uiRenderer.renderListRow(
                        gameClient,
                        characterNames.get(index),
                        rightColumnX + 24f,
                        listStartY - (index * LIST_ROW_SPACING),
                        index == selectedIndex
                );
            }
        }
        UiRenderState.endText(gameClient);
    }
}
