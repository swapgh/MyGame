package com.game.client.render.overlay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.game.client.app.GameClient;
import com.game.client.model.TargetSelection;
import com.game.client.model.WorldActionContext;
import com.game.client.model.WorldActionType;
import com.game.client.model.WorldFrameState;
import com.game.client.ui.layouts.UiRect;
import com.game.client.ui.render.UiRenderState;
import com.game.client.ui.render.UiRenderer;
import com.game.client.ui.theme.UiPalette;
import com.game.client.world.sync.EntitySyncState;
import com.game.shared.protocol.world.InventoryUpdatePacket;

/**
 * Draws the in-world HUD layer including overlay text, inventory, and labels.
 *
 * @since 0.1.0
 */
public final class WorldHudRenderer {
    private static final float SCREEN_MARGIN = 18f;
    private static final float PANEL_GAP = 14f;
    private static final float HEADER_PANEL_WIDTH = 300f;
    private static final float HEADER_PANEL_HEIGHT = 122f;
    private static final float TARGET_PANEL_WIDTH = 300f;
    private static final float TARGET_PANEL_HEIGHT = 92f;
    private static final float ACTION_PANEL_WIDTH = 360f;
    private static final float ACTION_PANEL_HEIGHT = 56f;
    private static final float INVENTORY_PANEL_WIDTH = 272f;
    private static final float INVENTORY_PANEL_HEIGHT = 154f;
    private static final float EQUIPMENT_PANEL_WIDTH = 272f;
    private static final float EQUIPMENT_PANEL_HEIGHT = 112f;

    private static final float PANEL_INSET_X = 16f;
    private static final float PANEL_HEADER_Y = 22f;
    private static final float PANEL_LINE_SPACING = 20f;
    private static final float PANEL_STATUS_GAP = 24f;
    private static final float INVENTORY_META_GAP = 28f;
    private static final float INVENTORY_LIST_GAP = 52f;
    private static final float INVENTORY_ROW_SPACING = 20f;
    private static final float EQUIPMENT_ROW_SPACING = 20f;
    private static final float PLAYER_BAR_WIDTH = 228f;
    private static final float PLAYER_BAR_HEIGHT = 16f;
    private static final float PLAYER_BAR_GAP = 24f;
    private static final int MAX_INVENTORY_ROWS = 4;
    private static final int MAX_EQUIPMENT_ROWS = 3;

    private static final float ENTITY_LABEL_OFFSET_X = 32f;
    private static final float ENTITY_LABEL_OFFSET_Y = 36f;

    private final UiRenderer uiRenderer = new UiRenderer();

    /**
     * Draws the complete in-world overlay for the current frame.
     *
     * @param gameClient the owning client
     * @param characterName current character name
     * @param entities current visible entities
     * @param playerEntityId local player entity id
     * @param frameState prepared frame state
     */
    public void render(
            GameClient gameClient,
            String characterName,
            Iterable<EntitySyncState> entities,
            long playerEntityId,
            WorldFrameState frameState
    ) {
        float viewportWidth = gameClient.uiCamera().viewportWidth;
        float viewportHeight = gameClient.uiCamera().viewportHeight;

        UiRect headerPanel = topLeftPanel(viewportHeight, HEADER_PANEL_WIDTH, HEADER_PANEL_HEIGHT);
        UiRect targetPanel = topRightPanel(viewportWidth, viewportHeight, TARGET_PANEL_WIDTH, TARGET_PANEL_HEIGHT);
        UiRect inventoryPanel = bottomLeftPanel(INVENTORY_PANEL_WIDTH, INVENTORY_PANEL_HEIGHT);
        UiRect equipmentPanel = stackedPanelAbove(inventoryPanel, EQUIPMENT_PANEL_WIDTH, EQUIPMENT_PANEL_HEIGHT, PANEL_GAP);
        UiRect actionPanel = bottomCenteredPanel(viewportWidth, ACTION_PANEL_WIDTH, ACTION_PANEL_HEIGHT, SCREEN_MARGIN);
        EntitySyncState playerState = findEntity(entities, playerEntityId);

        renderHeader(gameClient, characterName, playerState, headerPanel);
        renderTarget(
                gameClient,
                frameState.currentTarget() == null ? null : findEntity(entities, frameState.currentTarget().entityId()),
                targetPanel
        );
        renderActionContext(gameClient, frameState.actionContext(), frameState.interactionMessage(), actionPanel);
        renderInventory(gameClient, frameState.inventoryUpdate(), inventoryPanel, equipmentPanel);
        renderEntityLabels(
                gameClient,
                entities,
                playerEntityId,
                frameState.currentTarget() == null ? -1L : frameState.currentTarget().entityId()
        );
    }

