package com.game.client.systems;

import com.game.client.world.WorldViewModel;
import com.game.shared.protocol.world.WorldSnapshotPacket;

/**
 * Applies authoritative snapshots to the client world view.
 *
 * @since 0.1.0
 */
public final class SnapshotApplySystem {
    /**
     * Applies a snapshot to the given view model.
     *
     * @param snapshot the latest snapshot
     * @param viewModel the client world view model
     * @return {@code true} when a new snapshot was applied
     */
    public boolean apply(WorldSnapshotPacket snapshot, WorldViewModel viewModel) {
        return viewModel.applySnapshot(snapshot);
    }
}
