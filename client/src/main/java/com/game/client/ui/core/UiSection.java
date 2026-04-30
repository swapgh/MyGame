package com.game.client.ui.core;

import com.game.client.ui.components.UiFormPanel;
import com.game.client.ui.layouts.UiRect;

import java.util.ArrayList;
import java.util.List;

/**
 * Document node describing a framed section and its contents.
 *
 * @param bounds framed section bounds
 * @param formPanel optional interactive form panel
 * @param lines lightweight text nodes rendered with the section
 * @since 0.1.0
 */
public record UiSection(
        UiRect bounds,
        UiFormPanel formPanel,
        List<UiTextLine> lines
) {

    /**
     * Creates a builder for the section node.
     *
     * @param bounds section rectangle
     * @return a new builder
     */
    public static Builder builder(UiRect bounds) {
        return new Builder(bounds);
    }

    /**
     * Mutable builder for section documents.
     */
    public static final class Builder {
        private final UiRect bounds;
        private UiFormPanel formPanel;
        private final List<UiTextLine> lines = new ArrayList<>();

        private Builder(UiRect bounds) {
            this.bounds = bounds;
        }

        /**
         * Attaches an interactive form panel.
         *
         * @param formPanel interactive section form
         * @return this builder
         */
        public Builder form(UiFormPanel formPanel) {
            this.formPanel = formPanel;
            return this;
        }

        /**
         * Adds a text line to the section.
         *
         * @param line line node
         * @return this builder
         */
        public Builder line(UiTextLine line) {
            lines.add(line);
            return this;
        }

        /**
         * Builds the immutable section document.
         *
         * @return the section document
         */
        public UiSection build() {
            return new UiSection(bounds, formPanel, List.copyOf(lines));
        }
    }
}