    private void renderHeader(GameClient gameClient, String characterName, EntitySyncState playerState, UiRect panel) {
        uiRenderer.renderPanel(gameClient, panel.x(), panel.y(), panel.width(), panel.height());
        float titleY = panel.top() - 16f;
        float barX = panel.x() + 54f;
        float barTopY = panel.top() - 38f;
        int currentHealth = playerState == null ? 0 : playerState.currentHealth();
        int maxHealth = playerState == null ? 1 : Math.max(1, playerState.maxHealth());

        UiRenderState.beginText(gameClient);
        GlyphLayout titleLayout = new GlyphLayout(gameClient.uiFont().small, "Level 1");
        gameClient.uiFont().small.setColor(UiPalette.TEXT_PRIMARY);
        gameClient.uiFont().small.draw(gameClient.spriteBatch(), "Level 1", panel.x() + 10f, titleY);
        gameClient.uiFont().small.draw(
                gameClient.spriteBatch(),
                characterName,
                panel.x() + 18f + titleLayout.width,
                titleY
        );

        renderBar(gameClient, barX, barTopY, PLAYER_BAR_WIDTH, PLAYER_BAR_HEIGHT,
                (float) currentHealth / (float) maxHealth, Color.valueOf("AA4341"),
                "HP " + currentHealth + "/" + maxHealth);
        renderBar(gameClient, barX, barTopY - PLAYER_BAR_GAP, PLAYER_BAR_WIDTH, PLAYER_BAR_HEIGHT,
                0f, Color.valueOf("335A8F"), "Mana 0");
        renderBar(gameClient, barX, barTopY - (PLAYER_BAR_GAP * 2f), PLAYER_BAR_WIDTH, PLAYER_BAR_HEIGHT,
                0f, Color.valueOf("6C6C6C"), "XP 0/14");

        gameClient.uiFont().small.setColor(Color.WHITE);
    }

