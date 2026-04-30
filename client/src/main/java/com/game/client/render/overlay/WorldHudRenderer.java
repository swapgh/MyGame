package com.game.client.render.overlay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private static final float SCREEN_MARGIN = 28f;
    private static final float HEADER_PANEL_WIDTH = 360f;
    private static final float HEADER_PANEL_HEIGHT = 150f;
    private static final float TARGET_PANEL_WIDTH = 340f;
    private static final float TARGET_PANEL_HEIGHT = 116f;
    private static final float ACTION_PANEL_WIDTH = 420f;
    private static final float ACTION_PANEL_HEIGHT = 86f;
    private static final float INVENTORY_PANEL_WIDTH = 380f;
    private static final float INVENTORY_PANEL_HEIGHT = 212f;
    private static final float EQUIPMENT_PANEL_WIDTH = 300f;
    private static final float EQUIPMENT_PANEL_HEIGHT = 212f;

    private static final float PANEL_INSET_X = 20f;
    private static final float PANEL_HEADER_Y = 28f;
    private static final float PANEL_LINE_SPACING = 28f;
    private static final float PANEL_STATUS_GAP = 34f;
    private static final float INVENTORY_META_GAP = 40f;
    private static final float INVENTORY_LIST_GAP = 80f;
    private static final float INVENTORY_ROW_SPACING = 32f;
    private static final float EQUIPMENT_ROW_SPACING = 32f;

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
        UiRect equipmentPanel = bottomRightPanel(viewportWidth, EQUIPMENT_PANEL_WIDTH, EQUIPMENT_PANEL_HEIGHT);
        UiRect actionPanel = bottomCenteredPanel(viewportWidth, ACTION_PANEL_WIDTH, ACTION_PANEL_HEIGHT, inventoryPanel.top() + 16f);

        renderHeader(gameClient, characterName, headerPanel);
        renderTarget(gameClient, frameState.currentTarget(), targetPanel);
        renderTargetDetails(
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

    private void renderHeader(GameClient gameClient, String characterName, UiRect panel) {
        uiRenderer.renderPanel(gameClient, panel.x(), panel.y(), panel.width(), panel.height());
        float textX = panel.x() + PANEL_INSET_X;
        float topY = panel.top() - PANEL_HEADER_Y;
        uiRenderer.renderStatus(gameClient, "Connected as " + characterName, textX, topY, UiPalette.TEXT_PRIMARY);
        uiRenderer.renderInfo(gameClient, "WASD or arrows move", textX, topY - PANEL_LINE_SPACING);
        uiRenderer.renderInfo(gameClient, "TAB cycles hostile targets", textX, topY - (PANEL_LINE_SPACING * 2f));
        uiRenderer.renderInfo(gameClient, "E attacks or interacts", textX, topY - (PANEL_LINE_SPACING * 3f));
        uiRenderer.renderInfo(gameClient, "F loots  1-8 equips  ESC exits", textX, topY - (PANEL_LINE_SPACING * 4f));
    }

    private void renderTarget(GameClient gameClient, TargetSelection currentTarget, UiRect panel) {
        uiRenderer.renderPanel(gameClient, panel.x(), panel.y(), panel.width(), panel.height());
        String targetText = currentTarget == null
                ? "Target: none"
                : "Target: " + currentTarget.displayName();
        uiRenderer.renderStatus(
                gameClient,
                targetText,
                panel.x() + PANEL_INSET_X,
                panel.top() - PANEL_HEADER_Y,
                currentTarget == null ? UiPalette.TEXT_MUTED : UiPalette.TEXT_WARNING
        );
    }

    private void renderTargetDetails(GameClient gameClient, EntitySyncState targetState, UiRect panel) {
        if (targetState == null) {
            uiRenderer.renderInfo(
                    gameClient,
                    "No hostile target selected",
                    panel.x() + PANEL_INSET_X,
                    panel.top() - PANEL_HEADER_Y - PANEL_STATUS_GAP
            );
            return;
        }

        String healthLine = targetState.alive()
                ? "Health: " + targetState.currentHealth() + "/" + targetState.maxHealth()
                : "Respawning in " + targetState.respawnTicksRemaining() + " ticks";
        uiRenderer.renderStatus(
                gameClient,
                healthLine,
                panel.x() + PANEL_INSET_X,
                panel.top() - PANEL_HEADER_Y - PANEL_STATUS_GAP,
                targetState.alive() ? UiPalette.TEXT_DANGER : UiPalette.TEXT_MUTED
        );
    }

    private void renderActionContext(GameClient gameClient, WorldActionContext actionContext, String interactionMessage, UiRect panel) {
        uiRenderer.renderPanel(gameClient, panel.x(), panel.y(), panel.width(), panel.height());
        float textX = panel.x() + PANEL_INSET_X;
        float topY = panel.top() - PANEL_HEADER_Y;
        uiRenderer.renderInfo(gameClient, actionContext.label(), textX, topY);
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
        for (var item : inventoryUpdate.inventoryItems()) {
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
        }

        body.setColor(UiPalette.TEXT_PRIMARY);
        body.draw(gameClient.spriteBatch(), "Equipped", equipmentTextX, equipmentTitleY);

        float equipmentY = equipmentRowY;
        for (var equippedItem : inventoryUpdate.equippedItems()) {
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
        }

        body.setColor(Color.WHITE);
        small.setColor(Color.WHITE);
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
}
