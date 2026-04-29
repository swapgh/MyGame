package com.game.server.components.npc;

/**
 * Basic AI tuning values for NPC behavior.
 *
 * @param moveSpeed the NPC movement speed in world units per second
 * @param aggroRange the maximum distance to chase a player
 * @param roamRadius the distance the NPC may drift from its home position
 * @since 0.1.0
 */
public record AiComponent(
        float moveSpeed,
        float aggroRange,
        float roamRadius
) {
    public AiComponent {
        if (moveSpeed < 0.0f) {
            throw new IllegalArgumentException("moveSpeed cannot be negative");
        }
        if (aggroRange < 0.0f) {
            throw new IllegalArgumentException("aggroRange cannot be negative");
        }
        if (roamRadius < 0.0f) {
            throw new IllegalArgumentException("roamRadius cannot be negative");
        }
    }
}