    private void renderTarget(GameClient gameClient, EntitySyncState targetState, UiRect panel) {
        uiRenderer.renderPanel(gameClient, panel.x(), panel.y(), panel.width(), panel.height());
        if (targetState == null) {
            UiRenderState.beginText(gameClient);
            gameClient.uiFont().small.setColor(UiPalette.TEXT_MUTED);
            gameClient.uiFont().small.draw(gameClient.spriteBatch(), "Target", panel.x() + 12f, panel.top() - 14f);
            uiRenderer.renderInfo(
                    gameClient,
                    "No target selected",
                    panel.x() + 56f,
                    panel.top() - 38f
            );
            renderBar(gameClient, panel.x() + 56f, panel.top() - 48f, 224f, PLAYER_BAR_HEIGHT,
                    0f, Color.valueOf("AA4341"), "HP 0/0");
            uiRenderer.renderInfo(gameClient, "TAB to cycle nearby hostiles", panel.x() + 56f, panel.y() + 20f);
            gameClient.uiFont().small.setColor(Color.WHITE);
            return;
        }

        UiRenderState.beginText(gameClient);
        gameClient.uiFont().small.setColor(UiPalette.TEXT_MUTED);
        gameClient.uiFont().small.draw(gameClient.spriteBatch(), "Target", panel.x() + 12f, panel.top() - 14f);
        gameClient.uiFont().small.draw(gameClient.spriteBatch(), "Lv. 1", panel.x() + 12f, panel.top() - 36f);
        uiRenderer.renderStatus(
                gameClient,
                targetState.displayName(),
                panel.x() + 56f,
                panel.top() - 18f,
                targetState.alive() ? UiPalette.TEXT_WARNING : UiPalette.TEXT_MUTED
        );
        renderBar(
                gameClient,
                panel.x() + 56f,
                panel.top() - 48f,
                224f,
                PLAYER_BAR_HEIGHT,
                targetState.maxHealth() <= 0 ? 0f : (float) targetState.currentHealth() / (float) targetState.maxHealth(),
                Color.valueOf("AA4341"),
                "HP " + targetState.currentHealth() + "/" + targetState.maxHealth()
        );
        uiRenderer.renderInfo(
                gameClient,
                targetState.alive()
                        ? "Press E to attack"
                        : "Respawning in " + targetState.respawnTicksRemaining() + " ticks",
                panel.x() + 56f,
                panel.y() + 20f
        );
        gameClient.uiFont().small.setColor(Color.WHITE);
    }

    private void renderActionContext(GameClient gameClient, WorldActionContext actionContext, String interactionMessage, UiRect panel) {
        uiRenderer.renderPanel(gameClient, panel.x(), panel.y(), panel.width(), panel.height());
        float textX = panel.x() + PANEL_INSET_X;
        float topY = panel.top() - PANEL_HEADER_Y;
        uiRenderer.renderInfo(gameClient, compactActionLabel(actionContext.label()), textX, topY);
        if (interactionMessage != null && !interactionMessage.isBlank()) {
            uiRenderer.renderStatus(
                    gameClient,
                    interactionMessage,
                    textX,
                    topY - PANEL_STATUS_GAP,
                    actionContext.actionType() == WorldActionType.INTERACT_VENDOR
                            ? UiPalette.TEXT_SUCCESS
                            : UiPalette.TEXT_MUTED
            );
        }
    }

