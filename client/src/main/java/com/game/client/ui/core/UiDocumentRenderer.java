package com.game.client.ui.core;

import com.badlogic.gdx.graphics.Color;
import com.game.client.app.GameClient;
import com.game.client.ui.theme.UiPalette;
import com.game.client.ui.render.UiRenderer;

/**
 * Renders screen documents using the existing client renderer.
 *
 * @since 0.1.0
 */
public final class UiDocumentRenderer {

    /**
     * Renders the provided screen document.
     *
     * @param gameClient owning client
     * @param uiRenderer shared UI renderer
     * @param document screen document
     */
    public void render(GameClient gameClient, UiRenderer uiRenderer, UiDocument document) {
        if (document.hero() != null) {
            renderHero(gameClient, uiRenderer, document.hero());
        }
        for (UiSection section : document.sections()) {
            uiRenderer.renderPanel(
                    gameClient,
                    section.bounds().x(),
                    section.bounds().y(),
                    section.bounds().width(),
                    section.bounds().height()
            );
            if (section.formPanel() != null) {
                section.formPanel().render(gameClient, gameClient.spriteBatch());
            }
            renderLines(gameClient, uiRenderer, section.lines());
        }
        renderLines(gameClient, uiRenderer, document.lines());
    }

    private void renderHero(GameClient gameClient, UiRenderer uiRenderer, UiHero hero) {
        if (hero.centered()) {
            uiRenderer.renderHeroCentered(
                    gameClient,
                    hero.eyebrow(),
                    hero.title(),
                    hero.subtitle(),
                    hero.anchorX(),
                    hero.anchorY()
            );
            return;
        }
        uiRenderer.renderHero(
                gameClient,
                hero.eyebrow(),
                hero.title(),
                hero.subtitle()
        );
    }

    private void renderLines(GameClient gameClient, UiRenderer uiRenderer, Iterable<UiTextLine> lines) {
        for (UiTextLine line : lines) {
            if (line.style() == UiTextStyle.STATUS) {
                Color color = line.color() == null ? UiPalette.TEXT_MUTED : line.color();
                uiRenderer.renderStatus(gameClient, line.text(), line.x(), line.y(), color);
            } else if (line.style() == UiTextStyle.LIST_ROW) {
                uiRenderer.renderListRow(gameClient, line.text(), line.x(), line.y(), line.selected());
            } else {
                uiRenderer.renderInfo(gameClient, line.text(), line.x(), line.y());
            }
        }
    }
}
