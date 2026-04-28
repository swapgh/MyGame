package com.game.server.systems.npc;

import com.game.server.components.combat.HealthComponent;
import com.game.server.components.loot.LootDropStateComponent;
import com.game.server.components.npc.RespawnComponent;
import com.game.server.components.world.TransformComponent;
import com.game.server.components.world.VelocityComponent;
import com.game.server.ecs.component.ComponentStore;
import com.game.server.ecs.entity.EntityManager;
import com.game.server.ecs.system.GameSystem;
import com.game.shared.math.Vec2;
import com.game.shared.time.GameClock;

import java.util.ArrayList;

/**
 * Restores dead entities once their respawn timer completes.
 *
 * @since 0.1.0
 */
public final class RespawnSystem implements GameSystem {
    @Override
    public void tick(EntityManager entities, GameClock clock) {
        ComponentStore<RespawnComponent> respawns = entities.storeOf(RespawnComponent.class);
        ComponentStore<HealthComponent> healths = entities.storeOf(HealthComponent.class);
        ComponentStore<LootDropStateComponent> lootDropStates = entities.storeOf(LootDropStateComponent.class);
        ComponentStore<TransformComponent> transforms = entities.storeOf(TransformComponent.class);
        ComponentStore<VelocityComponent> velocities = entities.storeOf(VelocityComponent.class);

        for (var entry : new ArrayList<>(respawns.all())) {
            RespawnComponent respawn = entry.getValue();
            if (!respawn.waitingForRespawn() || clock.tick() < respawn.respawnTick()) {
                continue;
            }

            HealthComponent health = healths.get(entry.getKey()).orElse(null);
            if (health == null) {
                continue;
            }

            healths.put(entry.getKey(), new HealthComponent(health.maxHealth(), health.maxHealth()));
            transforms.put(entry.getKey(), new TransformComponent(respawn.spawnPosition()));
            velocities.put(entry.getKey(), new VelocityComponent(Vec2.ZERO));
            if (lootDropStates.has(entry.getKey())) {
                lootDropStates.put(entry.getKey(), new LootDropStateComponent(false));
            }
            respawns.put(
                    entry.getKey(),
                    new RespawnComponent(respawn.spawnPosition(), respawn.respawnDelayTicks(), -1L)
            );
        }
    }
}
