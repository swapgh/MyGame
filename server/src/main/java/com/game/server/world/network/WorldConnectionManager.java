package com.game.server.world.network;

import com.game.server.ecs.entity.EntityId;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks active world-server client connections.
 *
 * @since 0.1.0
 */
public final class WorldConnectionManager {
    private final Map<UUID, WorldConnection> connections = new ConcurrentHashMap<>();
    private final Map<UUID, EntityId> playerEntitiesByConnectionId = new ConcurrentHashMap<>();

    /**
     * Registers a new active connection.
     *
     * @param connection the connection to track
     */
    public void register(WorldConnection connection) {
        connections.put(connection.id(), connection);
    }

    /**
     * Removes an active connection.
     *
     * @param connectionId the connection id to remove
     */
    public void unregister(UUID connectionId) {
        connections.remove(connectionId);
        playerEntitiesByConnectionId.remove(connectionId);
    }

    /**
     * Finds a tracked connection by id.
     *
     * @param connectionId the connection id
     * @return the tracked connection, if present
     */
    public Optional<WorldConnection> find(UUID connectionId) {
        return Optional.ofNullable(connections.get(connectionId));
    }

    /**
     * Returns all tracked active connections.
     *
     * @return an unmodifiable view of active connections
     */
    public Collection<WorldConnection> all() {
        return Collections.unmodifiableCollection(connections.values());
    }

    /**
     * Returns the number of tracked active connections.
     *
     * @return the active connection count
     */
    public int count() {
        return connections.size();
    }

    /**
     * Associates a connection with its player entity.
     *
     * @param connectionId the connection id
     * @param entityId the player entity id
     */
    public void bindPlayerEntity(UUID connectionId, EntityId entityId) {
        playerEntitiesByConnectionId.put(connectionId, entityId);
    }

    /**
     * Finds the player entity for a connection.
     *
     * @param connectionId the connection id
     * @return the player entity id, if present
     */
    public Optional<EntityId> findPlayerEntityId(UUID connectionId) {
        return Optional.ofNullable(playerEntitiesByConnectionId.get(connectionId));
    }

    /**
     * Removes and returns the player entity bound to a connection.
     *
     * @param connectionId the connection id
     * @return the removed player entity id, if present
     */
    public Optional<EntityId> releasePlayerEntityId(UUID connectionId) {
        return Optional.ofNullable(playerEntitiesByConnectionId.remove(connectionId));
    }
}
