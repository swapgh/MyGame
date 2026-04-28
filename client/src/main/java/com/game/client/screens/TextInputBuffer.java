package com.game.client.screens;

/**
 * Small keyboard text buffer for the temporary Phase 4 client UI.
 *
 * @since 0.1.0
 */
public final class TextInputBuffer {
    private final StringBuilder value = new StringBuilder();

    /**
     * Creates a text buffer with an initial value.
     *
     * @param initialValue the initial text
     */
    public TextInputBuffer(String initialValue) {
        if (initialValue != null) {
            value.append(initialValue);
        }
    }

    /**
     * Returns the current text value.
     *
     * @return the current text
     */
    public String value() {
        return value.toString();
    }

    /**
     * Applies a typed printable character.
     *
     * @param character the typed character
     */
    public void append(char character) {
        if (character >= 32 && character != 127 && character != '\r' && character != '\n' && character != '\t') {
            value.append(character);
        }
    }

    /**
     * Removes the last character if the buffer is not empty.
     */
    public void backspace() {
        if (value.length() > 0) {
            value.deleteCharAt(value.length() - 1);
        }
    }

    /**
     * Clears the buffer contents.
     */
    public void clear() {
        value.setLength(0);
    }
}
