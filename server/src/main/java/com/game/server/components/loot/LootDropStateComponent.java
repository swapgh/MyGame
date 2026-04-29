package com.game.server.components.loot;

/**
 * Tracks whether an NPC has already spawned loot for its current life.
 *
 * @param droppedForCurrentLife whether loot has already been spawned
 * @since 0.1.0
 */
public record LootDropStateComponent(boolean droppedForCurrentLife) {
}
