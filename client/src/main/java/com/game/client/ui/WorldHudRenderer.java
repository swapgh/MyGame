package com.game.client.ui;

import com.badlogic.gdx.graphics.Color;
import com.game.client.app.GameClient;
import com.game.client.components.WorldEntityRenderState;

/**
 * Draws world-screen instructional text and entity labels.
 *
 * @since 0.1.0
 */
public final class WorldHudRenderer {
    /**
     * Draws the static world HUD text.
     *
     * @param gameClient the owning client
     * @param characterName the current character name
     */
    public void renderHeader(GameClient gameClient, String characterName) {
        gameClient.font().draw(gameClient.spriteBatch(), "Game Screen", 80f, 640f);
        gameClient.font().draw(gameClient.spriteBatch(), "Connected as " + characterName, 80f, 600f);
        gameClient.font().draw(gameClient.spriteBatch(), "Use WASD or arrow keys to move.", 80f, 560f);
        gameClient.font().draw(gameClient.spriteBatch(), "Press SPACE to attack the closest target in range.", 80f, 520f);
        gameClient.font().draw(
                gameClient.spriteBatch(),
                "White square is you. Green squares are living targets. Gray squares are dead.",
                80f,
                480f
        );
        gameClient.font().draw(gameClient.spriteBatch(), "Press ESC to disconnect back to login.", 80f, 440f);
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
            Iterable<WorldEntityRenderState> entities,
            long playerEntityId
    ) {
        for (WorldEntityRenderState renderState : entities) {
            String label = renderState.alive()
                    ? renderState.currentHealth() + "/" + renderState.maxHealth() + " HP"
                    : "Respawn " + renderState.respawnTicksRemaining();
            gameClient.font().setColor(renderState.entityId() == playerEntityId ? Color.WHITE : Color.SALMON);
            gameClient.font().draw(
                    gameClient.spriteBatch(),
                    label,
                    renderState.displayPosition().x() - 32.0f,
                    renderState.displayPosition().y() + 36.0f
            );
        }
        gameClient.font().setColor(Color.WHITE);
    }
}
