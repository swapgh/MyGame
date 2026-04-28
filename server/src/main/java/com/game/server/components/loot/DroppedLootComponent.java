package com.game.server.components.loot;

/**
 * Marker for world loot dropped from an NPC death.
 *
 * @param itemId the dropped item id
 * @param displayName the user-facing loot label
 * @param sourceNpcId the source NPC definition id
 * @since 0.1.0
 */
public record DroppedLootComponent(String itemId, String displayName, String sourceNpcId) {
}
