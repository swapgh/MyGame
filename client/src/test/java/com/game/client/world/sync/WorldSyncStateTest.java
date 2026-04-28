package com.game.client.world.sync;

import com.game.shared.ids.SharedEntityId;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.world.EntityType;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldSyncStateTest {
    @Test
    void appliesSnapshotAndRemovesEntitiesMissingFromLaterSnapshots() {
        WorldSyncState worldSyncState = new WorldSyncState(new SharedEntityId(1L));

        WorldSnapshotPacket firstSnapshot = new WorldSnapshotPacket(
                10L,
                new SharedEntityId(1L),
                List.of(
                        new EntitySpawnPacket(new SharedEntityId(1L), new Vec2(100.0f, 100.0f), Vec2.ZERO, EntityType.PLAYER, "DevKnight", 100, 100, true, 0L),
                        new EntitySpawnPacket(new SharedEntityId(2L), new Vec2(150.0f, 100.0f), Vec2.ZERO, EntityType.NPC, "Training Slime", 80, 100, true, 0L)
                )
        );
        WorldSnapshotPacket secondSnapshot = new WorldSnapshotPacket(
                11L,
                new SharedEntityId(1L),
                List.of(
                        new EntitySpawnPacket(new SharedEntityId(1L), new Vec2(100.0f, 100.0f), Vec2.ZERO, EntityType.PLAYER, "DevKnight", 100, 100, true, 0L)
                )
        );

        assertTrue(worldSyncState.applySnapshot(firstSnapshot));
        assertEquals(2, worldSyncState.entityStates().size());

        assertTrue(worldSyncState.applySnapshot(secondSnapshot));
        assertEquals(1, worldSyncState.entityStates().size());
    }

    @Test
    void ignoresDuplicateSnapshotTicks() {
        WorldSyncState worldSyncState = new WorldSyncState(new SharedEntityId(1L));
        WorldSnapshotPacket snapshot = new WorldSnapshotPacket(
                25L,
                new SharedEntityId(1L),
                List.of(new EntitySpawnPacket(new SharedEntityId(1L), new Vec2(40.0f, 50.0f), Vec2.ZERO, EntityType.PLAYER, "DevKnight", 100, 100, true, 0L))
        );

        assertTrue(worldSyncState.applySnapshot(snapshot));
        assertFalse(worldSyncState.applySnapshot(snapshot));
    }

    @Test
    void tracksCombatFieldsForRemoteEntities() {
        WorldSyncState worldSyncState = new WorldSyncState(new SharedEntityId(1L));
        WorldSnapshotPacket snapshot = new WorldSnapshotPacket(
                30L,
                new SharedEntityId(1L),
                List.of(
                        new EntitySpawnPacket(new SharedEntityId(1L), new Vec2(0.0f, 0.0f), Vec2.ZERO, EntityType.PLAYER, "DevKnight", 100, 100, true, 0L),
                        new EntitySpawnPacket(new SharedEntityId(2L), new Vec2(10.0f, 0.0f), Vec2.ZERO, EntityType.NPC, "Training Slime", 0, 100, false, 18L)
                )
        );

        worldSyncState.applySnapshot(snapshot);

        EntitySyncState remote = worldSyncState.entityStates().stream()
                .filter(entityState -> entityState.entityId() == 2L)
                .findFirst()
                .orElse(null);
        assertNotNull(remote);
        assertEquals(EntityType.NPC, remote.entityType());
        assertFalse(remote.alive());
        assertEquals(18L, remote.respawnTicksRemaining());
        assertEquals(0, remote.currentHealth());
    }
}
