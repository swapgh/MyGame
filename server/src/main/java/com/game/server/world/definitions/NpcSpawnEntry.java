package com.game.server.world.definitions;

/**
 * One spawn-table entry describing where NPCs should appear.
 *
 * @param zoneId target zone id
 * @param npcId NPC definition id
 * @param count number of NPCs to create
 * @param spawnX base spawn x coordinate
 * @param spawnY base spawn y coordinate
 * @param spacing spacing between spawned NPCs
 * @param respawnDelayTicks respawn delay in ticks
 * @param roamRadius allowed roam radius from home position
 * @since 0.1.0
 */
public record NpcSpawnEntry(
        int zoneId,
        String npcId,
        int count,
        float spawnX,
        float spawnY,
        float spacing,
        long respawnDelayTicks,
        float roamRadius
) {
}
