package com.game.client.sync;

import com.game.shared.protocol.world.WorldSnapshotPacket;

/**
 * Applies authoritative snapshots to the client world sync state.
 *
 * @since 0.1.0
 */
public final class SnapshotApplier {
    /**
     * Applies a snapshot to the given view model.
     *
     * @param snapshot the latest snapshot
     * @param worldSyncState the client world sync state
     * @return {@code true} when a new snapshot was applied
     */
    public boolean apply(WorldSnapshotPacket snapshot, WorldSyncState worldSyncState) {
        return worldSyncState.applySnapshot(snapshot);
    }
}