    private void renderInventory(GameClient gameClient, InventoryUpdatePacket inventoryUpdate, UiRect inventoryPanel, UiRect equipmentPanel) {
        BitmapFont body = gameClient.uiFont().body;
        BitmapFont small = gameClient.uiFont().small;

        uiRenderer.renderPanel(
                gameClient,
                inventoryPanel.x(),
                inventoryPanel.y(),
                inventoryPanel.width(),
                inventoryPanel.height()
        );
        uiRenderer.renderPanel(
                gameClient,
                equipmentPanel.x(),
                equipmentPanel.y(),
                equipmentPanel.width(),
                equipmentPanel.height()
        );
        UiRenderState.beginText(gameClient);

        float inventoryTextX = inventoryPanel.x() + PANEL_INSET_X;
        float inventoryTitleY = inventoryPanel.top() - PANEL_HEADER_Y;
        float inventoryMetaY = inventoryTitleY - INVENTORY_META_GAP;
        float inventoryRowY = inventoryTitleY - INVENTORY_LIST_GAP;

        float equipmentTextX = equipmentPanel.x() + PANEL_INSET_X;
        float equipmentTitleY = equipmentPanel.top() - PANEL_HEADER_Y;
        float equipmentRowY = equipmentTitleY - INVENTORY_META_GAP;

        body.setColor(UiPalette.TEXT_PRIMARY);
        body.draw(gameClient.spriteBatch(), "Inventory", inventoryTextX, inventoryTitleY);

        if (inventoryUpdate == null) {
            small.setColor(UiPalette.TEXT_MUTED);
            small.draw(gameClient.spriteBatch(), "Waiting for inventory sync...", inventoryTextX, inventoryMetaY);
            body.setColor(Color.WHITE);
            small.setColor(Color.WHITE);
            return;
        }

        small.setColor(UiPalette.TEXT_MUTED);
        small.draw(gameClient.spriteBatch(),
                "Slots: " + inventoryUpdate.inventoryItems().size() + "/" + inventoryUpdate.capacity(),
                inventoryTextX, inventoryMetaY);

        float rowY = inventoryRowY;
        int visibleInventoryRows = Math.min(inventoryUpdate.inventoryItems().size(), MAX_INVENTORY_ROWS);
        for (int index = 0; index < visibleInventoryRows; index++) {
            var item = inventoryUpdate.inventoryItems().get(index);
            String itemLabel = (item.slotIndex() + 1) + ". "
                    + item.displayName() + " x" + item.quantity();
            if (item.equippable() && item.equipmentSlot() != null) {
                itemLabel += " [" + item.equipmentSlot().name() + "]";
            }
            small.setColor(item.equippable() ? UiPalette.TEXT_ACCENT : UiPalette.TEXT_PRIMARY);
            small.draw(gameClient.spriteBatch(), itemLabel, inventoryTextX, rowY);
            rowY -= INVENTORY_ROW_SPACING;
        }
        if (inventoryUpdate.inventoryItems().isEmpty()) {
            small.setColor(UiPalette.TEXT_MUTED);
            small.draw(gameClient.spriteBatch(), "Empty", inventoryTextX, rowY);
        } else if (inventoryUpdate.inventoryItems().size() > MAX_INVENTORY_ROWS) {
            small.setColor(UiPalette.TEXT_MUTED);
            small.draw(
                    gameClient.spriteBatch(),
                    "+" + (inventoryUpdate.inventoryItems().size() - MAX_INVENTORY_ROWS) + " more items",
                    inventoryTextX,
                    rowY
            );
        }

        body.setColor(UiPalette.TEXT_PRIMARY);
        body.draw(gameClient.spriteBatch(), "Equipped", equipmentTextX, equipmentTitleY);

        float equipmentY = equipmentRowY;
        int visibleEquipmentRows = Math.min(inventoryUpdate.equippedItems().size(), MAX_EQUIPMENT_ROWS);
        for (int index = 0; index < visibleEquipmentRows; index++) {
            var equippedItem = inventoryUpdate.equippedItems().get(index);
            small.setColor(UiPalette.TEXT_SUCCESS);
            small.draw(gameClient.spriteBatch(),
                    equippedItem.equipmentSlot().name() + ": " + equippedItem.displayName(),
                    equipmentTextX,
                    equipmentY);
            equipmentY -= EQUIPMENT_ROW_SPACING;
        }
        if (inventoryUpdate.equippedItems().isEmpty()) {
            small.setColor(UiPalette.TEXT_MUTED);
            small.draw(gameClient.spriteBatch(), "Nothing equipped", equipmentTextX, equipmentY);
        } else if (inventoryUpdate.equippedItems().size() > MAX_EQUIPMENT_ROWS) {
            small.setColor(UiPalette.TEXT_MUTED);
            small.draw(
                    gameClient.spriteBatch(),
                    "+" + (inventoryUpdate.equippedItems().size() - MAX_EQUIPMENT_ROWS) + " more equipped",
                    equipmentTextX,
                    equipmentY
            );
        }

        body.setColor(Color.WHITE);
        small.setColor(Color.WHITE);
    }

    private void renderBar(
            GameClient gameClient,
            float x,
            float y,
            float width,
            float height,
            float fillRatio,
            Color fillColor,
            String label
    ) {
        float clampedRatio = Math.max(0f, Math.min(1f, fillRatio));

        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Filled);
        ShapeRenderer shapeRenderer = gameClient.shapeRenderer();
        shapeRenderer.setColor(new Color(0.05f, 0.07f, 0.10f, 0.96f));
        shapeRenderer.rect(x, y - height, width, height);
        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(x + 2f, y - height + 2f, (width - 4f) * clampedRatio, height - 4f);
        UiRenderState.endShapes(gameClient);

