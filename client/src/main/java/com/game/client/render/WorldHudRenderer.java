package com.game.client.render;

import com.badlogic.gdx.graphics.Color;
import com.game.client.app.GameClient;
import com.game.client.sync.EntitySyncState;
import com.game.shared.protocol.world.InventoryUpdatePacket;

/**
 * Draws world-screen instructional text and entity labels.
 *
 * @since 0.1.0
 */
public final class WorldHudRenderer {
    private final ClientUiRenderer uiRenderer = new ClientUiRenderer();

    /**
     * Draws the static world HUD text.
     *
     * @param gameClient the owning client
     * @param characterName the current character name
     */
    public void renderHeader(GameClient gameClient, String characterName) {
        uiRenderer.renderHero(gameClient, "World", "Live Session", "Connected as " + characterName);
        uiRenderer.renderInfo(gameClient, "WASD or arrows move", 96f, 500f);
        uiRenderer.renderInfo(gameClient, "SPACE attacks the closest target in range", 96f, 470f);
        uiRenderer.renderInfo(gameClient, "E picks up loot, 1-8 equips matching inventory slots", 96f, 440f);
        uiRenderer.renderInfo(gameClient, "ESC disconnects back to login", 96f, 410f);
    }

    /**
     * Draws entity status labels above the world markers.
     *
     * @param gameClient the owning client
     * @param entities the visible entities
     * @param playerEntityId the local player entity id
     */
    public void renderEntityLabels(
            GameClient gameClient,
            Iterable<EntitySyncState> entities,
            long playerEntityId
    ) {
        for (EntitySyncState entityState : entities) {
            String label = switch (entityState.entityType()) {
                case LOOT -> "Loot: " + entityState.displayName();
                case NPC, PLAYER -> entityState.alive()
                        ? entityState.displayName() + " " + entityState.currentHealth() + "/" + entityState.maxHealth() + " HP"
                        : entityState.displayName() + " respawn " + entityState.respawnTicksRemaining();
                default -> entityState.displayName();
            };
            gameClient.font().setColor(entityState.entityId() == playerEntityId
                    ? ClientUiPalette.TEXT_PRIMARY
                    : ClientUiPalette.TEXT_ACCENT);
            gameClient.font().draw(
                    gameClient.spriteBatch(),
                    label,
                    entityState.displayPosition().x() - 32.0f,
                    entityState.displayPosition().y() + 36.0f
            );
        }
        gameClient.font().setColor(Color.WHITE);
    }

    /**
     * Draws the latest inventory and equipment state.
     *
     * @param gameClient the owning client
     * @param inventoryUpdate the latest inventory update, if present
     */
    public void renderInventory(GameClient gameClient, InventoryUpdatePacket inventoryUpdate) {
        float startX = 80f;
        float startY = 330f;
        uiRenderer.renderPanel(gameClient, startX - 20f, 54f, 500f, 254f);
        uiRenderer.renderPanel(gameClient, 600f, 54f, 320f, 254f);
        gameClient.font().setColor(ClientUiPalette.TEXT_PRIMARY);
        gameClient.font().draw(gameClient.spriteBatch(), "Inventory", startX, startY);
        if (inventoryUpdate == null) {
            gameClient.font().setColor(ClientUiPalette.TEXT_MUTED);
            gameClient.font().draw(gameClient.spriteBatch(), "Waiting for inventory sync...", startX, startY - 40f);
            return;
        }

        gameClient.font().setColor(ClientUiPalette.TEXT_MUTED);
        gameClient.font().draw(
                gameClient.spriteBatch(),
                "Slots: " + inventoryUpdate.inventoryItems().size() + "/" + inventoryUpdate.capacity(),
                startX,
                startY - 40f
        );
        float rowY = startY - 80f;
        for (var item : inventoryUpdate.inventoryItems()) {
            String itemLabel = (item.slotIndex() + 1) + ". " + item.displayName() + " x" + item.quantity();
            if (item.equippable() && item.equipmentSlot() != null) {
                itemLabel += " [" + item.equipmentSlot().name() + "]";
            }
            gameClient.font().setColor(item.equippable() ? ClientUiPalette.TEXT_ACCENT : ClientUiPalette.TEXT_PRIMARY);
            gameClient.font().draw(gameClient.spriteBatch(), itemLabel, startX, rowY);
            rowY -= 32f;
        }
        if (inventoryUpdate.inventoryItems().isEmpty()) {
            gameClient.font().setColor(ClientUiPalette.TEXT_MUTED);
            gameClient.font().draw(gameClient.spriteBatch(), "Empty", startX, rowY);
        }

        float equipmentX = 620f;
        gameClient.font().setColor(ClientUiPalette.TEXT_PRIMARY);
        gameClient.font().draw(gameClient.spriteBatch(), "Equipped", equipmentX, startY);
        float equipmentY = startY - 40f;
        for (var equippedItem : inventoryUpdate.equippedItems()) {
            gameClient.font().setColor(ClientUiPalette.TEXT_SUCCESS);
            gameClient.font().draw(
                    gameClient.spriteBatch(),
                    equippedItem.equipmentSlot().name() + ": " + equippedItem.displayName(),
                    equipmentX,
                    equipmentY
            );
            equipmentY -= 32f;
        }
        if (inventoryUpdate.equippedItems().isEmpty()) {
            gameClient.font().setColor(ClientUiPalette.TEXT_MUTED);
            gameClient.font().draw(gameClient.spriteBatch(), "Nothing equipped", equipmentX, equipmentY);
        }
        gameClient.font().setColor(Color.WHITE);
    }
}
