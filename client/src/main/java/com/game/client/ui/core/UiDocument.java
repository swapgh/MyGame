package com.game.client.ui.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen document used to compose LibGDX screens.
 *
 * @param hero optional heading block
 * @param sections framed content sections
 * @param lines free-floating text lines
 * @since 0.1.0
 */
public record UiDocument(
        UiHero hero,
        List<UiSection> sections,
        List<UiTextLine> lines
) {

    /**
     * Creates a new document builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Mutable builder for screen documents.
     */
    public static final class Builder {
        private UiHero hero;
        private final List<UiSection> sections = new ArrayList<>();
        private final List<UiTextLine> lines = new ArrayList<>();

        private Builder() {
        }

        /**
         * Sets the document hero block.
         *
         * @param hero heading block
         * @return this builder
         */
        public Builder hero(UiHero hero) {
            this.hero = hero;
            return this;
        }

        /**
         * Adds a section document.
         *
         * @param section section node
         * @return this builder
         */
        public Builder section(UiSection section) {
            sections.add(section);
            return this;
        }

        /**
         * Adds a free-floating text line.
         *
         * @param line line node
         * @return this builder
         */
        public Builder line(UiTextLine line) {
            lines.add(line);
            return this;
        }

        /**
         * Builds the immutable document.
         *
         * @return the screen document
         */
        public UiDocument build() {
            return new UiDocument(hero, List.copyOf(sections), List.copyOf(lines));
        }
    }
}
