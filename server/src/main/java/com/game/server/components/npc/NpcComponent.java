package com.game.server.components.npc;

import com.game.shared.protocol.world.EntityType;

/**
 * Marker for NPC entities built from a named definition.
 *
 * @param definitionId the source NPC definition id
 * @param displayName the NPC display name
 * @param entityType replicated NPC entity type such as enemy or vendor
 * @since 0.1.0
 */
public record NpcComponent(String definitionId, String displayName, EntityType entityType) {
    /**
     * Creates an NPC component defaulting to a hostile replicated NPC type.
     *
     * @param definitionId source NPC definition id
     * @param displayName NPC display name
     */
    public NpcComponent(String definitionId, String displayName) {
        this(definitionId, displayName, EntityType.NPC);
    }
}
