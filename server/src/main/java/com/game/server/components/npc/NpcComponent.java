package com.game.server.components.npc;

/**
 * Marker for NPC entities built from a named definition.
 *
 * @param definitionId the source NPC definition id
 * @param displayName the NPC display name
 * @since 0.1.0
 */
public record NpcComponent(String definitionId, String displayName) {
}
