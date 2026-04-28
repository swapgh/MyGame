package com.game.client.world;

import com.game.client.components.WorldEntityRenderState;
import com.game.shared.ecs.SharedEntityId;
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

class WorldViewModelTest {
    @Test
    void appliesSnapshotAndRemovesEntitiesMissingFromLaterSnapshots() {
        WorldViewModel viewModel = new WorldViewModel(new SharedEntityId(1L));

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

        assertTrue(viewModel.applySnapshot(firstSnapshot));
        assertEquals(2, viewModel.renderStates().size());

        assertTrue(viewModel.applySnapshot(secondSnapshot));
        assertEquals(1, viewModel.renderStates().size());
    }

    @Test
    void ignoresDuplicateSnapshotTicks() {
        WorldViewModel viewModel = new WorldViewModel(new SharedEntityId(1L));
        WorldSnapshotPacket snapshot = new WorldSnapshotPacket(
                25L,
                new SharedEntityId(1L),
                List.of(new EntitySpawnPacket(new SharedEntityId(1L), new Vec2(40.0f, 50.0f), Vec2.ZERO, EntityType.PLAYER, "DevKnight", 100, 100, true, 0L))
        );

        assertTrue(viewModel.applySnapshot(snapshot));
        assertFalse(viewModel.applySnapshot(snapshot));
    }

    @Test
    void tracksCombatFieldsForRemoteEntities() {
        WorldViewModel viewModel = new WorldViewModel(new SharedEntityId(1L));
        WorldSnapshotPacket snapshot = new WorldSnapshotPacket(
                30L,
                new SharedEntityId(1L),
                List.of(
                        new EntitySpawnPacket(new SharedEntityId(1L), new Vec2(0.0f, 0.0f), Vec2.ZERO, EntityType.PLAYER, "DevKnight", 100, 100, true, 0L),
                        new EntitySpawnPacket(new SharedEntityId(2L), new Vec2(10.0f, 0.0f), Vec2.ZERO, EntityType.NPC, "Training Slime", 0, 100, false, 18L)
                )
        );

        viewModel.applySnapshot(snapshot);

        WorldEntityRenderState remote = viewModel.renderStates().stream()
                .filter(state -> state.entityId() == 2L)
                .findFirst()
                .orElse(null);
        assertNotNull(remote);
        assertEquals(EntityType.NPC, remote.entityType());
        assertFalse(remote.alive());
        assertEquals(18L, remote.respawnTicksRemaining());
        assertEquals(0, remote.currentHealth());
    }
}
