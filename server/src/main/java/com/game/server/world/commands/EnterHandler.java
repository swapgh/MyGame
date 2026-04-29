package com.game.server.world.commands;

import com.game.server.world.app.WorldServerMain.WorldApplication;
import com.game.server.components.combat.BaseCombatStatsComponent;
import com.game.server.components.combat.CombatStateComponent;
import com.game.server.components.combat.CombatStatsComponent;
import com.game.server.components.combat.HealthComponent;
import com.game.server.components.inventory.EquipmentComponent;
import com.game.server.components.inventory.InventoryComponent;
import com.game.server.components.loot.DroppedLootComponent;
import com.game.server.components.npc.NpcComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.PlayerComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.components.world.VelocityComponent;
import com.game.server.ecs.entity.EntityId;
import com.game.server.world.network.WorldConnection;
import com.game.server.world.network.WorldPacketHandler;
import com.game.shared.math.Vec2;
import com.game.shared.protocol.core.Packet;
import com.game.shared.protocol.world.EntitySpawnPacket;
import com.game.shared.protocol.world.EntityType;
import com.game.shared.protocol.world.EnterWorldPacket;
import com.game.shared.protocol.world.InventoryUpdatePacket;
import com.game.shared.protocol.world.WorldSnapshotPacket;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.EnumMap;
import java.util.List;

/**
 * Handles world-entry packets and player session bootstrap.
 *
 * @since 0.1.0
 */
public final class EnterHandler implements WorldPacketHandler {
    private final WorldApplication application;

    public EnterHandler(WorldApplication application) {
        this.application = application;
    }

    @Override
    public void handle(WorldConnection connection, Packet packet) throws IOException {
        EnterWorldPacket enterWorldPacket = (EnterWorldPacket) packet;
        SocketAddress remote = connection.socket().getRemoteSocketAddress();
        EntityId entityId = application.connectionManager().findPlayerEntityId(connection.id())
                .orElseGet(() -> spawnPlayer(connection, enterWorldPacket));
        int entityCount = application.worldContext().entityManager().count();
        int zoneCount = application.worldContext().zoneLoader().count();
        int connectionCount = application.connectionManager().count();
        System.out.printf(
                "ENTER_WORLD from %s as %s — %d entities alive, %d zones loaded, %d connection(s), tick=%d%n",
                remote,
                enterWorldPacket.characterName(),
                entityCount,
                zoneCount,
                connectionCount,
                application.gameLoop().clock().tick()
        );
        connection.send(buildSnapshot(entityId));
        sendInventoryUpdate(connection, entityId);
    }

    private EntityId spawnPlayer(WorldConnection connection, EnterWorldPacket packet) {
        EntityId entityId = application.worldContext().entityManager().create();
        float spawnX = 200.0f + (application.connectionManager().count() * 80.0f);
        float spawnY = 200.0f + (application.connectionManager().count() * 40.0f);
        application.worldContext().entityManager().put(entityId, new TransformComponent(new Vec2(spawnX, spawnY)));
        application.worldContext().entityManager().put(entityId, new VelocityComponent(Vec2.ZERO));
        application.worldContext().entityManager().put(entityId, new PlayerComponent(packet.characterName()));
        application.worldContext().entityManager().put(entityId, new HealthComponent(100, 100));
        application.worldContext().entityManager().put(entityId, new BaseCombatStatsComponent(18, 90.0f, 10L));
        application.worldContext().entityManager().put(entityId, new CombatStatsComponent(18, 90.0f, 10L));
        application.worldContext().entityManager().put(entityId, new CombatStateComponent(-100L));
        application.worldContext().entityManager().put(entityId, new InventoryComponent(8, List.of()));
        application.worldContext().entityManager().put(
                entityId,
                new EquipmentComponent(new EnumMap<>(com.game.shared.protocol.world.EquipmentSlot.class))
        );
        application.worldContext().entityManager().put(
                entityId,
                new RespawnComponent(new Vec2(spawnX, spawnY), 60L, -1L)
        );
        application.connectionManager().bindPlayerEntity(connection.id(), entityId);
        return entityId;
    }

    private WorldSnapshotPacket buildSnapshot(EntityId playerEntityId) {
        List<EntitySpawnPacket> entities = application.worldContext().entityManager()
                .storeOf(TransformComponent.class)
                .all()
                .stream()
                .map(entry -> new EntitySpawnPacket(
                        new com.game.shared.ids.SharedEntityId(entry.getKey().value()),
                        entry.getValue().position(),
                        application.worldContext().entityManager()
                                .get(entry.getKey(), VelocityComponent.class)
                                .map(VelocityComponent::velocity)
                                .orElse(Vec2.ZERO),
                        resolveEntityType(entry.getKey()),
                        resolveDisplayName(entry.getKey()),
                        application.worldContext().entityManager()
                                .get(entry.getKey(), HealthComponent.class)
                                .map(HealthComponent::currentHealth)
                                .orElse(1),
                        application.worldContext().entityManager()
                                .get(entry.getKey(), HealthComponent.class)
                                .map(HealthComponent::maxHealth)
                                .orElse(1),
                        application.worldContext().entityManager()
                                .get(entry.getKey(), HealthComponent.class)
                                .map(HealthComponent::alive)
                                .orElse(true),
                        application.worldContext().entityManager()
                                .get(entry.getKey(), RespawnComponent.class)
                                .filter(RespawnComponent::waitingForRespawn)
                                .map(respawn -> Math.max(0L, respawn.respawnTick() - application.gameLoop().clock().tick()))
                                .orElse(0L)
                ))
                .toList();
        return new WorldSnapshotPacket(
                application.gameLoop().clock().tick(),
                new com.game.shared.ids.SharedEntityId(playerEntityId.value()),
                entities
        );
    }

    private EntityType resolveEntityType(EntityId entityId) {
        if (application.worldContext().entityManager().has(entityId, PlayerComponent.class)) {
            return EntityType.PLAYER;
        }
        if (application.worldContext().entityManager().has(entityId, NpcComponent.class)) {
            return EntityType.NPC;
        }
        if (application.worldContext().entityManager().has(entityId, DroppedLootComponent.class)) {
            return EntityType.LOOT;
        }
        return EntityType.UNKNOWN;
    }

    private String resolveDisplayName(EntityId entityId) {
        return application.worldContext().entityManager().get(entityId, PlayerComponent.class)
                .map(PlayerComponent::characterName)
                .or(() -> application.worldContext().entityManager().get(entityId, NpcComponent.class).map(NpcComponent::displayName))
                .or(() -> application.worldContext().entityManager().get(entityId, DroppedLootComponent.class).map(DroppedLootComponent::displayName))
                .orElse("Entity");
    }

    private void sendInventoryUpdate(WorldConnection connection, EntityId entityId) throws IOException {
        InventoryUpdatePacket updatePacket = application.inventoryService()
                .buildInventoryUpdate(application.worldContext().entityManager(), entityId);
        if (updatePacket != null) {
            connection.send(updatePacket);
        }
    }
}