        UiRenderState.beginShapes(gameClient, ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(UiPalette.PANEL_BORDER.r, UiPalette.PANEL_BORDER.g, UiPalette.PANEL_BORDER.b, 0.55f));
        shapeRenderer.rect(x, y - height, width, height);
        UiRenderState.endShapes(gameClient);

        UiRenderState.beginText(gameClient);
        GlyphLayout layout = new GlyphLayout(gameClient.uiFont().small, label);
        gameClient.uiFont().small.setColor(UiPalette.TEXT_PRIMARY);
        gameClient.uiFont().small.draw(
                gameClient.spriteBatch(),
                label,
                x + ((width - layout.width) * 0.5f),
                y - 2f
        );
    }

    private void renderEntityLabels(GameClient gameClient,
                                    Iterable<EntitySyncState> entities,
                                    long playerEntityId,
                                    long selectedTargetEntityId) {
        BitmapFont body = gameClient.uiFont().body;
        for (EntitySyncState entityState : entities) {
            String label = switch (entityState.entityType()) {
                case LOOT -> "Loot: " + entityState.displayName();
                case ENEMY, NPC, PLAYER, VENDOR -> entityState.alive()
                        ? entityState.displayName()
                        + " " + entityState.currentHealth()
                        + "/" + entityState.maxHealth() + " HP"
                        : entityState.displayName()
                        + " respawn " + entityState.respawnTicksRemaining();
                default -> entityState.displayName();
            };
            if (entityState.entityId() == playerEntityId) {
                body.setColor(UiPalette.TEXT_PRIMARY);
            } else if (entityState.entityId() == selectedTargetEntityId) {
                body.setColor(UiPalette.TEXT_WARNING);
            } else {
                body.setColor(UiPalette.TEXT_ACCENT);
            }
            body.draw(
                    gameClient.spriteBatch(),
                    label,
                    entityState.displayPosition().x() - ENTITY_LABEL_OFFSET_X,
                    entityState.displayPosition().y() + ENTITY_LABEL_OFFSET_Y
            );
        }
        body.setColor(Color.WHITE);
    }

    private static EntitySyncState findEntity(Iterable<EntitySyncState> entities, long entityId) {
        for (EntitySyncState entity : entities) {
            if (entity.entityId() == entityId) {
                return entity;
            }
        }
        return null;
    }

    private static UiRect topLeftPanel(float viewportHeight, float width, float height) {
        return new UiRect(SCREEN_MARGIN, viewportHeight - SCREEN_MARGIN - height, width, height);
    }

    private static UiRect topRightPanel(float viewportWidth, float viewportHeight, float width, float height) {
        return new UiRect(viewportWidth - SCREEN_MARGIN - width, viewportHeight - SCREEN_MARGIN - height, width, height);
    }

    private static UiRect bottomLeftPanel(float width, float height) {
        return new UiRect(SCREEN_MARGIN, SCREEN_MARGIN, width, height);
    }

    private static UiRect bottomRightPanel(float viewportWidth, float width, float height) {
        return new UiRect(viewportWidth - SCREEN_MARGIN - width, SCREEN_MARGIN, width, height);
    }

    private static UiRect bottomCenteredPanel(float viewportWidth, float width, float height, float minimumY) {
        return new UiRect((viewportWidth - width) * 0.5f, minimumY, width, height);
    }

    private static UiRect stackedPanelAbove(UiRect anchorPanel, float width, float height, float gap) {
        return new UiRect(anchorPanel.x(), anchorPanel.top() + gap, width, height);
    }

    private static String compactActionLabel(String label) {
        if (label == null || label.isBlank()) {
            return "Explore the area";
        }
        return label
                .replace("interact with", "talk to")
                .replace("attack", "attack")
                .trim();
    }
}
