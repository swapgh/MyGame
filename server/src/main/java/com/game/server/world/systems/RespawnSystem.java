package com.game.server.world.systems;

import com.game.server.world.components.HealthComponent;
import com.game.server.world.components.RespawnComponent;
import com.game.server.world.components.TransformComponent;
import com.game.server.world.components.VelocityComponent;
import com.game.server.world.ecs.ComponentStore;
import com.game.server.world.ecs.EntityManager;
import com.game.server.world.ecs.GameSystem;
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
            respawns.put(
                    entry.getKey(),
                    new RespawnComponent(respawn.spawnPosition(), respawn.respawnDelayTicks(), -1L)
            );
        }
    }
}
