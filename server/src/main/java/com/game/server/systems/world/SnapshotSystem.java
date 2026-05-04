package com.game.server.systems.world;

import com.game.server.components.combat.HealthComponent;
import com.game.server.components.loot.DroppedLootComponent;
import com.game.server.components.npc.NpcComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.PlayerComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.components.world.VelocityComponent;
import com.game.server.ecs.component.ComponentStore;
import com.game.server.ecs.entity.EntityId;
import com.game.server.ecs.entity.EntityManager;
import com.game.server.ecs.system.GameSystem;
import com.game.server.world.network.WorldConnection;
import com.game.server.world.network.WorldConnectionManager;
import com.game.shared.ids.SharedEntityId;
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
            EntityId entityId,
            ComponentStore<PlayerComponent> players,
            ComponentStore<NpcComponent> npcs,
            ComponentStore<DroppedLootComponent> droppedLoot
    ) {
        if (players.has(entityId)) {
            return EntityType.PLAYER;
        }
        if (npcs.has(entityId)) {
            return npcs.get(entityId).map(NpcComponent::entityType).orElse(EntityType.NPC);
        }
        if (droppedLoot.has(entityId)) {
            return EntityType.LOOT;
        }
        return EntityType.UNKNOWN;
    }

    private static String resolveDisplayName(
            EntityId entityId,
            ComponentStore<PlayerComponent> players,
            ComponentStore<NpcComponent> npcs,
            ComponentStore<DroppedLootComponent> droppedLoot
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
