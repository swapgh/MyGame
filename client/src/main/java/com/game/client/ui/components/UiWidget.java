package com.game.client.ui.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.game.client.app.GameClient;

/**
 * Base interface for all UI widgets.
 *
 * @since 0.1.0
 */
public interface UiWidget {

    void render(GameClient gameClient, SpriteBatch batch);

    boolean handleMouseMove(float mouseX, float mouseY);

    boolean handleMousePressed(float mouseX, float mouseY, int button);

    boolean handleMouseReleased(float mouseX, float mouseY, int button);

    boolean handleKeyTyped(char character);

    boolean handleKeyPressed(int keyCode);

    void update(float delta);

    void setBounds(float x, float y, float width, float height);

    Vector2 getPosition();

    float getWidth();

    float getHeight();

    boolean isFocused();

    void setFocused(boolean focused);

    boolean isHovered();

    void setVisible(boolean visible);

    boolean isVisible();
}
