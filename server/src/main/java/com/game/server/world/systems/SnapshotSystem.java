package com.game.server.world.systems;

import com.game.server.world.components.DroppedLootComponent;
import com.game.server.world.components.HealthComponent;
import com.game.server.world.components.NpcComponent;
import com.game.server.world.components.PlayerComponent;
import com.game.server.world.components.RespawnComponent;
import com.game.server.world.components.TransformComponent;
import com.game.server.world.components.VelocityComponent;
import com.game.server.world.ecs.EntityManager;
import com.game.server.world.ecs.GameSystem;
import com.game.server.world.network.WorldConnection;
import com.game.server.world.network.WorldConnectionManager;
import com.game.shared.ecs.SharedEntityId;
import com.game.shared.protocol.world.EntityType;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;
import com.game.shared.time.GameClock;

import java.io.IOException;
import java.util.List;

/**
 * Broadcasts authoritative world snapshots to connected clients.
 *
 * @since 0.1.0
 */
public final class SnapshotSystem implements GameSystem {
    private final WorldConnectionManager connectionManager;

    /**
     * Creates a snapshot broadcaster for active world connections.
     *
     * @param connectionManager the active connection manager
     */
    public SnapshotSystem(WorldConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Broadcasts a fresh snapshot to each connected client.
     *
     * @param entities the shared entity manager for this tick
     * @param clock the current game clock snapshot
     */
    @Override
    public void tick(EntityManager entities, GameClock clock) {
        var velocities = entities.storeOf(VelocityComponent.class);
        var healths = entities.storeOf(HealthComponent.class);
        var respawns = entities.storeOf(RespawnComponent.class);
        var players = entities.storeOf(PlayerComponent.class);
        var npcs = entities.storeOf(NpcComponent.class);
        var droppedLoot = entities.storeOf(DroppedLootComponent.class);
        List<EntitySpawnPacket> entityStates = entities.storeOf(TransformComponent.class).all().stream()
                .map(entry -> new EntitySpawnPacket(
                        new SharedEntityId(entry.getKey().value()),
                        entry.getValue().position(),
                        velocities.get(entry.getKey())
                                .map(VelocityComponent::velocity)
                                .orElse(com.game.shared.math.Vec2.ZERO),
                        resolveEntityType(entry.getKey(), players, npcs, droppedLoot),
                        resolveDisplayName(entry.getKey(), players, npcs, droppedLoot),
                        healths.get(entry.getKey()).map(HealthComponent::currentHealth).orElse(1),
                        healths.get(entry.getKey()).map(HealthComponent::maxHealth).orElse(1),
                        healths.get(entry.getKey()).map(HealthComponent::alive).orElse(true),
                        respawns.get(entry.getKey())
                                .filter(RespawnComponent::waitingForRespawn)
                                .map(respawn -> Math.max(0L, respawn.respawnTick() - clock.tick()))
                                .orElse(0L)
                ))
                .toList();

        for (WorldConnection connection : connectionManager.all()) {
            SharedEntityId playerEntityId = connectionManager.findPlayerEntityId(connection.id())
                    .map(entityId -> new SharedEntityId(entityId.value()))
                    .orElse(new SharedEntityId(0L));
            try {
                connection.send(new WorldSnapshotPacket(clock.tick(), playerEntityId, entityStates));
            } catch (IOException exception) {
                System.err.printf("Failed to send snapshot to %s: %s%n", connection.id(), exception.getMessage());
                }
        }
    }

    private static EntityType resolveEntityType(
            com.game.server.world.ecs.EntityId entityId,
            com.game.server.world.ecs.ComponentStore<PlayerComponent> players,
            com.game.server.world.ecs.ComponentStore<NpcComponent> npcs,
            com.game.server.world.ecs.ComponentStore<DroppedLootComponent> droppedLoot
    ) {
        if (players.has(entityId)) {
            return EntityType.PLAYER;
        }
        if (npcs.has(entityId)) {
            return EntityType.NPC;
        }
        if (droppedLoot.has(entityId)) {
            return EntityType.LOOT;
        }
        return EntityType.UNKNOWN;
    }

    private static String resolveDisplayName(
            com.game.server.world.ecs.EntityId entityId,
            com.game.server.world.ecs.ComponentStore<PlayerComponent> players,
            com.game.server.world.ecs.ComponentStore<NpcComponent> npcs,
            com.game.server.world.ecs.ComponentStore<DroppedLootComponent> droppedLoot
    ) {
        if (players.has(entityId)) {
            return players.get(entityId).map(PlayerComponent::characterName).orElse("Player");
        }
        if (npcs.has(entityId)) {
            return npcs.get(entityId).map(NpcComponent::displayName).orElse("NPC");
        }
        if (droppedLoot.has(entityId)) {
            return droppedLoot.get(entityId).map(DroppedLootComponent::displayName).orElse("Loot");
        }
        return "Entity";
    }
}
